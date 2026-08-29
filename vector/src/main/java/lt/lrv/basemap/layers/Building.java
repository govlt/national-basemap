package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.expression.Expression;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

import java.util.List;

public class Building implements OpenMapTilesSchema.Building, ForwardingProfile.LayerPostProcessor {

    @Override
    public Expression filter() {
        return Expression.and(
                Expression.matchSource(Source.GRPK),
                Expression.matchSourceLayer(Layers.GRPK_PASTAT),
                Expression.matchType(Expression.POLYGON_TYPE)
        );
    }

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        features.polygon(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setMinZoom(13);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14) {
            return FeatureMerge.mergeMultiPolygon(items);
        }

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }
}
