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

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class Waterway implements OpenMapTilesSchema.Waterway, ForwardingProfile.FeaturePostProcessor {

    // We have rivers with names S-3, S-8. This regex filters out names ending with - and number
    static final Pattern PATTERN_NAMES_IGNORE = Pattern.compile("-\\d+$");

    static final ZoomFunction.MeterToPixelThresholds MIN_PIXEL_LENGTHS = ZoomFunction.meterThresholds()
            .put(8, 1_000)
            .put(9, 500)
            .put(10, 250)
            .put(11, 50);

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layer.GRPK_HIDRO_L) && sf.canBeLine()) {
            var type = (int) sf.getLong("TIPAS");
            var code = sf.getString("GKODAS");
            var gkey = nullIfEmpty(sf.getString("GRAKTAS"));

           if (List.of("hc1", "hc3", "hc33", "hc32", "hc31", "hc1op0", "hc3op0", "hc33op0", "hc32op0", "hc31op0", "op01").contains(code) && type == 1 && gkey != null) {
                addWaterwayLine(FieldValues.CLASS_RIVER, 9, sf, features);
           } else if (List.of("hc1", "hc3", "hc33", "hc32", "hc31", "hc1op0", "hc3op0", "hc33op0", "hc32op0", "hc31op0", "op01").contains(code) && type == 2 && gkey != null) {
                addWaterwayLine(FieldValues.CLASS_CANAL, 9, sf, features);
           } else if (List.of("hc31", "hc32", "hc33", "hc31op0", "hc32op0", "hc33op0").contains(code) && (type == 3 || type == 4)) {
                addWaterwayLine(FieldValues.CLASS_DITCH, 12, sf, features);
           } else if (!code.equals("fhc3")) {
                addWaterwayLine(FieldValues.CLASS_STREAM, 11, sf, features);
           }
        }
    }

    void addWaterwayLine(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        var length = (int) sf.getLong("PLOTIS");
        var name = nullIfEmpty(sf.getString("VARDAS"));
        var code = sf.getString("GKODAS");

        var brunnel = switch (code) {
            case "op01", "hc1op0", "hc31op0", "hc32op0", "hc33op0", "hc3op0" -> FieldValues.BRUNNEL_TUNNEL;
            default -> null;
        };

        var feature = features.line(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.BRUNNEL, brunnel)
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
