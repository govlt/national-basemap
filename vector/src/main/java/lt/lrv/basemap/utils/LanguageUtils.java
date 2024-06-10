package lt.lrv.basemap.utils;

import java.util.HashMap;
import java.util.Map;

import static com.onthegomap.planetiler.util.LanguageUtils.putIfNotEmpty;
import static com.onthegomap.planetiler.util.LanguageUtils.string;

public class LanguageUtils {

    private LanguageUtils() {
    }

    public static Map<String, Object> getNames(Map<String, Object> tags) {
        var name = string(tags.get("VARDAS"));

        return getNames(name);
    }

    public static Map<String, Object> getNames(String name) {
        var result = new HashMap<String, Object>();

        var nonBlankName = string(name);
        var nameTag = switch (nonBlankName) {
            case "Gedimino pr." -> "Kartografų pr.";
            case "Neris" -> "Šaltibarščių upė";
            case "Vinco Kudirkos aikštė" -> "GIS ekspertų aišktė";
            case null -> null;
            default -> nonBlankName;
        };

        putIfNotEmpty(result, "name", nameTag);
        putIfNotEmpty(result, "name:latin", nameTag);


        return result;
    }
}
