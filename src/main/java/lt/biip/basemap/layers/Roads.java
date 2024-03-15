package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public class Roads implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (!sf.canBeLine()) {
            return;
        }

        var minZoom = 10;

        var category = sf.getTag("KATEGOR");
        if (category == "AM" || category == "1" || category == "2") {
            minZoom = 3;
        } else if (category == "3" || category == "4") {
            minZoom = 8;
        }

        var labelMinZoom = minZoom + 1;

        var label = ObjectUtils.firstNonNull(sf.getTag("ENUMERIS"), sf.getTag("NUMERIS"));

        features.line(this.name())
                .inheritAttrFromSource("KATEGOR")
                .inheritAttrFromSource("ENUMERIS")
                .inheritAttrFromSource("NUMERIS")
                .inheritAttrFromSource("VARDAS")
                .inheritAttrFromSource("PASKIRTIS")
                .setAttr("minZoom", minZoom)
                .setAttrWithMinzoom("label", label, labelMinZoom)
                .setMinPixelSize(2.0)
                .setPixelTolerance(0.0)
                .setMinZoom(minZoom)
                .setSortKey(minZoom);

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
        return "roads";
    }
}
