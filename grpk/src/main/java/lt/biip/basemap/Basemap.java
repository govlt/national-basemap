package lt.biip.basemap;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import lt.biip.basemap.layers.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


public class Basemap extends ForwardingProfile {

    public static final String SOURCE_GRPK = "grpk";
    public static final String SOURCE_AR = "ar";

    public static void main(String[] args) throws Exception {
        Planetiler.create(Arguments.fromConfigFile(Path.of("config.properties")))
                .setProfile(new Basemap())
                .addShapefileSource(
                        SOURCE_GRPK,
                        Path.of("data", "sources", "grpk-espg-4326.shp.zip"),
                        "https://cdn.biip.lt/tiles/sources/grpk/grpk-espg-4326.shp.zip"
                )
                .addShapefileSource(
                        SOURCE_AR,
                        Path.of("data", "sources", "ar-espg-4326.shp.zip"),
                        "https://cdn.biip.lt/tiles/sources/registru-centras/ar-espg-4326.shp.zip"
                )
                .overwriteOutput(Path.of("data", "output", "grpk.pmtiles"))
                .run();

    }

    public Basemap() {
        var handlers = Arrays.asList(
                new SourceProcessors(
                        SOURCE_GRPK,
                        Arrays.asList(
                                new Landcover(),
                                new Boundary(),
                                new Building(),
                                new Place(),
                                new Transportation(),
                                new Landuse(),
                                new Waterway(),
                                new Water(),
                                new Aeroway()
                        )
                ),
                new SourceProcessors(
                        SOURCE_AR,
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


