package lt.lrv.basemap;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import lt.lrv.basemap.constants.Layers;
import lt.lrv.basemap.constants.Source;
import lt.lrv.basemap.layers.*;
import lt.lrv.basemap.openmaptiles.Layer;

import java.nio.file.Path;


public class Basemap extends ForwardingProfile {
    // For local development in order to speed up build it's recommended to comment out some GRPK layers
    static final String[] GRPK_LAYERS = {
            Layers.GRPK_GELEZINK,
            Layers.GRPK_HIDRO_L,
            Layers.GRPK_KELIAI,
            Layers.GRPK_PASTAT,
            Layers.GRPK_PLOTAI_PREFIX,
            Layers.GRPK_RIBOS,
            Layers.GRPK_VIETOV_P,
            Layers.GRPK_VIETOV_T,
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
                        "https://cdn.startupgov.lt/tiles/vector/sources/grpk/grpk-espg-4326.shp.zip"
                )
                .addShapefileSource(
                        Source.AR,
                        Path.of("data", "sources", "house-numbers-espg-4326.shp.zip"),
                        "https://cdn.startupgov.lt/tiles/vector/sources/address-registry/house-numbers-espg-4326.shp.zip"
                )
                .overwriteOutput(Path.of("data", "output", "lithuania.pmtiles"))
                .run();

    }

    public Basemap() {
        var handlers = new SourceProcessors[]{
                new SourceProcessors(
                        Source.GRPK,
                        new Layer[]{
                                new AerodromeLabel(),
                                new Aeroway(),
                                new Boundary(),
                                new Building(),
                                new Landcover(),
                                new Landuse(),
                                new MountainPeak(),
                                new Park(),
                                new Place(),
                                new Poi(),
                                new Transportation(),
                                new TransportationName(),
                                new Water(),
                                new WaterName(),
                                new Waterway(),
                        }
                ),
                new SourceProcessors(
                        Source.AR,
                        new Layer[]{
                                new HouseNumber()
                        }
                )
        };

        for (var sourceHandlers : handlers) {
            for (var layer : sourceHandlers.layers) {
                registerSourceHandler(sourceHandlers.source, layer);
                registerHandler(layer);
            }
        }
    }

    @Override
    public String name() {
        return "National Vector Basemap of Lithuania";
    }

    private record SourceProcessors(
            String source,
            Layer[] layers
    ) {

    }
}