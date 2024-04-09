package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;
import lt.biip.basemap.utils.LanguageUtils;
import org.geotools.process.geometry.CenterLine;

public class WaterName implements ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) &&
                sf.canBePolygon() &&
                !sf.getString("VARDAS", "").isBlank()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "hd3", "hd4", "hd9" -> addWaterCenterLine("lake", 12, sf, features);
                case "hd5" -> addWaterCenterLine("ocean", 6, sf, features);
            }
        }
    }

    void addWaterCenterLine(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        try {
            var geom = CenterLine.getCenterLine(sf.polygon(), 1);

            features.geometry("water_name", geom)
                    .setAttr("class", clazz)
                    .putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setMinPixelSize(0.0)
                    .setPixelTolerance(0.0)
                    .setMinZoom(minZoom);

        } catch (GeometryException e) {
            throw new RuntimeException(e);
        }
    }
}