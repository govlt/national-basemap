package lt.biip.basemap;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import lt.biip.basemap.layers.Boundaries;
import lt.biip.basemap.layers.Buildings;
import lt.biip.basemap.layers.Roads;

import java.nio.file.Path;


public class Basemap extends ForwardingProfile {

    public static void main(String[] args) throws Exception {
        Planetiler.create(Arguments.fromConfigFile(Path.of("config.properties")))
                .setProfile(new Basemap())
                .addGeoPackageSource(
                        "keliai",
                        Path.of("data", "sources", "layers", "keliai.gpkg"),
                        "https://google.lt"
                )
                .addGeoPackageSource(
                        "pastat",
                        Path.of("data", "sources", "layers", "pastat.gpkg"),
                        "https://google.lt"
                )
                .addGeoPackageSource(
                        "ribos",
                        Path.of("data", "sources", "layers", "ribos.gpkg"),
                        "https://google.lt"
                )
                .overwriteOutput(Path.of("data", "biip-maps.mbtiles"))
                .run();

    }

    public Basemap() {
        var roads = new Roads();
        registerHandler(roads);
        registerSourceHandler("keliai", roads);
        var buildings = new Buildings();
        registerHandler(buildings);
        registerSourceHandler("pastat", buildings);
        var boundaries = new Boundaries();
        registerHandler(boundaries);
        registerSourceHandler("ribos", boundaries);
    }

    @Override
    public String name() {
        return "BIIP Basemap";
    }

    @Override
    public String description() {
        return "Basemap layer derived from OpenPortal GRPK";
    }

    @Override
    public String version() {
        return "0.0.1";
    }

    @Override
    public boolean isOverlay() {
        return false;
    }
}

