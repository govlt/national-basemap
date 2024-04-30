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

        putIfNotEmpty(result, "name", nonBlankName);
        putIfNotEmpty(result, "name:latin", nonBlankName);

        return result;
    }
}
