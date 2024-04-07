package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Source;

import java.util.List;

public class Landcover implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith("PLOTAI") && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");
            var area = sf.getString("SHAPE_Area");

            switch (code) {
                case "hd6" -> addPolygon("wetland", "wetland", 11, features);
                case "sd2" -> addPolygon("farmland", "meadow", 12, features);
                case "sd11" -> addPolygon("farmland", "farmland", 11, features);
                case "ms4" -> addPolygon("farmland", "orchard", 12, features);
                case "sd42" -> addPolygon("sand", null, 8, features);
                case "ms0" -> addPolygon("forest", "forest", 5, features);
                case "mj0", "sd15" -> addPolygon("forest", "forest", 13, features);
            }
        }
    }


    public void addPolygon(String clazz, String subclass, int minZoom, FeatureCollector features) {
        features.polygon(this.name())
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
