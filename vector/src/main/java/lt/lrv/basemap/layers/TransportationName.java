package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;
import lt.lrv.basemap.utils.Utils;

import java.util.List;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class TransportationName implements OpenMapTilesSchema.TransportationName, ForwardingProfile.FeaturePostProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        // Currently TransportationName processFeature is handled inside Transportation
    }

    public static void addFeature(String clazz, String subclass, String brunnel, int level, int transportMinZoom, SourceFeature sf, FeatureCollector features) {
        var name = nullIfEmpty(sf.getString("VARDAS"));
        var rawRef = Utils.coalesce(nullIfEmpty(sf.getString("ENUMERIS")), nullIfEmpty(sf.getString("NUMERIS")));

        if (Utils.coalesce(name, rawRef) == null) {
            return;
        }

        var feature = features.line(LAYER_NAME)
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.SUBCLASS, subclass)
                .setAttr(Fields.BRUNNEL, brunnel)
                .setAttr(Fields.LEVEL, level)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0);

        if (rawRef != null) {
            // Handle cases like E67/E272 by getting first road number
            var ref = rawRef.contains("/") ? rawRef.split("/")[0] : rawRef;
            var minZoom = getRefMinZoom(rawRef);

            feature.setAttr(Fields.REF, ref)
                    .setAttr(Fields.REF_LENGTH, ref.length())
                    .setMinZoom(getRefMinZoom(rawRef))
                    .setSortKeyDescending(minZoom);
        } else {
            feature.putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setMinZoom(Math.min(transportMinZoom + 2, 14));
        }
    }

    private static int getRefMinZoom(String ref) {
        if (ref.startsWith("E")) {
            return 8;
        } else if (ref.startsWith("A")) {
            return 9;
        } else {
            return 11;
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
