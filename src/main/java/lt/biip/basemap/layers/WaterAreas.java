package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class WaterAreas implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (!sf.canBePolygon()) {
            return;
        }

        var minZoom = 6;
        var code = sf.getTag("GKODAS");
        if (code == "hd5") {
            minZoom = 0;
        }

        features.polygon(this.name())
                .inheritAttrFromSource("TOP_ID")
                .inheritAttrFromSource("GKODAS")
                .inheritAttrFromSource("VARDAS")
                .inheritAttrFromSource("META")
                .setBufferPixels(8)
                .setMinZoom(minZoom)
                .setSortKey(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        return FeatureMerge.mergeOverlappingPolygons(items, 1);
    }

    @Override
    public String name() {
        return "water-areas";
    }
}
