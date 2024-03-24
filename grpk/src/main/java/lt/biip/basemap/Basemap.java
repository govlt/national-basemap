package lt.biip.basemap;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import lt.biip.basemap.layers.*;

import java.nio.file.Path;
import java.util.Arrays;


public class Basemap extends ForwardingProfile {

    public static void main(String[] args) throws Exception {
        Planetiler.create(Arguments.fromConfigFile(Path.of("config.properties")))
                .setProfile(new Basemap())
                .addShapefileSource(
                        "grpk",
                        Path.of("data", "sources", "grpk", "grpk-espg-4326-shp.zip"),
                        "https://cdn.biip.lt/tiles/sources/grpk/grpk-espg-4326-shp.zip"

                )
                .overwriteOutput(Path.of("data", "output", "grpk", "grpk.pmtiles"))
                .run();

    }

    public Basemap() {
        var grpkHandlers =
                Arrays.asList(
                        new Areas(),
                        new Boundaries(),
                        new Buildings(),
                        new ForestLines(),
                        new PlaceTitles(),
                        new Railway(),
                        new Roads(),
                        new WaterLines()
                );

        for (var handler : grpkHandlers) {
            registerHandler(handler);
            registerSourceHandler("grpk", handler);
        }
    }

    @Override
    public String name() {
        return "GRPK Vector Map of Lithuania";
    }
}

