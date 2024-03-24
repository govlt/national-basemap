package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class PlaceTitles implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("VIETOV_T") && sf.isPoint()) {
            features.point(this.name())
                    .setAttr("gkodas", sf.getTag("GKODAS"))
                    .setAttr("adm_tip", sf.getTag("ADM_TIP"))
                    .setAttr("vardas", sf.getTag("VARDAS"))
                    .setMinZoom(10);
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return items;
    }

    @Override
    public String name() {
        return "vietov_t";
    }
}
