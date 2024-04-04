package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class WaterLines implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("HIDRO_L") && sf.canBeLine()) {
            var length = (int) sf.getLong("PLOTIS");

            features.line(this.name())
                    .setAttr("gkodas", sf.getTag("GKODAS"))
                    .setAttr("vardas", sf.getTag("VARDAS"))
                    .setMinPixelSize(0)
                    .setPixelTolerance(0.0)
                    .setMinZoom(9)
                    .setSortKeyDescending(length);
        }
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
        return "hidro_l";
    }
}
