package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Source;

public class HouseNumber implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.AR) && sf.isPoint()) {
            features.point("housenumber")
                    .setBufferPixels(8)
                    .setAttr("housenumber", sf.getTag("NR"))
                    .setMinZoom(14);
        }
    }
}
