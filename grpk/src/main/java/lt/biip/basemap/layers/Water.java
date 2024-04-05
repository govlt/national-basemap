package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Water implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().startsWith("PLOTAI") && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "hd1", "hd2" -> addWaterPolygon("river", sf, features);
                case "hd3", "hd4", "hd9" -> addWaterPolygon("lake", sf, features);
                case "hd5" -> addWaterPolygon("ocean", sf, features);
            }
        }
    }


    public void addWaterPolygon(String attrClass, SourceFeature sf, FeatureCollector features) {
        features.polygon(this.name())
                .setAttr("class", attrClass)
                .setAttr("name", sf.getTag("VARDAS"))
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setMinZoom(2);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14)
            return items;

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }

    @Override
    public String name() {
        return "water";
    }
}
