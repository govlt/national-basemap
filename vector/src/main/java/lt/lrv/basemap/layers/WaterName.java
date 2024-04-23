package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;
import org.geotools.process.geometry.CenterLine;
import java.util.List;
import static java.lang.Math.toIntExact;


public class WaterName implements OpenMapTilesSchema.WaterName, ForwardingProfile.FeaturePostProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().startsWith(Layers.GRPK_PLOTAI_PREFIX) &&
                sf.canBePolygon() &&
                !sf.getString("VARDAS", "").isBlank()) {
            var code = sf.getString("GKODAS");
            var area = sf.getLong("SHAPE_Area");

            switch (code) {
                case "hd3", "hd4", "hd9" -> {
                    if (area >= 5_000_000) {
                        addWaterCenterLine(FieldValues.CLASS_LAKE, 12, sf, features);
                    } else if (area >= 500_000) {
                        addWaterCenterLine(FieldValues.CLASS_LAKE, 13, sf, features);
                    } else {
                        addWaterCenterLine(FieldValues.CLASS_LAKE, 14, sf, features);
                    }
                }
                case "hd5" -> addWaterCenterLine(FieldValues.CLASS_OCEAN, 6, sf, features);
            }
        }
    }

    void addWaterCenterLine(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        try {
            var geom = CenterLine.getCenterLine(sf.polygon(), 1);
            var area = toIntExact(sf.getLong("SHAPE_Area") / 1000);

            features.geometry(this.name(), geom)
                    .setBufferPixels(BUFFER_SIZE)
                    .setAttr(Fields.CLASS, clazz)
                    .putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setMinPixelSize(0.0)
                    .setPixelTolerance(0.0)
                    .setSortKeyDescending(area)
                    .setMinZoom(minZoom);

        } catch (GeometryException e) {
            throw new RuntimeException(e);
        }
    }

    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return FeatureMerge.mergeMultiLineString(items);
    }
}