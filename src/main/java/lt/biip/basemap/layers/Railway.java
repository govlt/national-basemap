package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.utils.FeatureZoomRange;

import java.util.Arrays;
import java.util.List;

public class Railway implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (!sf.canBeLine()) {
            return;
        }

        var zoomRange = getZoomRange(sf);

        features.line(this.name())
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setAttr("tipas", sf.getTag("TIPAS"))
                .setAttr("lygmuo", sf.getTag("LYGMUO"))
                .setAttr("minZoom", zoomRange.minZoom())
                .setAttr("maxZoom", zoomRange.maxZoom())
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0)
                .setZoomRange(zoomRange.minZoom(), zoomRange.maxZoom())
                .setSortKeyDescending(zoomRange.minZoom());

    }

    FeatureZoomRange getZoomRange(SourceFeature sf) {
        var gkodas = sf.getTag("GKODAS") != null ? sf.getTag("GKODAS").toString() : "null";
        var tipas = sf.getTag("TIPAS") != null ? sf.getTag("GKODAS").toString() : "null";

        if (Arrays.asList("gz1", "gz2", "gz1gz2").contains(gkodas) && tipas.equals("1")) {
            return new FeatureZoomRange(11, 24);
        }

        return new FeatureZoomRange(0, 24);
    }


    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return items;
    }

    @Override
    public String name() {
        return "gelezink";
    }
}
