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

import java.util.List;
import java.util.Map;

public class Landcover implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    public static final ZoomFunction<Number> MIN_PIXEL_SIZE_THRESHOLDS = ZoomFunction.fromMaxZoomThresholds(Map.of(
            13, 8,
            10, 4,
            9, 2
    ));

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "sd2" -> addPolygon("farmland", "meadow", 8, features);
                case "ms4" -> addPolygon("farmland", "orchard", 12, features);
                case "hd6" -> addPolygon("wetland", "wetland", 8, features);
                case "sd42" -> addPolygon("sand", "sand", 8, features);
                case "ms0" -> addPolygon("wood", "forest", 5, features);
                case "mj0", "sd15" -> addPolygon("forest", "forest", 12, features);
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
        return "landcover";
    }
}
