package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;

import static com.google.common.base.Strings.emptyToNull;

public class Poi implements OpenMapTilesSchema.Poi {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().equals(Layer.GRPK_VIETOV_P) &&
                sf.canBePolygon() &&
                emptyToNull(sf.getString("VARDAS")) != null
        ) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "uur14" -> addFeature(FieldValues.CLASS_PARK, 5, sf, features);
                case "uvu11" -> addFeature(FieldValues.CLASS_HARBOR, 1, sf, features);
                case "ums0", "uhd6", "uhd10" -> addFeature(null, 15, sf, features);
            }
        } else if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().equals(Layer.GRPK_VIETOV_T) &&
                sf.isPoint() &&
                emptyToNull(sf.getString("VARDAS")) != null &&
                emptyToNull(sf.getString("ANTR")) == null
        ) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "uur14" -> addFeature(FieldValues.CLASS_PARK, 5, sf, features);
                case "uvp1" -> addFeature(FieldValues.CLASS_CEMETERY, 10, sf, features);
                case "unk0" -> {
                    switch (sf.getString("OBJ_TIP")) {
                        case "SPORT" -> addFeature(FieldValues.CLASS_STADIUM, 10, sf, features);
                        case "KRAŠT" -> addFeature(FieldValues.CLASS_ATTRACTION, 10, sf, features);
                        default -> addFeature(null, 15, sf, features);
                    }
                }
            }
        }
    }

    void addFeature(String clazz, int rank, SourceFeature sf, FeatureCollector features) {
        var feature = sf.isPoint() ? features.point(this.name()) : features.centroidIfConvex(this.name());

        feature.setBufferPixels(BUFFER_SIZE)
                .putAttrs(LanguageUtils.getNames(sf.tags()))
                .setAttr(Fields.CLASS, clazz)
                .setAttr(Fields.RANK, rank)
                .setAttr(Fields.LEVEL, 0)
                .setMinZoom(12)
                .setPointLabelGridPixelSize(14, 64)
                .setSortKey(rank);
    }

}
