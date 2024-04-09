package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;
import lt.biip.basemap.utils.LanguageUtils;

import java.util.List;

public class Boundary implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layer.GRPK_RIBOS) && sf.canBeLine()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "as1", "fas1" -> addBoundaryFeature(2, sf, features);
                case "as2" -> addBoundaryFeature(4, sf, features);
                case "as3" -> addBoundaryFeature(5, sf, features);
                case "as51" -> addBoundaryFeature(8, sf, features);
            }
        }
    }

    void addBoundaryFeature(int adminLevel, SourceFeature sf, FeatureCollector features) {
        features.line(this.name())
                .setAttr("admin_level", adminLevel)
                .setAttr("disputed", 0)
                // TODO determine if border is maritime or not
                .setAttr("maritime", 0)
                .putAttrs(LanguageUtils.getNames(sf.tags()));
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        if (zoom >= 14) {
            return items;
        }

        return FeatureMerge.mergeLineStrings(
                items,
                0,
                0,
                4
        );
    }

    @Override
    public String name() {
        return "boundary";
    }
}
