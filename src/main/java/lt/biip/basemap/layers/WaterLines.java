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
        if (!sf.canBeLine()) {
            return;
        }


        var minZoom = 6;

        var length = (Integer) sf.getTag("PLOTIS", 0);

        features.line(this.name())
                .inheritAttrFromSource("TIPAS")
                .inheritAttrFromSource("VARDAS")
                .inheritAttrFromSource("PLOTIS")
                .inheritAttrFromSource("PLOTIS_ZP")
                .inheritAttrFromSource("LAIVYB")
                .setMinPixelSize(2.0)
                .setPixelTolerance(0.0)
                .setMinZoom(minZoom)
                .setSortKeyDescending(length);

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
        return "water-lines";
    }
}
