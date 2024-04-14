package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

public class MountainPeak implements OpenMapTilesSchema.MountainPeak {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        // TODO: add mountain_peak layer https://openmaptiles.org/schema/#mountain_peak
    }
}
