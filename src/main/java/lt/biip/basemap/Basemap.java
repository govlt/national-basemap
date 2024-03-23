package lt.biip.basemap;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import lt.biip.basemap.layers.*;

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
                        "gelezink",
                        Path.of("data", "sources", "layers", "gelezink.gpkg"),
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
                .addGeoPackageSource(
                        "hidro-l",
                        Path.of("data", "sources", "layers", "hidro-l.gpkg"),
                        "https://google.lt"
                )
                .addGeoPackageSource(
                        "plotai",
                        Path.of("data", "sources", "layers", "plotai.gpkg"),
                        "https://google.lt"
                )
                .addGeoPackageSource(
                        "vietov_t",
                        Path.of("data", "sources", "layers", "vietov_t.gpkg"),
                        "https://google.lt"
                )
                .addGeoPackageSource(
                        "miskas_l",
                        Path.of("data", "sources", "layers", "miskas_l.gpkg"),
                        "https://google.lt"
                )
                .overwriteOutput(Path.of("data", "output", "grpk", "grpk.pmtiles"))
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
        var waterLines = new WaterLines();
        registerHandler(waterLines);
        registerSourceHandler("hidro-l", waterLines);
        var areas = new Areas();
        registerHandler(areas);
        registerSourceHandler("plotai", areas);
        var railways = new Railway();
        registerHandler(railways);
        registerSourceHandler("gelezink", railways);
        var placeTitles = new PlaceTitles();
        registerHandler(placeTitles);
        registerSourceHandler("vietov_t", placeTitles);
        var forestLines = new ForestLines();
        registerHandler(forestLines);
        registerSourceHandler("miskas_l", forestLines);

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

