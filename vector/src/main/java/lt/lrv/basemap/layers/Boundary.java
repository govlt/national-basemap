package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;

import java.util.List;

public class Boundary implements OpenMapTilesSchema.Boundary, ForwardingProfile.FeaturePostProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layer.GRPK_RIBOS) && sf.canBeLine()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "as1" -> addBoundaryFeature(2, 0, sf, features);
                case "as2" -> addBoundaryFeature(4, 5, sf, features);
                case "as3" -> addBoundaryFeature(5, 8, sf, features);
                case "as51" -> addBoundaryFeature(8, 10, sf, features);
            }
        }
    }

    void addBoundaryFeature(int adminLevel, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.line(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.ADMIN_LEVEL, adminLevel)
                .setAttr(Fields.DISPUTED, 0)
                // TODO determine if border is maritime or not
                .setAttr(Fields.MARITIME, 0)
                .setMinZoom(minZoom)
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
}
