package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;

import java.util.List;

public class Landcover implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");
            var area = sf.getLong("SHAPE_Area");

            if (code.equals("hd6") && area > 2000000) {
                addPolygon("wetland", "wetland", 8, features);
            } else if (code.equals("hd6") && area > 1500000) {
                addPolygon("wetland", "wetland", 9, features);
            } else if (code.equals("hd6") && area > 1000000) {
                addPolygon("wetland", "wetland", 10, features);
            } else if (code.equals("hd6") && area > 500000) {
                addPolygon("wetland", "wetland", 11, features);
            } else if (code.equals("hd6")) {
                addPolygon("wetland", "wetland", 12, features);
            }

            else if (code.equals("sd2") && area > 2000000) {
                addPolygon("farmland", "meadow", 8, features);
            } else if (code.equals("sd2") && area > 1500000) {
                addPolygon("farmland", "meadow", 9, features);
            } else if (code.equals("sd2") && area > 1000000) {
                addPolygon("farmland", "meadow", 10, features);
            } else if (code.equals("sd2") && area > 500000) {
                addPolygon("farmland", "meadow", 11, features);
            } else if (code.equals("sd2")) {
                addPolygon("farmland", "meadow", 12, features);
            }

            else if (code.equals("ms4")) {
                addPolygon("farmland", "orchard", 12, features);
            }

            else if (code.equals("sd42") && area > 2000000) {
                addPolygon("sand", "sand", 8, features);
            } else if (code.equals("sd42") && area > 1500000) {
                addPolygon("sand", "sand", 9, features);
            } else if (code.equals("sd42") && area > 1000000) {
                addPolygon("sand", "sand", 10, features);
            } else if (code.equals("sd42") && area > 500000) {
                addPolygon("sand", "sand", 11, features);
            } else if (code.equals("sd42")) {
                addPolygon("sand", "sand", 12, features);
            }

            else if (code.equals("ms0") && area > 3000000) {
                addPolygon("wood", "forest", 5, features);
            } else if (code.equals("ms0") && area > 2500000) {
                addPolygon("wood", "forest", 7, features);
            } else if (code.equals("ms0") && area > 2000000) {
                addPolygon("wood", "forest", 8, features);
            } else if (code.equals("ms0") && area > 1500000) {
                addPolygon("wood", "forest", 9, features);
            } else if (code.equals("ms0") && area > 1000000) {
                addPolygon("wood", "forest", 10, features);
            } else if (code.equals("ms0") && area > 500000) {
                addPolygon("wood", "forest", 11, features);
            } else if (code.equals("ms0")) {
                addPolygon("wood", "forest", 12, features);
            }

            else if (code.equals("mj0") || code.equals("sd15")) {
                addPolygon("forest", "forest", 12, features);
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
