package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;
import lt.lrv.basemap.utils.LanguageUtils;
import org.geotools.process.geometry.CenterLine;

public class WaterName implements OpenMapTilesSchema.WaterName {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) &&
                sf.canBePolygon() &&
                !sf.getString("VARDAS", "").isBlank()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "hd3", "hd4", "hd9" -> addWaterCenterLine(FieldValues.CLASS_LAKE, 12, sf, features);
                case "hd5" -> addWaterCenterLine(FieldValues.CLASS_OCEAN, 6, sf, features);
            }
        }
    }

    void addWaterCenterLine(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        try {
            var geom = CenterLine.getCenterLine(sf.polygon(), 1);

            features.geometry(this.name(), geom)
                    .setBufferPixels(BUFFER_SIZE)
                    .setAttr(Fields.CLASS, clazz)
                    .putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setMinPixelSize(0.0)
                    .setPixelTolerance(0.0)
                    .setMinZoom(minZoom);

        } catch (GeometryException e) {
            throw new RuntimeException(e);
        }
    }
}