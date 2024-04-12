package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.utils.LanguageUtils;

import java.util.Arrays;
import java.util.List;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class Transportation implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    static final List<String> PAVED_VALUES = Arrays.asList("A", "C", "G", "Md");

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.canBeLine()) {
            if (sf.getSourceLayer().equals(Layer.GRPK_KELIAI)) {
                var paskirtis = sf.getString("PASKIRTIS");
                var tipas = sf.getLong("TIPAS");
                var danga = sf.getString("DANGA");

                if (tipas == 1) {
                    addTransportationFeature(FieldValue.CLASS_MOTORWAY, null, 2, sf, features);
                } else if (tipas == 5) {
                    addTransportationFeature(FieldValue.CLASS_TRUNK, null, 4, sf, features);
                } else if (tipas == 2) {
                    addTransportationFeature(FieldValue.CLASS_PRIMARY, null, 4, sf, features);
                } else if (tipas == 3) {
                    addTransportationFeature(FieldValue.CLASS_SECONDARY, null, 8, sf, features);
                } else if (tipas == 4) {
                    addTransportationFeature(FieldValue.CLASS_TERTIARY, null, 8, sf, features);
                } else if (tipas == 6) {
                    // Gerosios vilties st. and other similar streets belong to tipas 6
                    // For now just assign service, because residential filters it out in some styles completely
                    addTransportationFeature(FieldValue.CLASS_MINOR, null, 12, sf, features);
                } else if (tipas == 7 && (paskirtis.equals("JUNG") || paskirtis.equals("LEGR"))) {
                    addTransportationFeature(FieldValue.CLASS_SECONDARY, null, 12, sf, features);
                } else if (tipas == 7 || tipas == 9) {
                    addTransportationFeature(FieldValue.CLASS_SERVICE, null, 13, sf, features);
                } else if (tipas == 8 && danga.equals("Ž")) {
                    addTransportationFeature(FieldValue.CLASS_PATH, null, 14, sf, features);
                } else if (tipas == 8) {
                    addTransportationFeature(FieldValue.CLASS_SERVICE, null, 13, sf, features);
                } else if (tipas == 10 || tipas == 11 || tipas == 13) {
                    addTransportationFeature(FieldValue.CLASS_PATH, null, 14, sf, features);
                } else if (tipas == 14) {
                    addTransportationFeature(FieldValue.CLASS_FERRY, null, 13, sf, features);
                } else {
                    addTransportationFeature(FieldValue.CLASS_UNCLASSIFIED, null, 14, sf, features);
                }
            } else if (sf.getSourceLayer().equals(Layer.GRPK_GELEZINK)) {
                var gkodas = sf.getString("GKODAS");
                var minZoom = sf.getLong("TIPAS") == 1 ? 8 : 11;

                switch (gkodas) {
                    case "gz1", "gz2", "gz1gz2", "gz10" ->
                            addTransportationFeature(FieldValue.CLASS_RAIL, FieldValue.SUBCLASS_RAIL, minZoom, sf, features);
                    case "gz4" -> {
                        if ("funik.".equals(sf.getString("INFO"))) {
                            addTransportationFeature(FieldValue.CLASS_RAIL, FieldValue.SUBCLASS_FUNICULAR, minZoom, sf, features);
                        } else {
                            addTransportationFeature(FieldValue.CLASS_RAIL, FieldValue.SUBCLASS_NARROW_GAUGE, minZoom, sf, features);
                        }
                    }
                }
            }
        }
    }

    public void addTransportationFeature(String clazz, String subclass, int minZoom, SourceFeature sf, FeatureCollector features) {
        var level = (int) sf.getLong("LYGMUO");
        var name = nullIfEmpty(sf.getString("VARDAS"));

        var expressway = clazz.equals(FieldValue.CLASS_MOTORWAY);

        var ref = expressway ? nullIfEmpty(sf.getString("NUMERIS")) : null;
        var refLength = ref != null ? ref.length() : null;
        var surface = PAVED_VALUES.contains(sf.getString("DANGA")) ? "paved" : "unpaved";

        var brunnel = switch (level) {
            case 1, 2, 3 -> "bridge";
            case -1 -> "tunnel";
            default -> null;
        };

        features.line(this.name())
                .setAttr(Field.CLASS, clazz)
                .setAttr(Field.SUBCLASS, subclass)
                .setAttr(Field.EXPRESSWAY, expressway)
                .setAttr(Field.LEVEL, level)
                .setAttr(Field.BRUNNEL, brunnel)
                .setAttr(Field.SURFACE, surface)
                .setMinZoom(minZoom)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0);

        // TODO transportation_name building should be moved to TransportationName class once Transportation layer becomes stable
        if (ref != null || name != null) {
            features.line("transportation_name")
                    .putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setAttr(Field.CLASS, clazz)
                    .setAttr(Field.SUBCLASS, subclass)
                    .setAttr(Field.REF, ref)
                    .setAttr(Field.REF_LENGTH, refLength)
                    .setAttr(Field.BRUNNEL, brunnel)
                    .setAttr(Field.LEVEL, level)
                    .setMinPixelSize(0.0)
                    .setPixelTolerance(0.0)
                    .setMinZoom(Math.min(minZoom + 2, 14));
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        if (zoom >= 14) {
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
        return "transportation";
    }

    private static final class Field {
        static final String CLASS = "class";
        static final String SUBCLASS = "subclass";
        static final String EXPRESSWAY = "expressway";
        static final String REF = "ref";
        static final String REF_LENGTH = "ref_length";
        static final String LEVEL = "layer";
        static final String BRUNNEL = "brunnel";
        static final String SURFACE = "surface";
    }

    private static class FieldValue {
        static final String CLASS_MOTORWAY = "motorway";
        static final String CLASS_TRUNK = "trunk";
        static final String CLASS_PRIMARY = "primary";
        static final String CLASS_SECONDARY = "secondary";
        static final String CLASS_TERTIARY = "tertiary";
        static final String CLASS_RESIDENTIAL = "residential";
        static final String CLASS_LINK = "link";
        static final String CLASS_MINOR = "minor";
        static final String CLASS_SERVICE = "service";
        static final String CLASS_PATH = "path";
        static final String CLASS_FERRY = "ferry";
        static final String CLASS_UNCLASSIFIED = "unclassified";
        static final String CLASS_TRACK = "track";
        static final String CLASS_RAIL = "rail";
        static final String SUBCLASS_RAIL = "rail";
        static final String SUBCLASS_NARROW_GAUGE = "narrow_gauge";
        static final String SUBCLASS_FUNICULAR = "funicular";
    }
}