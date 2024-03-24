package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Boundaries implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("RIBOS") && sf.canBeLine()) {
            features.line(this.name())
                    .setAttr("gkodas", sf.getTag("GKODAS"))
                    .setAttr("vardas", sf.getTag("VARDAS"));

        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return FeatureMerge.mergeLineStrings(
                items,
                0,
                0,
                4
        );
    }

    @Override
    public String name() {
        return "ribos";
    }
}
