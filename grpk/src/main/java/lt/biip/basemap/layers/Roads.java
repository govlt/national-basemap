package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Roads implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("KELIAI") && sf.canBeLine()) {

            var paskirtis = sf.getString("PASKIRTIS");
            var tipas = sf.getLong("TIPAS");
            var danga = sf.getString("DANGA");

            if (tipas == 1) {
                addRoad("motorway", 2, sf, features);
            } else if (tipas == 5) {
                addRoad("trunk", 4, sf, features);
            } else if (tipas == 2) {
                addRoad("primary", 4, sf, features);
            } else if (tipas == 3) {
                addRoad("secondary", 8, sf, features);
            } else if (tipas == 4) {
                addRoad("tertiary", 8, sf, features);
            } else if (tipas == 6) {
                addRoad("residential", 12, sf, features);
            } else if (tipas == 7 && paskirtis.equals("JUNG")) {
                addRoad("link", 13, sf, features);
            } else if (tipas == 7 || tipas == 9) {
                addRoad("service", 13, sf, features);
            } else if (tipas == 8 && danga.equals("Ž")) {
                addRoad("path", 14, sf, features);
            } else if (tipas == 8) {
                addRoad("service", 13, sf, features);
            } else if (tipas == 10 || tipas == 11 || tipas == 13) {
                addRoad("path", 14, sf, features);
            } else if (tipas == 14) {
                addRoad("ferry", 13, sf, features);
            } else {
                addRoad("unclassified", 14, sf, features);
            }

        }
    }

    public void addRoad(String kind, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.line(this.name())
                .setAttr("ref", sf.getTag("NUMERIS"))
                .setAttr("name", sf.getTag("VARDAS"))
                .setAttr("level", sf.getTag("LYGMUO"))
                .setAttr("kind", kind)
                .setAttr("kind_detail", sf.getTag("GKODAS"))
                .setAttr("minZoom", minZoom)
                .setMinZoom(minZoom)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        if (zoom < 14) {
            return FeatureMerge.mergeLineStrings(
                    items,
                    0.5, // after merging, remove lines that are still less than 0.5px long
                    0.1, // simplify output linestrings using a 0.1px tolerance
                    4.0 // remove any detail more than 4px outside the tile boundary
            );
        } else {
            return items;
        }
    }

    @Override
    public String name() {
        return "keliai";
    }
}
