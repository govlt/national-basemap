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
                case LT_MOTORWAY -> 8;
                case LT_PRIMARY -> 10;
                case LT_SECONDARY -> 11;
            };

            feature.setAttr(Fields.REF, ref)
                    .setAttr(Fields.REF_LENGTH, ref.length())
                    .setAttr(Fields.NETWORK, network.toString())
                    .setMinZoom(Math.max(minZoom, transportMinZoom))
                    .setSortKeyDescending(minZoom);
        } else {
            feature.putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setMinZoom(Math.min(transportMinZoom + 2, 14));
        }
    }

    static Network getNetwork(@Nonnull String ref) {
        if (ref.startsWith("A")) {
            return Network.LT_MOTORWAY;
        } else if (ref.length() == 3) {
            return Network.LT_PRIMARY;
        } else {
            return Network.LT_SECONDARY;
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

    private enum Network {
        LT_MOTORWAY("lt-motorway"),
        LT_PRIMARY("lt-primary"),
        LT_SECONDARY("lt-secondary");

        private final String networkName;

        Network(String networkName) {
            this.networkName = networkName;
        }

        @Override
        public String toString() {
            return networkName;
        }
    }
}
