package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.Layer;
import lt.lrv.basemap.utils.LanguageUtils;

import java.util.List;

// Based on https://github.com/openmaptiles/openmaptiles/pull/1235
public class Power implements Layer, ForwardingProfile.LayerPostProcesser {

    static final double BUFFER_SIZE = 4.0;

    final PlanetilerConfig config;

    public Power(PlanetilerConfig config) {
        this.config = config;
    }

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK)) {
            if (Layers.GRPK_ELEKTR_L.equals(sf.getSourceLayer()) &&
                    "ie6".equals(sf.getTag("GKODAS")) &&
                    sf.canBeLine()
            ) {
                addLineFeature(sf, features);
            } else if (Layers.GRPK_ELEKTR_T.equals(sf.getSourceLayer()) && sf.isPoint()) {
                addPoleFeature(features);
            }
        }
    }

    void addLineFeature(SourceFeature sf, FeatureCollector features) {
        var voltage = sf.getLong("ITAMPA");
        var name = voltage != 0 ? String.format("%d kV", voltage) : null;

        features.line(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, FieldValues.CLASS_LINE)
                .putAttrs(LanguageUtils.getNames(name))
                .setMinPixelSize(0)
                .setPixelTolerance(0)
                .setMinZoom(14);
    }

    void addPoleFeature(FeatureCollector features) {
        features.point(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, FieldValues.CLASS_POLE)
                .setMinZoom(14);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        items = FeatureMerge.mergeMultiPoint(items);

        return FeatureMerge.mergeLineStrings(items, 0, config.tolerance(zoom), BUFFER_SIZE);
    }

    @Override
    public String name() {
        return "power";
    }

    static class Fields {
        public static final String CLASS = "class";
    }

    static class FieldValues {
        public static final String CLASS_POLE = "pole";
        public static final String CLASS_LINE = "line";
    }
}
