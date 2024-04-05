package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Aeroway implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().startsWith("PLOTAI") && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            switch (code) {
                case "va1" -> addFeature("aerodrome", sf, features);
                case "va11" -> addFeature("runway", sf, features);
                case "va12" -> addFeature("helipad", sf, features);
            }
        }
    }


    public void addFeature(String attrClass, SourceFeature sf, FeatureCollector features) {
        features.polygon(this.name())
                .setAttr("class", attrClass)
                .setMinZoom(10);

        var name = sf.getString("VARDAS");
        if (name != null) {
            features.centroid("aerodrome_label")
                    .setAttr("name", name)
                    .setMinZoom(10);
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14)
            return items;

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }

    @Override
    public String name() {
        return "aeroway";
    }
}
