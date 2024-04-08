package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Source;

import java.util.List;

public class Water implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith("PLOTAI") && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");
            var area = sf.getLong("SHAPE_Area");

            if (List.of("hd3", "hd4", "hd9").contains(code) && area > 3000000) {
                addWaterPolygon("lake", 5, sf, features);
            } else if (List.of("hd3", "hd4", "hd9").contains(code) && area > 2500000) {
                addWaterPolygon("lake", 7, sf, features);
            } else if (List.of("hd3", "hd4", "hd9").contains(code)&& area > 2000000) {
                addWaterPolygon("lake", 8, sf, features);
            } else if (List.of("hd3", "hd4", "hd9").contains(code) && area > 1500000) {
                addWaterPolygon("lake", 9, sf, features);
            } else if (List.of("hd3", "hd4", "hd9").contains(code) && area > 1000000) {
                addWaterPolygon("lake", 10, sf, features);
            } else if (List.of("hd3", "hd4", "hd9").contains(code) && area > 500000) {
                addWaterPolygon("lake", 11, sf, features);
            } else if (List.of("hd3", "hd4", "hd9").contains(code)) {
                addWaterPolygon("lake", 12, sf, features);
            }

            else if (List.of("hd1", "hd2").contains(code) && area > 3000000) {
                addWaterPolygon("river", 5, sf, features);
            } else if (List.of("hd1", "hd2").contains(code) && area > 2500000) {
                addWaterPolygon("river", 7, sf, features);
            } else if (List.of("hd1", "hd2").contains(code)&& area > 2000000) {
                addWaterPolygon("river", 8, sf, features);
            } else if (List.of("hd1", "hd2").contains(code) && area > 1500000) {
                addWaterPolygon("river", 9, sf, features);
            } else if (List.of("hd1", "hd2").contains(code) && area > 1000000) {
                addWaterPolygon("river", 10, sf, features);
            } else if (List.of("hd1", "hd2").contains(code) && area > 500000) {
                addWaterPolygon("river", 11, sf, features);
            } else if (List.of("hd1", "hd2").contains(code)) {
                addWaterPolygon("river", 12, sf, features);
            }

            else if (code.equals("hd5")) {
                addWaterPolygon("ocean", 0, sf, features);
            }

        }
    }


    public void addWaterPolygon(String clazz, int minZoom, SourceFeature sf, FeatureCollector features) {
        features.polygon(this.name())
                .setAttr("class", clazz)
                .setMinZoom(minZoom);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14) {
            return items;
        }

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }

    @Override
    public String name() {
        return "water";
    }
}
