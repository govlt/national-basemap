package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.Arrays;
import java.util.List;

public class Transportation implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.canBeLine()) {
            if (sf.getSourceLayer().equals("KELIAI")) {
                var paskirtis = sf.getString("PASKIRTIS");
                var tipas = sf.getLong("TIPAS");
                var danga = sf.getString("DANGA");

                if (tipas == 1) {
                    addTransportationFeature("motorway", 2, sf, features);
                } else if (tipas == 5) {
                    addTransportationFeature("trunk", 4, sf, features);
                } else if (tipas == 2) {
                    addTransportationFeature("primary", 4, sf, features);
                } else if (tipas == 3) {
                    addTransportationFeature("secondary", 8, sf, features);
                } else if (tipas == 4) {
                    addTransportationFeature("tertiary", 8, sf, features);
                } else if (tipas == 6) {
                    addTransportationFeature("residential", 12, sf, features);
                } else if (tipas == 7 && paskirtis.equals("JUNG")) {
                    addTransportationFeature("link", 13, sf, features);
                } else if (tipas == 7 || tipas == 9) {
                    addTransportationFeature("service", 13, sf, features);
                } else if (tipas == 8 && danga.equals("Ž")) {
                    addTransportationFeature("path", 14, sf, features);
                } else if (tipas == 8) {
                    addTransportationFeature("service", 13, sf, features);
                } else if (tipas == 10 || tipas == 11 || tipas == 13) {
                    addTransportationFeature("path", 14, sf, features);
                } else if (tipas == 14) {
                    addTransportationFeature("ferry", 13, sf, features);
                } else {
                    addTransportationFeature("unclassified", 14, sf, features);
                }
            } else if (sf.getSourceLayer().equals("GELEZINK")) {
                var gkodas = sf.getString("GKODAS");
                var tipas = sf.getLong("TIPAS");

                if (Arrays.asList("gz1", "gz2", "gz1gz2").contains(gkodas) && tipas == 1) {
                    addTransportationFeature("track", 11, sf, features);
                } else {
                    addTransportationFeature("track", 8, sf, features);
                }
            }
        }
    }

    public void addTransportationFeature(String attrClass, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.line(this.name())
                .setAttr("class", attrClass)
                .setAttr("ref", sf.getTag("NUMERIS"))
                .setAttr("name", sf.getTag("VARDAS"))
                .setAttr("level", sf.getTag("LYGMUO"))
                .setAttr("kind_detail", sf.getTag("GKODAS"))
                .setAttr("minZoom", minZoom)
                .setMinZoom(minZoom)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        if (zoom >= 14) {
            return items;
        }

        return FeatureMerge.mergeLineStrings(
                items,
                0.5, // after merging, remove lines that are still less than 0.5px long
                0.1, // simplify output linestrings using a 0.1px tolerance
                4.0 // remove any detail more than 4px outside the tile boundary
        );
    }

    @Override
    public String name() {
        return "transportation";
    }
}
