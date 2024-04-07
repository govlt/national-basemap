package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.utils.LanguageUtils;

import java.util.Arrays;
import java.util.List;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class Transportation implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    static final List<String> PAVED_VALUES = Arrays.asList("A", "C", "G", "Md");

    static final String FIELD_CLASS = "class";
    static final String FIELD_EXPRESSWAY = "expressway";
    static final String FIELD_REF = "ref";
    static final String FIELD_REF_LENGTH = "ref_length";
    static final String FIELD_LEVEL = "level";
    static final String FIELD_SURFACE = "level";

    public static final String CLASS_MOTORWAY = "motorway";
    public static final String CLASS_TRUNK = "trunk";
    public static final String CLASS_PRIMARY = "primary";
    public static final String CLASS_SECONDARY = "secondary";
    public static final String CLASS_TERTIARY = "tertiary";
    public static final String CLASS_RESIDENTIAL = "residential";
    public static final String CLASS_LINK = "link";
    public static final String CLASS_SERVICE = "service";
    public static final String CLASS_PATH = "path";
    public static final String CLASS_FERRY = "ferry";
    public static final String CLASS_UNCLASSIFIED = "unclassified";
    public static final String CLASS_TRACK = "track";

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.canBeLine()) {
            if (sf.getSourceLayer().equals("KELIAI")) {
                var paskirtis = sf.getString("PASKIRTIS");
                var tipas = sf.getLong("TIPAS");
                var danga = sf.getString("DANGA");

                if (tipas == 1) {
                    addTransportationFeature(CLASS_MOTORWAY, 2, sf, features);
                } else if (tipas == 5) {
                    addTransportationFeature(CLASS_TRUNK, 4, sf, features);
                } else if (tipas == 2) {
                    addTransportationFeature(CLASS_PRIMARY, 4, sf, features);
                } else if (tipas == 3) {
                    addTransportationFeature(CLASS_SECONDARY, 8, sf, features);
                } else if (tipas == 4) {
                    addTransportationFeature(CLASS_TERTIARY, 8, sf, features);
                } else if (tipas == 6) {
                    addTransportationFeature(CLASS_RESIDENTIAL, 12, sf, features);
                } else if (tipas == 7 && paskirtis.equals("JUNG")) {
                    addTransportationFeature(CLASS_LINK, 13, sf, features);
                } else if (tipas == 7 || tipas == 9) {
                    addTransportationFeature(CLASS_SERVICE, 13, sf, features);
                } else if (tipas == 8 && danga.equals("Ž")) {
                    addTransportationFeature(CLASS_PATH, 14, sf, features);
                } else if (tipas == 8) {
                    addTransportationFeature(CLASS_SERVICE, 13, sf, features);
                } else if (tipas == 10 || tipas == 11 || tipas == 13) {
                    addTransportationFeature(CLASS_PATH, 14, sf, features);
                } else if (tipas == 14) {
                    addTransportationFeature(CLASS_FERRY, 13, sf, features);
                } else {
                    addTransportationFeature(CLASS_UNCLASSIFIED, 14, sf, features);
                }
            } else if (sf.getSourceLayer().equals("GELEZINK")) {
                var gkodas = sf.getString("GKODAS");
                var tipas = sf.getLong("TIPAS");

                if (Arrays.asList("gz1", "gz2", "gz1gz2").contains(gkodas) && tipas == 1) {
                    addTransportationFeature(CLASS_TRACK, 11, sf, features);
                } else {
                    addTransportationFeature(CLASS_TRACK, 8, sf, features);
                }
            }
        }
    }

    public void addTransportationFeature(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        var level = (int) sf.getLong("LYGMUO");
        var name = nullIfEmpty(sf.getString("VARDAS"));

        var expressway = clazz.equals(CLASS_MOTORWAY);

        var ref = expressway ? nullIfEmpty(sf.getString("NUMERIS")) : null;
        var refLength = ref != null ? ref.length() : null;
        var surface = PAVED_VALUES.contains(sf.getString("DANGA")) ? "paved" : "unpaved";

        features.line(this.name())
                .setAttr(FIELD_CLASS, clazz)
                .setAttr(FIELD_EXPRESSWAY, expressway)
                .setAttr(FIELD_LEVEL, level)
                .setAttr(FIELD_SURFACE, surface)
                .setAttr("minZoom", minZoom)
                .setMinZoom(minZoom)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0);

        if (ref != null || name != null) {
            features.line("transportation_name")
                    .putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setAttr(FIELD_CLASS, clazz)
                    .setAttr(FIELD_REF, ref)
                    .setAttr(FIELD_REF_LENGTH, refLength)
                    .setAttr(FIELD_LEVEL, level)
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
}
