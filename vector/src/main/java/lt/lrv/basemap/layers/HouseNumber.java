package lt.lrv.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.openmaptiles.OpenMapTilesSchema;

public class HouseNumber implements OpenMapTilesSchema.Housenumber {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.AR) && sf.isPoint()) {
            features.point(this.name())
                    .setBufferPixels(BUFFER_SIZE)
                    .setAttr(Fields.HOUSENUMBER, sf.getTag("NR"))
                    .setMinZoom(14);
        }
    }
}
