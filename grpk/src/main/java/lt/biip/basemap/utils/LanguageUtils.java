package lt.biip.basemap.utils;

import java.util.HashMap;
import java.util.Map;

import static com.onthegomap.planetiler.util.LanguageUtils.putIfNotEmpty;
import static com.onthegomap.planetiler.util.LanguageUtils.string;

public class LanguageUtils {

    private LanguageUtils() {
    }

    public static Map<String, Object> getNames(Map<String, Object> tags) {
        var result = new HashMap<String, Object>();

        var name = string(tags.get("VARDAS"));

        putIfNotEmpty(result, "name", name);
        putIfNotEmpty(result, "name:latin", name);

        return result;
    }
}
