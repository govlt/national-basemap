package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.Arrays;
import java.util.List;

public class Railway implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("GELEZINK") && sf.canBeLine()) {
            var minZoom = getMinZoom(sf);

            features.line(this.name())
                    .setAttr("gkodas", sf.getTag("GKODAS"))
                    .setAttr("tipas", sf.getTag("TIPAS"))
                    .setAttr("lygmuo", sf.getTag("LYGMUO"))
                    .setMinPixelSize(0.0)
                    .setPixelTolerance(0.0)
                    .setMinZoom(minZoom)
                    .setSortKeyDescending(minZoom);
        }
    }

    int getMinZoom(SourceFeature sf) {
        var gkodas = sf.getString("GKODAS");
        var tipas = sf.getString("TIPAS");

        if (Arrays.asList("gz1", "gz2", "gz1gz2").contains(gkodas) && tipas.equals("1")) {
            return 11;
        }

        return 8;
    }


    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return items;
    }

    @Override
    public String name() {
        return "gelezink";
    }


}
