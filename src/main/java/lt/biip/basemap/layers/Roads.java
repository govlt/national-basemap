package lt.biip.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.biip.basemap.utils.FeatureZoomRange;
import org.apache.commons.lang3.ObjectUtils;

import javax.xml.transform.Source;
import java.util.Arrays;
import java.util.List;

public class Roads implements ForwardingProfile.FeaturePostProcessor, ForwardingProfile.FeatureProcessor {

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (!sf.canBeLine()) {
            return;
        }

        var zoomRange = getZoomRangeV2(sf);

        var label = ObjectUtils.firstNonNull(sf.getTag("ENUMERIS"), sf.getTag("NUMERIS"));

        features.line(this.name())
                .setAttr("gkodas", sf.getTag("GKODAS"))
                .setAttr("kategor", sf.getTag("KATEGOR"))
                .setAttr("enumeris", sf.getTag("ENUMERIS"))
                .setAttr("numeris", sf.getTag("NUMERIS"))
                .setAttr("lygmuo", sf.getTag("LYGMUO"))
                .setAttr("paskirtis", sf.getTag("PASKIRTIS"))
                .setAttr("vardas", sf.getTag("VARDAS"))
                .setAttr("minZoom", zoomRange.minZoom())
                .setAttr("maxZoom", zoomRange.maxZoom())
                .setAttr("label", label)
                .setMinPixelSize(0.0)
                .setPixelTolerance(0.0)
                .setZoomRange(zoomRange.minZoom(), zoomRange.maxZoom())
                .setSortKeyDescending(zoomRange.minZoom());

    }

    FeatureZoomRange getZoomRangeV2(SourceFeature sf) {
        var gkodas = sf.getTag("GKODAS") != null ? sf.getTag("GKODAS").toString() : "null";
        var paskirtis = sf.getTag("PASKIRTIS") != null ? sf.getTag("PASKIRTIS").toString() : "null";
        var lygmuo = sf.getTag("LYGMUO") != null ? Integer.parseInt(sf.getTag("LYGMUO").toString()) : -999;
        var kategor = sf.getTag("KATEGOR") != null ? sf.getTag("KATEGOR").toString() : null;
        var numeris = sf.getTag("NUMERIS") != null ? sf.getTag("NUMERIS").toString() : null;

        if (Arrays.asList("A12", "A5", "A6", "A1", "A11", "E28", "E67", "E77", "E85", "E262", "E272").contains(numeris)) {
            return new FeatureZoomRange(0, 24);
        }

        if (Arrays.asList("AM", "1", "2").contains(kategor)) {
            return new FeatureZoomRange(0, 24);
        }

        if (Arrays.asList("PAGR", "P/Z").contains(paskirtis)) {
            return new FeatureZoomRange(0, 24);
        }


        if (gkodas.equals("dc2")) {
            return new FeatureZoomRange(0, 24);
        }

        if (Arrays.asList("gc2", "gc12", "gc14").contains(gkodas) && lygmuo < 0) {
            return new FeatureZoomRange(0, 24);
        }

        if (Arrays.asList("4", "5").contains(kategor) && gkodas.equals("gc12") && lygmuo < 0) {
            return new FeatureZoomRange(0, 24);
        }

        if (Arrays.asList("2", "3").contains(kategor) && lygmuo < 0) {
            return new FeatureZoomRange(0, 24);
        }

        return new FeatureZoomRange(10, 24);
    }


    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return items;
//        return FeatureMerge.mergeLineStrings(
//                items,
//                0.5, // after merging, remove lines that are still less than 0.5px long
//                0.1, // simplify output linestrings using a 0.1px tolerance
//                4.0 // remove any detail more than 4px outside the tile boundary
//        );
    }

    @Override
    public String name() {
        return "keliai";
    }
}
