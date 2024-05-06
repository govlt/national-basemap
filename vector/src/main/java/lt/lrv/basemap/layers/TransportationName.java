package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;
import lt.lrv.basemap.utils.Utils;

import javax.annotation.Nonnull;
import java.util.List;

import static com.onthegomap.planetiler.util.LanguageUtils.nullIfEmpty;

public class TransportationName implements OpenMapTilesSchema.TransportationName, ForwardingProfile.FeaturePostProcessor {

    final PlanetilerConfig config;

    public TransportationName(PlanetilerConfig config) {
        this.config = config;
    }

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        // Currently TransportationName processFeature is handled inside Transportation
    }

    public static void addFeature(String clazz, String subclass, int transportMinZoom, SourceFeature sf, FeatureCollector features) {
        var name = nullIfEmpty(sf.getString("VARDAS"));
        var ref = nullIfEmpty(sf.getString("NUMERIS"));

        if (Utils.coalesce(name, ref) == null) {
            return;
        }

        // brunnel attribute is excluded from road name line features so that tunnel and bridges don't prevent merging!
        var feature = features.line(LAYER_NAME)
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.SUBCLASS, subclass)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0);

        if (ref != null) {
            var network = getNetwork(ref);
            var minZoom = switch (network) {
                case NetworkValue.LT_MOTORWAY -> 7;
                case NetworkValue.LT_PRIMARY -> 9;
                default -> 11;
            };

            feature.setAttr(Fields.REF, ref)
                    .setAttr(Fields.REF_LENGTH, ref.length())
                    .setAttr(Fields.NETWORK, network)
                    .setMinZoom(Math.max(minZoom, transportMinZoom))
                    .setSortKeyDescending(minZoom);
        } else {
            var minZoom = Math.max(12, Math.min(transportMinZoom + 2, 14));
            feature.putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setMinZoom(minZoom);
        }
    }

    static String getNetwork(@Nonnull String ref) {
        if (ref.startsWith("A")) {
            return NetworkValue.LT_MOTORWAY;
        } else if (ref.length() == 3) {
            return NetworkValue.LT_PRIMARY;
        } else {
            return NetworkValue.LT_SECONDARY;
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return FeatureMerge.mergeLineStrings(
                items,
                config.minFeatureSize(zoom),
                config.tolerance(zoom),
                BUFFER_SIZE
        );
    }

    static final class NetworkValue {
        static final String LT_MOTORWAY = "lt-motorway";
        static final String LT_PRIMARY = "lt-primary";
        static final String LT_SECONDARY = "lt-secondary";
    }
}
