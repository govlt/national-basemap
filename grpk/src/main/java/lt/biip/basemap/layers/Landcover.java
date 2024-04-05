package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Landcover implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().startsWith("PLOTAI") && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "hd6" -> addPolygon("wetland", "wetland", 10, sf, features);
                case "sd4" -> addPolygon("farmland", "meadow", 10, sf, features);
                case "ms4" -> addPolygon("farmland", "allotments", 10, sf, features);
                case "sd42" -> addPolygon("sand", null, 10, sf, features);
                case "ms0", "sd15" -> addPolygon("wood", "forest", 2, sf, features);
            }
        }
    }


    public void addPolygon(String attrClass, String subclass, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.polygon(this.name())
                .setAttr("class", attrClass)
                .setAttr("name", sf.getTag("VARDAS"))
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setMinZoom(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14)
            return items;

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }

    @Override
    public String name() {
        return "landcover";
    }
}
