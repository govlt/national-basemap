package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

import java.util.List;

public class HouseNumber implements OpenMapTilesSchema.Housenumber, ForwardingProfile.FeaturePostProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.AR) && sf.isPoint()) {
            features.point(this.name())
                    .setBufferPixels(BUFFER_SIZE)
                    .setAttr(Fields.HOUSENUMBER, sf.getTag("NR"))
                    .setMinZoom(14);
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int i, List<VectorTile.Feature> items) {
        return FeatureMerge.mergeMultiPoint(items);
    }
}
