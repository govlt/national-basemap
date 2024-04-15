package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.ZoomFunction;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

import java.util.List;
import java.util.Map;

public class Landcover implements OpenMapTilesSchema.Landcover, ForwardingProfile.FeaturePostProcessor {

    public static final ZoomFunction<Number> MIN_PIXEL_SIZE_THRESHOLDS = ZoomFunction.fromMaxZoomThresholds(Map.of(
            13, 8,
            10, 4,
            9, 2
    ));

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && (sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) || sf.getSourceLayer().equals(Layer.GRPK_VIETOV_P)) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "sd2" -> addPolygon(FieldValues.CLASS_GRASS, FieldValues.SUBCLASS_MEADOW, 10, features);
                case "uur14" -> addPolygon(FieldValues.CLASS_GRASS, FieldValues.SUBCLASS_PARK, 8, features);
                case "ms4" -> addPolygon(FieldValues.CLASS_FARMLAND, FieldValues.SUBCLASS_ORCHARD, 12, features);
                case "hd6" -> addPolygon(FieldValues.CLASS_WETLAND, FieldValues.SUBCLASS_WETLAND, 8, features);
                case "sd42" -> addPolygon(FieldValues.CLASS_SAND, FieldValues.SUBCLASS_SAND, 8, features);
                case "ms0" -> addPolygon(FieldValues.CLASS_WOOD, FieldValues.SUBCLASS_FOREST, 5, features);
                case "mj0", "sd15" -> addPolygon(FieldValues.CLASS_WOOD, FieldValues.SUBCLASS_FOREST, 10, features);
            }
        }
    }


    public void addPolygon(String clazz, String subclass, int minZoom, FeatureCollector features) {
        features.polygon(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setMinPixelSizeOverrides(MIN_PIXEL_SIZE_THRESHOLDS)
                // Optimization from Planetiler-OpenMapTiles
                // default is 0.1, this helps reduce size of some heavy z5-10 tiles
                .setPixelToleranceBelowZoom(10, 0.25)
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.SUBCLASS, subclass)
                .setMinZoom(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14) {
            return items;
        }

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }
}
