package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

import java.util.Arrays;
import java.util.List;

public class Transportation implements OpenMapTilesSchema.Transportation, ForwardingProfile.LayerPostProcesser {

    static final List<String> PAVED_VALUES = Arrays.asList("A", "C", "G", "Md");

    static final String CLASS_RAIL = "rail";

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.canBeLine()) {
            switch (sf.getSourceLayer()) {
                case Layers.GRPK_KELIAI -> processRoadFeature(sf, features);
                case Layers.GRPK_GELEZINK -> processRailFeature(sf, features);
            }
        }
    }

    void processRoadFeature(SourceFeature sf, FeatureCollector features) {
        var type = (int) sf.getLong("TIPAS");

        switch (type) {
            case 1 -> addTransportationFeature(FieldValues.CLASS_MOTORWAY, null, 2, sf, features);
            case 5 -> addTransportationFeature(FieldValues.CLASS_TRUNK, null, 4, sf, features);
            case 2 -> addTransportationFeature(FieldValues.CLASS_PRIMARY, null, 4, sf, features);
            case 3 -> addTransportationFeature(FieldValues.CLASS_SECONDARY, null, 8, sf, features);
            case 4 -> addTransportationFeature(FieldValues.CLASS_TERTIARY, null, 8, sf, features);
            case 6, 8 -> addTransportationFeature(FieldValues.CLASS_MINOR, null, 12, sf, features);
            case 7, 9 -> addTransportationFeature(FieldValues.CLASS_SERVICE, null, 13, sf, features);
            case 10, 11 -> addTransportationFeature(FieldValues.CLASS_TRACK, null, 13, sf, features);
            case 15 -> addTransportationFeature(FieldValues.CLASS_PATH, null, 13, sf, features);
            case 13 -> addTransportationFeature(FieldValues.CLASS_PATH, null, 14, sf, features);
            case 14 -> addTransportationFeature(FieldValues.CLASS_FERRY, null, 13, sf, features);
            default -> addTransportationFeature(null, null, 14, sf, features);
        }
    }

    void processRailFeature(SourceFeature sf, FeatureCollector features) {
        var code = sf.getString("GKODAS");
        var minZoom = sf.getLong("TIPAS") == 1 ? 8 : 11;

        switch (code) {
            case "gz1", "gz2", "gz1gz2", "gz10" ->
                    addTransportationFeature(CLASS_RAIL, FieldValues.SUBCLASS_RAIL, minZoom, sf, features);
            case "gz4" -> {
                if ("funik.".equals(sf.getString("INFO"))) {
                    addTransportationFeature(CLASS_RAIL, FieldValues.SUBCLASS_FUNICULAR, minZoom, sf, features);
                } else {
                    addTransportationFeature(CLASS_RAIL, FieldValues.SUBCLASS_NARROW_GAUGE, minZoom, sf, features);
                }
            }
        }
    }

    public void addTransportationFeature(String clazz, String subclass, int minZoom, SourceFeature sf, FeatureCollector features) {
        var rawLevel = (int) sf.getLong("LYGMUO");
        var level = rawLevel != -9999 ? rawLevel : null;

        var expressway = "AM".equals(sf.getString("KATEGOR")) ? 1 : null;
        var surface = PAVED_VALUES.contains(sf.getString("DANGA")) ? FieldValues.SURFACE_PAVED : FieldValues.SURFACE_UNPAVED;

        var brunnel = switch (rawLevel) {
            case 1, 2, 3 -> "bridge";
            case -1 -> "tunnel";
            default -> null;
        };

        features.line(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.SUBCLASS, subclass)
                .setAttrWithMinzoom(Fields.EXPRESSWAY, expressway, 8)
                .setAttrWithMinzoom(Fields.BRUNNEL, brunnel, 12)
                .setAttrWithMinzoom(Fields.SURFACE, surface, 12)
                .setAttrWithMinzoom(Fields.LEVEL, level, 12)
                .setMinZoom(minZoom)
                .setMinPixelSize(0.0);

        TransportationName.addFeature(clazz, subclass, minZoom, sf, features);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        if (zoom >= 14) {
            return FeatureMerge.mergeMultiLineString(items);
        }

        return FeatureMerge.mergeLineStrings(
                items,
                0.5, // after merging, remove lines that are still less than 0.5px long
                0.1, // simplify output linestrings using a 0.1px tolerance
                4.0 // remove any detail more than 4px outside the tile boundary
        );
    }
}
