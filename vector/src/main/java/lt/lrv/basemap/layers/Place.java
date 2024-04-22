package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;

public class Place implements OpenMapTilesSchema.Place {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layers.GRPK_VIETOV_T) && sf.isPoint()) {
            var code = sf.getString("GKODAS");

            // TODO: add ANTR at higher zoom levels
            if (sf.getString("ANTR", "").isEmpty()) {
                switch (code) {
                    case "uas1" -> addFeature(FieldValues.CLASS_COUNTRY, 0, sf, features);
                    case "uas2" -> addFeature(FieldValues.CLASS_PROVINCE, 4, sf, features);
                    case "uas511" -> addFeature(FieldValues.CLASS_CITY, 6, sf, features);
                    case "uas512" -> addFeature(FieldValues.CLASS_TOWN, 11, sf, features);
                    case "uas52" -> addFeature(FieldValues.CLASS_VILLAGE, 12, sf, features);
                    case "uas53" -> addFeature(FieldValues.CLASS_SUBURB, 13, sf, features);
                    case "uas54" -> addFeature(FieldValues.CLASS_ISOLATED_DWELLING, 14, sf, features);
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

        features.point(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .putAttrs(LanguageUtils.getNames(sf.tags()))
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.CAPITAL, capital)
                .setMinZoom(minZoom);
    }
}
