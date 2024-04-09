package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;
import lt.biip.basemap.utils.LanguageUtils;

import java.util.List;

public class Waterway implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layer.GRPK_HIDRO_L) && sf.canBeLine()) {
            var type = (int) sf.getLong("TIPAS");

            switch (type) {
                case 1 -> addWaterwayLine("river", sf, features);
                case 2 -> addWaterwayLine("canal", sf, features);
                case 3, 4 -> addWaterwayLine("ditch", sf, features);
                default -> addWaterwayLine("unknown", sf, features);
            }
        }
    }

    void addWaterwayLine(String clazz, SourceFeature sf, FeatureCollector features) {
        var length = (int) sf.getLong("PLOTIS");
        var name = sf.getString("VARDAS", "");
        var minZoom = name.isBlank() ? 12 : 9;

        features.line(this.name())
                .setAttr("class", clazz)
                .setAttr("intermittent", 0)
                .putAttrs(LanguageUtils.getNames(sf.tags()))
                .setMinPixelSize(0)
                .setPixelTolerance(0.0)
                .setMinZoom(minZoom)
                .setSortKeyDescending(length);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        if (zoom >= 13) {
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
        return "waterway";
    }
}
