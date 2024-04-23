package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.ZoomFunction;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

import java.util.List;
import java.util.Map;

public class Landuse implements OpenMapTilesSchema.Landuse, ForwardingProfile.FeaturePostProcessor {

    private static final ZoomFunction<Number> MIN_PIXEL_SIZE_THRESHOLDS = ZoomFunction.fromMaxZoomThresholds(Map.of(
            13, 4,
            7, 2,
            6, 1
    ));

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith(Layers.GRPK_PLOTAI_PREFIX) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "pu0" -> addPolygon(FieldValues.CLASS_RESIDENTIAL, 5, features);
                case "pu3" -> addPolygon(FieldValues.CLASS_INDUSTRIAL, 5, features);
                case "vp1" -> addPolygon(FieldValues.CLASS_CEMETERY, 10, features);
                case "ek0" -> addPolygon(FieldValues.CLASS_QUARRY, 10, features);
                case "vk1" -> addPolygon(FieldValues.CLASS_STADIUM, 10, features);
                case "gt17", "gt18", "gt19" -> addPolygon(FieldValues.CLASS_RAILWAY, 10, features);
            }
        }
    }


    public void addPolygon(String clazz, int minZoom, FeatureCollector features) {
        features.polygon(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setMinPixelSizeOverrides(MIN_PIXEL_SIZE_THRESHOLDS)
                // Optimization from Planetiler-OpenMapTiles
                // default is 0.1, this helps reduce size of some heavy z5-10 tiles
                .setPixelToleranceBelowZoom(10, 0.25)
                .setAttr(Fields.CLASS, clazz)
                .setMinZoom(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14) {
            return FeatureMerge.mergeMultiPolygon(items);
        }

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }
}
