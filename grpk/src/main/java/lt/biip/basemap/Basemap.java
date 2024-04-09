package lt.biip.basemap;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import lt.biip.basemap.constants.Layer;
import lt.biip.basemap.constants.Source;
import lt.biip.basemap.layers.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


public class Basemap extends ForwardingProfile {
    // For local development in order to speed up build it's recommended to comment out some GRPK layers
    static final String[] GRPK_LAYERS = {
            Layer.GRPK_GELEZINK,
            Layer.GRPK_HIDRO_L,
            Layer.GRPK_KELIAI,
            Layer.GRPK_PASTAT,
            Layer.GRPK_PLOTAI_PREFIX,
            Layer.GRPK_RIBOS,
            Layer.GRPK_VIETOV_P,
            Layer.GRPK_VIETOV_T,
    };

    public static void main(String[] args) throws Exception {
        var grpkGlobPattern = "{" + String.join(",", GRPK_LAYERS) + "}*.shp";

        Planetiler.create(Arguments.fromConfigFile(Path.of("config.properties")))
                .setProfile(new Basemap())
                .addShapefileGlobSource(
                        null,
                        Source.GRPK,
                        Path.of("data", "sources", "grpk-espg-4326.shp.zip"),
                        grpkGlobPattern,
                        "https://cdn.biip.lt/tiles/sources/grpk/grpk-espg-4326.shp.zip"
                )
                .addShapefileSource(
                        Source.AR,
                        Path.of("data", "sources", "ar-espg-4326.shp.zip"),
                        "https://cdn.biip.lt/tiles/sources/registru-centras/ar-espg-4326.shp.zip"
                )
                .overwriteOutput(Path.of("data", "output", "grpk.pmtiles"))
                .run();

    }

    public Basemap() {
        var handlers = Arrays.asList(
                new SourceProcessors(
                        Source.GRPK,
                        Arrays.asList(
                                new AerodromeLabel(),
                                new Aeroway(),
                                new Boundary(),
                                new Building(),
                                new Landcover(),
                                new Landuse(),
                                new MountainPeak(),
                                new Park(),
                                new Place(),
                                new POI(),
                                new Transportation(),
                                new TransportationName(),
                                new Water(),
                                new WaterName(),
                                new Waterway()
                        )
                ),
                new SourceProcessors(
                        Source.AR,
                        List.of(
                                new HouseNumber()
                        )
                ));

        for (var sourceHandlers : handlers) {
            for (var handler : sourceHandlers.processors) {
                registerSourceHandler(sourceHandlers.source, handler);

                if (handler instanceof Handler) {
                    registerHandler((Handler) handler);
                }
            }
        }
    }

    @Override
    public String name() {
        return "GRPK Vector Map of Lithuania";
    }

    private record SourceProcessors(
            String source,
            List<? extends FeatureProcessor> processors
    ) {

    }
}