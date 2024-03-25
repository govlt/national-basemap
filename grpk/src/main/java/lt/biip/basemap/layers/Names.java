package lt.biip.basemap.layers;


import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

import java.util.List;

public class Names implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals("grpk") && sf.getSourceLayer().equals("VIETOV_P") && sf.canBePolygon()) {
            try {
                var area = sf.area();

                int minZoom;
                if (area > 20000000) {
                    minZoom = 9;
                } else if (area > 10000000) {
                    minZoom = 10;
                } else if (area > 4000000) {
                    minZoom = 11;
                } else {
                    minZoom = 13;
                }

                features.polygon(this.name())
                        .setAttr("gkodas", sf.getTag("GKODAS"))
                        .setAttr("vardas", sf.getTag("VARDAS"))
                        .setAttr("plotas", sf.getTag("PLOTAS"))
                        .setMinZoom(minZoom)
                        .setSortKeyDescending((int) area);
            } catch (GeometryException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return items;
    }

    @Override
    public String name() {
        return "vietov_p";
    }
}
