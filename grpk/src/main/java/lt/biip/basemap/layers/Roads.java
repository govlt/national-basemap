package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.Arrays;
import java.util.List;

public class Roads implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("KELIAI") && sf.canBeLine()) {

            var gkodas = sf.getString("GKODAS");
            var paskirtis = sf.getString("PASKIRTIS");
            var kategor = sf.getString("KATEGOR");
            var numeris = sf.getString("NUMERIS");
            var lygmuo = sf.getLong("LYGMUO");
    
            if (numeris.startsWith("A")) {
                addRoad("motorway", 2, sf, features);
            } else if  (kategor.equals("3")) {
                addRoad("primary", 2, sf, features);
            } else if  (Arrays.asList("4", "5").contains(kategor)) {
                addRoad("secondary", 8, sf, features);
            } else if  (Arrays.asList("gc2", "gc12").contains(gkodas) && paskirtis.equals("PAGR")) {
                addRoad("tertiary", 9, sf, features);
            } else {
                addRoad("unclassified", 11, sf, features);
            }
            
        }
    }

    public void addRoad(String kind, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.line(this.name())
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setAttr("vardas", sf.getTag("VARDAS"))
                .setAttr("paskirtis", sf.getTag("PASKIRTIS"))
                .setAttr("kategor", sf.getTag("KATEGOR"))
                .setAttr("numeris", sf.getTag("NUMERIS"))
                .setAttr("lygmuo", sf.getTag("LYGMUO"))
                .setAttr("kind", kind)
                .setAttr("minZoom", minZoom)
                .setMinZoom(minZoom)
                .setSortKey(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return items;
        // return FeatureMerge.mergeLineStrings(
        //         items,
        //         0.5, // after merging, remove lines that are still less than 0.5px long
        //         0.1, // simplify output linestrings using a 0.1px tolerance
        //         4.0 // remove any detail more than 4px outside the tile boundary
        // );
    }

    @Override
    public String name() {
        return "keliai";
    }
}
