package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.utils.LanguageUtils;

import static com.google.common.base.Strings.emptyToNull;

public class POI implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().equals(Layer.GRPK_VIETOV_T) &&
                sf.isPoint() &&
                emptyToNull(sf.getString("VARDAS")) != null &&
                emptyToNull(sf.getString("ANTR")) == null
        ) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "uur14" -> addFeature("park", 5, sf, features);
                case "uvp1" -> addFeature("cemetery", 10, sf, features);
                case "unk0" -> addFeature("unknown", 15, sf, features);
            }
        }
    }

    void addFeature(String clazz, int rank, SourceFeature sf, FeatureCollector features) {
        features.point("poi")
                .setBufferPixels(64)
                .putAttrs(LanguageUtils.getNames(sf.tags()))
                .setAttr("class", clazz)
                .setAttr("rank", rank)
                .setAttr("level", 0)
                .setMinZoom(10)
                .setPointLabelGridPixelSize(14, 64)
                .setSortKey(rank);
    }

}
