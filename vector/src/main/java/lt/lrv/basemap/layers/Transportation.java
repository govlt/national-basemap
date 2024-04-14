package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;

import java.util.Arrays;
import java.util.List;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class Transportation implements OpenMapTilesSchema.Transportation, ForwardingProfile.FeaturePostProcessor {

    static final List<String> PAVED_VALUES = Arrays.asList("A", "C", "G", "Md");

    static final String CLASS_RAIL = "rail";

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.canBeLine()) {
            if (sf.getSourceLayer().equals(Layer.GRPK_KELIAI)) {
                var paskirtis = sf.getString("PASKIRTIS");
                var tipas = sf.getLong("TIPAS");

                if (tipas == 1) {
                    addTransportationFeature(FieldValues.CLASS_MOTORWAY, null, 2, sf, features);
                } else if (tipas == 5) {
                    addTransportationFeature(FieldValues.CLASS_TRUNK, null, 4, sf, features);
                } else if (tipas == 2) {
                    addTransportationFeature(FieldValues.CLASS_PRIMARY, null, 4, sf, features);
                } else if (tipas == 3) {
                    addTransportationFeature(FieldValues.CLASS_SECONDARY, null, 8, sf, features);
                } else if (tipas == 7 && (paskirtis.equals("JUNG") || paskirtis.equals("LEGR"))) {
                    addTransportationFeature(FieldValues.CLASS_SECONDARY, null, 12, sf, features);
                } else if (tipas == 4) {
                    addTransportationFeature(FieldValues.CLASS_TERTIARY, null, 8, sf, features);
                } else if (tipas == 6 || tipas == 8) {
                    addTransportationFeature(FieldValues.CLASS_MINOR, null, 12, sf, features);
                } else if (tipas == 7 || tipas == 9) {
                    addTransportationFeature(FieldValues.CLASS_SERVICE, null, 13, sf, features);
                } else if (tipas == 10 || tipas == 11) {
                    addTransportationFeature(FieldValues.CLASS_TRACK, null, 13, sf, features);
                } else if (tipas == 13) {
                    addTransportationFeature(FieldValues.CLASS_PATH, null, 14, sf, features);
                } else if (tipas == 14) {
                    addTransportationFeature(FieldValues.CLASS_FERRY, null, 13, sf, features);
                } else {
                    // TODO: Probably this should be removed and remapped to other types.
                    //  OpenVectorTiles doesn't seem to support this value
                    addTransportationFeature("unclassified", null, 14, sf, features);
                }
            } else if (sf.getSourceLayer().equals(Layer.GRPK_GELEZINK)) {
                var gkodas = sf.getString("GKODAS");
                var minZoom = sf.getLong("TIPAS") == 1 ? 8 : 11;

                switch (gkodas) {
                    case "gz1", "gz2", "gz1gz2", "gz10" ->
                            addTransportationFeature("rail", FieldValues.SUBCLASS_RAIL, minZoom, sf, features);
                    case "gz4" -> {
                        if ("funik.".equals(sf.getString("INFO"))) {
                            addTransportationFeature(CLASS_RAIL, FieldValues.SUBCLASS_FUNICULAR, minZoom, sf, features);
                        } else {
                            addTransportationFeature(CLASS_RAIL, FieldValues.SUBCLASS_NARROW_GAUGE, minZoom, sf, features);
                        }
                    }
                }
            }
        }
    }

    public void addTransportationFeature(String clazz, String subclass, int minZoom, SourceFeature sf, FeatureCollector features) {
        var level = (int) sf.getLong("LYGMUO");
        var name = nullIfEmpty(sf.getString("VARDAS"));

        var expressway = clazz.equals(FieldValues.CLASS_MOTORWAY);

        var ref = expressway ? nullIfEmpty(sf.getString("NUMERIS")) : null;
        var refLength = ref != null ? ref.length() : null;
        var surface = PAVED_VALUES.contains(sf.getString("DANGA")) ? "paved" : "unpaved";

        var brunnel = switch (level) {
            case 1, 2, 3 -> "bridge";
            case -1 -> "tunnel";
            default -> null;
        };

        features.line(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.SUBCLASS, subclass)
                .setAttr(Fields.EXPRESSWAY, expressway)
                .setAttr(Fields.LEVEL, level)
                .setAttr(Fields.BRUNNEL, brunnel)
                .setAttr(Fields.SURFACE, surface)
                .setMinZoom(minZoom)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0);

        // TODO transportation_name building should be moved to TransportationName class once Transportation layer becomes stable
        if (ref != null || name != null) {
            features.line(OpenMapTilesSchema.TransportationName.LAYER_NAME)
                    .setBufferPixels(OpenMapTilesSchema.TransportationName.BUFFER_SIZE)
                    .putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setAttr(OpenMapTilesSchema.TransportationName.Fields.CLASS, clazz)
                    .setAttr(OpenMapTilesSchema.TransportationName.Fields.SUBCLASS, subclass)
                    .setAttr(OpenMapTilesSchema.TransportationName.Fields.REF, ref)
                    .setAttr(OpenMapTilesSchema.TransportationName.Fields.REF_LENGTH, refLength)
                    .setAttr(OpenMapTilesSchema.TransportationName.Fields.BRUNNEL, brunnel)
                    .setAttr(OpenMapTilesSchema.TransportationName.Fields.LEVEL, level)
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
}
