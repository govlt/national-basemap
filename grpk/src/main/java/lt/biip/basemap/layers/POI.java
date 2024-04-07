package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Source;
import lt.biip.basemap.utils.LanguageUtils;

public class POI implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
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

    void addFeature(String clazz, SourceFeature sf, FeatureCollector features) {
        features.point("poi")
                .putAttrs(LanguageUtils.getNames(sf.tags()))
                .setAttr("class", clazz)
                .setMinZoom(10);
    }
}
