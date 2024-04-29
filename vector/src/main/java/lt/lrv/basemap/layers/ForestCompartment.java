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

import java.util.List;

public class ForestCompartment implements Layer, ForwardingProfile.FeaturePostProcessor {

    static final double BUFFER_SIZE = 4.0;

    final PlanetilerConfig config;

    public ForestCompartment(PlanetilerConfig config) {
        this.config = config;
    }

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().equals(Layers.GRPK_MISKAS_L) &&
                sf.canBeLine() &&
                "lp3".equals(sf.getTag("GKODAS"))
        ) {
            features.line(this.name())
                    .setBufferPixels(BUFFER_SIZE)
                    .setMinZoom(13);
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        double minLength = config.minFeatureSize(zoom);
        double tolerance = config.tolerance(zoom);

        return FeatureMerge.mergeLineStrings(items, attrs -> minLength, tolerance, BUFFER_SIZE);
    }

    @Override
    public String name() {
        return "forest-compartment";
    }
}
