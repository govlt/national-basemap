package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;

public class POI implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") &&
                sf.getSourceLayer().equals("VIETOV_T") &&
                sf.isPoint() &&
                !sf.getString("VARDAS", "").isBlank()
        ) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "uur14" -> addFeature("park", sf, features);
                case "uvp1" -> addFeature("cemetery", sf, features);
            }
        }
    }

    void addFeature(String attrClass, SourceFeature sf, FeatureCollector features) {
        features.point("poi")
                .setAttr("name", sf.getTag("VARDAS"))
                .setAttr("class", attrClass)
                .setMinZoom(10);
    }
}
