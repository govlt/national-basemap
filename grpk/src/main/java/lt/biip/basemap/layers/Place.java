package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;
import lt.biip.basemap.utils.LanguageUtils;

public class Place implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layer.GRPK_VIETOV_T) && sf.isPoint()) {
            var code = sf.getString("GKODAS");

            // TODO: add ANTR at higher zoom levels
            if (sf.getString("ANTR", "").isEmpty()) {
                switch (code) {
                    case "uas1" -> addFeature("country", 0, sf, features);
                    case "uas2" -> addFeature("province", 4, sf, features);
                    case "uas511" -> addFeature("city", 6, sf, features);
                    case "uas512" -> addFeature("town", 11, sf, features);
                    case "uas52" -> addFeature("village", 12, sf, features);
                    case "uas53" -> addFeature("suburb", 13, sf, features);
                    case "uas54" -> addFeature("isolated_dwelling", 14, sf, features);
                }
            }
        }
    }

    void addFeature(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        var capital = switch (sf.getString("ADM_TIP")) {
            case "SOST" -> 2;
            case "APSK" -> 4;
            case "SAV" -> 5;
            case "SEN" -> 6;
            default -> null;
        };

        features.point("place")
                .putAttrs(LanguageUtils.getNames(sf.tags()))
                .setAttr("class", clazz)
                .setAttr("capital", capital)
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setAttr("adm_tip", sf.getTag("ADM_TIP"))
                .setMinZoom(minZoom);
    }
}
