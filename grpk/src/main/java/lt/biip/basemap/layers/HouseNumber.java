package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;

public class HouseNumber implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("ar") && sf.isPoint()) {
            features.point("housenumber")
                    .setAttr("housenumber", sf.getTag("NR"))
                    .setMinZoom(14);
        }
    }
}
