package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

import java.util.List;

public class Park implements OpenMapTilesSchema.Park, ForwardingProfile.FeaturePostProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layer.GRPK_VIETOV_P) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            if (code.equals("uur14")) {
                features.polygon(this.name())
                        .setBufferPixels(BUFFER_SIZE)
                        .setAttr(Fields.CLASS, "nature_reserve")
                        .setMinZoom(12);
            }
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14) {
            return items;
        }

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }
}
