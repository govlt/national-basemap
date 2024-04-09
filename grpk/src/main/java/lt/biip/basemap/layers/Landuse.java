package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;

import java.util.List;

public class Landuse implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");
            var area = sf.getLong("SHAPE_Area");

            if (code.equals("pu0") && area > 100000) {
                addPolygon("residential", "residential", 5, features);
            } else if (code.equals("pu0") && area > 50000) {
                addPolygon("residential", "residential", 7, features);
            } else if (code.equals("pu0") && area > 25000) {
                addPolygon("residential", "residential", 8, features);
            } else if (code.equals("pu0") && area > 10000) {
                addPolygon("residential", "residential", 9, features);
            } else if (code.equals("pu0") && area > 5000) {
                addPolygon("residential", "residential", 10, features);
            } else if (code.equals("pu0") && area > 1000) {
                addPolygon("residential", "residential", 11, features);
            } else if (code.equals("pu0")) {
                addPolygon("residential", "residential", 12, features);
            }

            else if (code.equals("pu3") && area > 100000) {
                addPolygon("industrial", "industrial", 5, features);
            } else if (code.equals("pu3") && area > 50000) {
                addPolygon("industrial", "industrial", 7, features);
            } else if (code.equals("pu3") && area > 25000) {
                addPolygon("industrial", "industrial", 8, features);
            } else if (code.equals("pu3") && area > 10000) {
                addPolygon("industrial", "industrial", 9, features);
            } else if (code.equals("pu3") && area > 5000) {
                addPolygon("industrial", "industrial", 10, features);
            } else if (code.equals("pu3") && area > 1000) {
                addPolygon("industrial", "industrial", 11, features);
            } else if (code.equals("pu3")) {
                addPolygon("industrial", "industrial", 12, features);
            }

            else if (code.equals("vp1") && area > 5000) {
                addPolygon("cemetery", "cemetery", 10, features);
            } else if (code.equals("vp1")) {
                addPolygon("cemetery", "cemetery", 12, features);
            }

            else if (code.equals("ek0") && area > 5000) {
                addPolygon("quarry", "quarry", 10, features);
            } else if (code.equals("ek0")) {
                addPolygon("quarry", "quarry", 12, features);
            }

            else if (code.equals("vg3") && area > 5000) {
                addPolygon("landfill", "landfill", 10, features);
            } else if (code.equals("vg3")) {
                addPolygon("landfill", "landfill", 12, features);
            }

            else if (List.of("gt17", "gt18", "gt19").contains(code) && area > 5000) {
                addPolygon("railway", "railway", 10, features);
            } else if (List.of("gt17", "gt18", "gt19").contains(code)) {
                addPolygon("railway", "railway", 12, features);
            }

            else if (code.equals("vk1")) {
                addPolygon("stadium", "stadium", 10, features);
            }
        }
    }


    public void addPolygon(String clazz, String subclass, int minZoom, FeatureCollector features) {
        features.polygon(this.name())
                .setAttr("class", clazz)
                .setAttr("subclass", subclass)
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
        return "landuse";
    }
}
