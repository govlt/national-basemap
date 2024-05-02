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
            var minZoom = ref.startsWith("A") ? 8 : 11;

            feature.setAttr(Fields.REF, ref)
                    .setAttr(Fields.REF_LENGTH, ref.length())
                    .setMinZoom(minZoom)
                    .setSortKeyDescending(minZoom);
        } else {
            feature.putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setMinZoom(Math.min(transportMinZoom + 2, 14));
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
}
