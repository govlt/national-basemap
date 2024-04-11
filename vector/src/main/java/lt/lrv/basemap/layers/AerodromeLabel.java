package lt.lrv.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;
import lt.lrv.basemap.constants.Layer;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.utils.LanguageUtils;

import java.util.Arrays;
import java.util.List;

public class AerodromeLabel implements ForwardingProfile.FeatureProcessor {

    // Some airports have multiple features and this leads to duplicated names. Use this to filter out them
    static final List<String> IGNORED_TOP_IDS = Arrays.asList(
            "f3be26af-0417-4ed7-960b-8685510ebc66",
            "be01462c-dfa3-4132-8321-b66616fc3b9d",
            "656ce44d-6a3d-4fa8-93eb-ef6401344e4b",
            "a593a5f0-7e9f-410c-8ca1-a0d22b7f00f7",
            "656ce44d-6a3d-4fa8-93eb-ef6401344e4b"
    );

    @Override
    public void processFeature(SourceFeature sf, FeatureCollector features) {
        if (sf.getSource().equals(Source.GRPK) &&
                sf.getSourceLayer().startsWith(Layer.GRPK_PLOTAI_PREFIX) &&
                sf.canBePolygon() &&
                sf.getString("GKODAS", "").equals("va1") &&
                !sf.getString("VARDAS", "").isBlank() &&
                !IGNORED_TOP_IDS.contains(sf.getString("TOP_ID", ""))) {
            var name = sf.getString("VARDAS");

            var iata = getIATA(name);
            var icao = getICAO(name);

            var isInternational = iata != null;
            var clazz = isInternational ? "international" : "regional";


            features.centroid("aerodrome_label")
                    .setMinZoom(isInternational ? 8 : 10)
                    .putAttrs(LanguageUtils.getNames(sf.tags()))
                    .setAttr("iata", iata)
                    .setAttr("icao", icao)
                    .setAttr("class", clazz);
        }
    }

    String getIATA(String name) {
        return switch (name) {
            case "Tarptautinis Vilniaus oro uostas" -> "VNO";
            case "Tarptautinis Kauno oro uostas" -> "KUN";
            case "Tarptautinis Palangos oro uostas" -> "PLQ";
            case "Tarptautinis Šiaulių oro uostas" -> "SQQ";
            default -> null;
        };
    }

    String getICAO(String name) {
        return switch (name) {
            case "Tarptautinis Vilniaus oro uostas" -> "EYVI";
            case "Tarptautinis Kauno oro uostas" -> "EYKA";
            case "Tarptautinis Palangos oro uostas" -> "EYPA";
            case "Tarptautinis Šiaulių oro uostas" -> "EYSA";
            default -> null;
        };
    }
}
