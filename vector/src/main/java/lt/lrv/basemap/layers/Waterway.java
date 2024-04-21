package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.ZoomFunction;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class Waterway implements OpenMapTilesSchema.Waterway, ForwardingProfile.FeaturePostProcessor {

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

            switch (type) {
                case 1 -> addWaterwayLine(9, sf, features);
                case 2 -> addWaterwayLine(10, sf, features);
                case 3, 4 -> addWaterwayLine(12, sf, features);
                default -> addWaterwayLine(11, sf, features);
            }
        }
    }

    void addWaterwayLine(int minZoom, SourceFeature sf, FeatureCollector features) {
        var length = (int) sf.getLong("PLOTIS");
        var name = nullIfEmpty(sf.getString("VARDAS"));
        var code = sf.getString("GKODAS");
        var type = (int) sf.getLong("TIPAS");

        // Do not include fictional water lines that overlap lakes
        if (Arrays.asList("fhc1", "fhc3").contains(code)) {
            return;
        }

        var clazz = switch (type) {
            case 1 -> FieldValues.CLASS_RIVER;
            case 2 -> FieldValues.CLASS_CANAL;
            case 3, 4 -> FieldValues.CLASS_DITCH;
            default -> FieldValues.CLASS_STREAM;
        };

        var brunnel = switch (code) {
            case "op01", "hc1op0", "hc31op0", "hc32op0", "hc33op0", "hc3op0" -> FieldValues.BRUNNEL_TUNNEL;
            default -> null;
        };

        var feature = features.line(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .setAttrWithMinzoom(Fields.BRUNNEL, brunnel, 12)
                .setAttr(Fields.INTERMITTENT, 0)
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
}
