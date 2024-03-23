package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Roads implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("KELIAI") && sf.canBeLine()) {
            var minZoom = getMinZoom(sf);

            features.line(this.name())
                    .setAttr("gkodas", sf.getTag("GKODAS"))
                    .setAttr("kategor", sf.getTag("KATEGOR"))
                    .setAttr("enumeris", sf.getTag("ENUMERIS"))
                    .setAttr("numeris", sf.getTag("NUMERIS"))
                    .setAttr("lygmuo", sf.getTag("LYGMUO"))
                    .setAttr("paskirtis", sf.getTag("PASKIRTIS"))
                    .setAttr("vardas", sf.getTag("VARDAS"))
                    .setMinPixelSize(0.0)
                    .setPixelTolerance(0.0)
                    .setMinZoom(minZoom)
                    .setSortKeyDescending(minZoom);
        }
    }

    int getMinZoom(SourceFeature sf) {
        var gkodas = sf.getString("GKODAS");
        var paskirtis = sf.getString("PASKIRTIS");
        var kategor = sf.getString("KATEGOR");
        var numeris = sf.getString("NUMERIS");
        var lygmuo = sf.getLong("LYGMUO");

        if (Arrays.asList("A12", "A5", "A6", "A1", "A11", "E28", "E67", "E77", "E85", "E262", "E272").contains(numeris)) {
            return 0;
        }

        if (Arrays.asList("AM", "1", "2").contains(kategor)) {
            return 0;
        }

        if (Arrays.asList("PAGR", "P/Z").contains(paskirtis)) {
            return 0;
        }

        if (gkodas.equals("dc2")) {
            return 0;
        }

        if (Arrays.asList("gc2", "gc12", "gc14").contains(gkodas) && lygmuo < 0) {
            return 0;
        }

        if (Arrays.asList("4", "5").contains(kategor) && gkodas.equals("gc12") && lygmuo < 0) {
            return 0;
        }

        if (Arrays.asList("2", "3").contains(kategor) && lygmuo < 0) {
            return 0;
        }

        return 10;
    }


    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return FeatureMerge.mergeLineStrings(
                items,
                0.5, // after merging, remove lines that are still less than 0.5px long
                0.1, // simplify output linestrings using a 0.1px tolerance
                4.0 // remove any detail more than 4px outside the tile boundary
        );
    }

    @Override
    public String name() {
        return "keliai";
    }
}
