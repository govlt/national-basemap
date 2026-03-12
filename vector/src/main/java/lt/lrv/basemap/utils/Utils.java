package lt.lrv.basemap.utils;

public class Utils {
    private Utils() {
    }

    public static <T> T coalesce(T a, T b) {
        return a != null ? a : b;
    }

    public static String toStringOrNull(Object obj) {
        if (obj == null) return null;

        var s = obj.toString();
        return s.isEmpty() ? null : s;
    }
}
