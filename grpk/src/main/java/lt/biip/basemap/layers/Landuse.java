package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.ZoomFunction;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;

import java.util.List;
import java.util.Map;

public class Landuse implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    private static final ZoomFunction<Number> MIN_PIXEL_SIZE_THRESHOLDS = ZoomFunction.fromMaxZoomThresholds(Map.of(
            13, 4,
            7, 2,
            6, 1
    ));

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "pu0" -> addPolygon("residential", "residential", 5, features);
                case "pu3" -> addPolygon("industrial", "industrial", 5, features);
                case "vp1" -> addPolygon("cemetery", "cemetery", 10, features);
                case "ek0" -> addPolygon("quarry", "quarry", 10, features);
                case "vg3" -> addPolygon("landfill", "landfill", 10, features);
                case "vk1" -> addPolygon("stadium", "stadium", 10, features);
                case "gt17", "gt18", "gt19" -> addPolygon("railway", "railway", 10, features);
            }
        }
    }


    public void addPolygon(String clazz, String subclass, int minZoom, FeatureCollector features) {
        features.polygon(this.name())
                .setBufferPixels(4)
                .setMinPixelSizeOverrides(MIN_PIXEL_SIZE_THRESHOLDS)
                // Optimization from Planetiler-OpenMapTiles
                // default is 0.1, this helps reduce size of some heavy z5-10 tiles
                .setPixelToleranceBelowZoom(10, 0.25)
                .setAttr("class", clazz)
                .setAttr("subclass", subclass)
                .setMinZoom(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14) {
            return items;
        }

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }

    @Override
    public String name() {
        return "landuse";
    }
}
