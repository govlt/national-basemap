package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.Arrays;
import java.util.List;

public class Areas implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (!sf.canBePolygon()) {
            return;
        }

        var code = sf.getTag("GKODAS", "").toString();

        if (code.startsWith("hd")) {
            int minZoom = getWaterMinZoom(code);
            addPolygon("water", minZoom, sf, features);
        } else if (code.equals("sd4")) {
            addPolygon("meadow", 10, sf, features);
        } else if (code.equals("vp1")) {
            addPolygon("cemetery", 10, sf, features);
        } else if (code.equals("ms4")) {
            addPolygon("allotments", 10, sf, features);
        } else if (Arrays.asList("ms0", "sd15").contains(code)) {
            addPolygon("forest", 8, sf, features);
        } else if (code.equals("hd6")) {
            addPolygon("wetland", 10, sf, features);
        } else if (Arrays.asList("va1", "va11").contains(code)) {
            addPolygon("airport", 0, sf, features);
        } else if (code.equals("pu3")) {
            addPolygon("industrial", 10, sf, features);
        } else if (code.equals("pu0")) {
            addPolygon("residential", 10, sf, features);
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

    public int getWaterMinZoom(String code) {
        if (code.equals("hd5")) {
            return 0;
        } else {
            return 10;
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        return FeatureMerge.mergeOverlappingPolygons(items, 1);
    }

    @Override
    public String name() {
        return "plotai";
    }
}
