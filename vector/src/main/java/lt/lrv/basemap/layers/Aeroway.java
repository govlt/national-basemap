package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

public class Aeroway implements OpenMapTilesSchema.Aeroway {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "va1" -> addFeature(FieldValues.CLASS_AERODROME, features);
                case "va11" -> addFeature(FieldValues.CLASS_RUNWAY, features);
                case "va12" -> addFeature(FieldValues.CLASS_HELIPAD, features);
            }
        }
    }


    public void addFeature(String clazz, FeatureCollector features) {
        features.polygon(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .setMinZoom(10)
                .setMinPixelSize(2);
    }
}
