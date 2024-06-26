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

import java.util.List;

import static lt.lrv.basemap.layers.Park.FieldValues.*;

public class Park implements OpenMapTilesSchema.Park, ForwardingProfile.LayerPostProcesser {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) && sf.getSourceLayer().equals(Layers.GRPK_VIETOV_P) && sf.canBePolygon()) {
            var code = sf.getString("GKODAS");

            if (code.equals("uur14")) {
                addPolygon(CLASS_PUBLIC_PARK, 9, sf.getString("VARDAS"), 5, features);
            }
        } else if (sf.getSource().equals(Source.STVK) && sf.canBePolygon()) {
            var name = sf.getString("pavadinimas");

            switch (sf.getSourceLayer()) {
                case "nac_parkai" -> addPolygon(CLASS_NATIONAL_PARK, 7, name, 1, features);
                case "reg_parkai" -> addPolygon(CLASS_NATIONAL_PARK, 9, name, 2, features);
                case "valstybiniai_draustiniai" -> addPolygon(CLASS_PROTECTED_AREA, 11, name, 3, features);
                case "valstybiniai_rezervatai" -> addPolygon(CLASS_PROTECTED_AREA, 9, name, 4, features);
            }
        }
    }

    public void addPolygon(String clazz, int minZoom, String name, int sortKey, FeatureCollector features) {
        features.polygon(this.name())
                .setBufferPixels(BUFFER_SIZE)
                .setAttr(Fields.CLASS, clazz)
                .putAttrs(LanguageUtils.getNames(name))
                .setMinZoom(minZoom)
                .setSortKeyDescending(sortKey);
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
        if (zoom >= 14) {
            return FeatureMerge.mergeMultiPolygon(items);
        }

        return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5);
    }

    static class FieldValues {
        public static final String CLASS_PUBLIC_PARK = "public_park";
        public static final String CLASS_NATIONAL_PARK = "national_park";
        public static final String CLASS_PROTECTED_AREA = "protected_area";
    }
}
