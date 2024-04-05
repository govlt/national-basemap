package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Place implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("VIETOV_T") && sf.isPoint()) {
            var code = sf.getString("GKODAS");

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

    void addFeature(String attrClass, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.point("place")
                .setAttr("class", attrClass)
                .setAttr("name", sf.getTag("VARDAS"))
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setAttr("adm_tip", sf.getTag("ADM_TIP"))
                .setMinZoom(minZoom);
    }
}
