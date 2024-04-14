package lt.lrv.basemap.utils;

public class Utils {
    private Utils() {
    }

    public static <T> T coalesce(T a, T b) {
        return a != null ? a : b;
    }
}
