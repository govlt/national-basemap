package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Areas implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().startsWith("PLOTAI") && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "hd1", "hd2", "hd3", "hd4", "hd9" -> addPolygon("water", 10, sf, features);
                case "hd5" -> addPolygon("water", 0, sf, features);
                case "hd6" -> addPolygon("wetland", 10, sf, features);
                case "sd4" -> addPolygon("meadow", 10, sf, features);
                case "vp1" -> addPolygon("cemetery", 10, sf, features);
                case "ms4" -> addPolygon("allotments", 10, sf, features);
                case "ms0", "sd15" -> addPolygon("forest", 2, sf, features);
                case "va1", "va11" -> addPolygon("airport", 0, sf, features);
                case "pu0" -> addPolygon("residential", 10, sf, features);
                case "pu3" -> addPolygon("industrial", 10, sf, features);
            }
        }
    }


    public void addPolygon(String kind, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.polygon(this.name())
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setAttr("vardas", sf.getTag("VARDAS"))
                .setAttr("kind", kind)
                .setMinZoom(minZoom)
                .setSortKey(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 15)
            return items;

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }

    @Override
    public String name() {
        return "plotai";
    }
}
