package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Strings.emptyToNull;

public class Place implements OpenMapTilesSchema.Place {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().equals(Layers.GRPK_VIETOV_T) &&
                sf.isPoint() &&
                emptyToNull(sf.getString("ANTR")) == null
        ) {
            var code = sf.getString("GKODAS");
            var adm_type = sf.getString("ADM_TIP");
            var pop = sf.getLong("GYVSK");

            if (Objects.equals(code, "uas2")) {
                addFeature(FieldValues.CLASS_PROVINCE, 4, sf, features);
            } else if (Objects.equals(code, "uas511") && List.of("SOST", "APSK").contains(adm_type)) {
                addFeature(FieldValues.CLASS_CITY, 6, sf, features);
            } else if (Objects.equals(code, "uas511") && (Objects.equals(adm_type, "SAV"))) {
                addFeature(FieldValues.CLASS_CITY, 7, sf, features);
            } else if (Objects.equals(code, "uas511")) {
                addFeature(FieldValues.CLASS_TOWN, 8, sf, features);
            } else if (Objects.equals(code, "uas512")) {
                addFeature(FieldValues.CLASS_TOWN, 9, sf, features);
            } else if (Objects.equals(code, "uas52") && pop > 200) {
                addFeature(FieldValues.CLASS_VILLAGE, 10, sf, features);
            } else if (Objects.equals(code, "uas52") && pop > 50) {
                addFeature(FieldValues.CLASS_VILLAGE, 11, sf, features);
            } else if (Objects.equals(code, "uas52")) {
                addFeature(FieldValues.CLASS_VILLAGE, 12, sf, features);
            } else if (Objects.equals(code, "uas53")) {
                addFeature(FieldValues.CLASS_SUBURB, 11, sf, features);
            } else if (Objects.equals(code, "uas54")) {
                addFeature(FieldValues.CLASS_ISOLATED_DWELLING, 14, sf, features);
            }
        }
    }

    void addFeature(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        var admTip = sf.getString("ADM_TIP");
        var capital = "SOST".equals(admTip) ? 2 : null;

        var rank = switch (admTip) {
            case "SOST" -> 2;
            case "APSK" -> 4;
            case "SAV" -> 6;
            case "SEN" -> 8;
            default -> 12;
        };

        features.point(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .putAttrs(LanguageUtils.getNames(sf.tags()))
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.CAPITAL, capital)
                .setAttr(Fields.RANK, rank)
                .setMinZoom(minZoom);
    }
}
