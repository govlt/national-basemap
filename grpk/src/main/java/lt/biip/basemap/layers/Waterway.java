package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.ZoomFunction;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;
import lt.biip.basemap.utils.LanguageUtils;

import java.util.List;
import java.util.regex.Pattern;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class Waterway implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    // We have rivers with names S-3, S-8. This regex filters out names ending with - and number
    static final Pattern PATTERN_NAMES_IGNORE = Pattern.compile("-\\d+$");

    static final ZoomFunction.MeterToPixelThresholds MIN_PIXEL_LENGTHS = ZoomFunction.meterThresholds()
            .put(8, 300_000)
            .put(9, 8_000)
            .put(10, 4_000)
            .put(11, 1_000);

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layer.GRPK_HIDRO_L) && sf.canBeLine()) {
            var type = (int) sf.getLong("TIPAS");
            var code = sf.getString("GKODAS");

            if (code.equals("hc1") && type == 1) {
                addWaterwayLine("river", 8, sf, features);
            } else if (List.of("hc3", "hc33").contains(code) && type == 1) {
                addWaterwayLine("river", 9, sf, features);
            } else if (List.of("hc1", "hc3", "hc31", "hc32", "hc33").contains(code) && type == 2) {
                addWaterwayLine("canal", 9, sf, features);
            } else if (List.of("hc31", "hc32").contains(code) && type == 1) {
                addWaterwayLine("river", 10, sf, features);
            } else if (List.of("hc31", "hc32").contains(code) && (type == 3 || type == 4)) {
                addWaterwayLine("ditch", 11, sf, features);
            }
        }
    }

    void addWaterwayLine(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        var length = (int) sf.getLong("PLOTIS");
        var name = nullIfEmpty(sf.getString("VARDAS"));

        var feature = features.line(this.name())
                .setBufferPixels(4)
                .setAttr("class", clazz)
                .setAttr("intermittent", 0)
                .setMinPixelSizeBelowZoom(11, 0)
                .setMinZoom(minZoom)
                .setSortKeyDescending(length);

        if (Waterway.hasHumanReadableName(name)) {
            feature.putAttrs(LanguageUtils.getNames(sf.tags()));
        }
    }

    static boolean hasHumanReadableName(String name) {
        return name != null && !name.isBlank() && !PATTERN_NAMES_IGNORE.matcher(name).find();

    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        if (zoom >= 13) {
            return items;
        }

        var minLength = zoom <= 11 ? MIN_PIXEL_LENGTHS.apply(zoom).doubleValue() : 0.5;

        return FeatureMerge.mergeLineStrings(
                items,
                minLength, // after merging, remove lines that are still less than 0.5px long
                0.1, // simplify output linestrings using a 0.1px tolerance
                4.0 // remove any detail more than 4px outside the tile boundary
        );
    }

    @Override
    public String name() {
        return "waterway";
    }
}
