package lt.biip.basemap;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.store.ReprojectingFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class BuildingNumbers {


    static final String CSV_SEPARATOR = "|";


    public static void main(String[] args) throws Exception {

        var lookup = getHouseNumberLookupByCode();
        var features = getReprojectedAddressFeatures();

        var geometryFactory = JTSFactoryFinder.getGeometryFactory();


        var ftBuilder = new SimpleFeatureTypeBuilder();
        var schema = features.getSchema();
        ftBuilder.setName(schema.getName());
        ftBuilder.setSuperType((SimpleFeatureType) schema.getSuper());
        ftBuilder.addAll(schema.getAttributeDescriptors());
        ftBuilder.add("NR", String.class);

        var nSchema = ftBuilder.buildFeatureType();

        var houseNumberFeatures = new ArrayList<SimpleFeature>();

        try (var iterator = features.features()) {
            while (iterator.hasNext()) {
                var feature = iterator.next();

                var code = (long) feature.getAttribute("AOB_KODAS");
                var number = lookup.get(code);

                if (number != null) {
                    var builder = SimpleFeatureBuilder.build(nSchema, feature.getAttributes(), feature.getID());

                    builder.setAttribute("NR", number);

                    houseNumberFeatures.add(builder);
                } else {
                    System.err.println("Unable to find house number for feature ID=" + feature.getID() + " with AOB_KODAS=" + code);
                }
            }
        }

        var houseNumberCollection = DataUtilities.collection(houseNumberFeatures);


        FeatureJSON fj = new FeatureJSON();
        fj.writeFeatureCollection(houseNumberCollection, "housenumbers.geojson");
    }

    static Map<Long, String> getHouseNumberLookupByCode() throws IOException {
        var uri = URI.create("https://www.registrucentras.lt/aduomenys/?byla=adr_stat_lr.csv").toURL();

        try (var inputStream = uri.openStream();
             var inputStreamReader = new InputStreamReader(inputStream);
             var br = new BufferedReader(inputStreamReader)
        ) {
            var lookup = new HashMap<Long, String>();

            var headers = Arrays.asList(br.readLine().split(Pattern.quote(CSV_SEPARATOR)));

            var indexCode = headers.indexOf("AOB_KODAS");
            var indexNumber = headers.indexOf("NR");

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(Pattern.quote(CSV_SEPARATOR));

                var code = Long.valueOf(data[indexCode]);
                var number = data[indexNumber];
                lookup.put(code, number);
            }

            return lookup;
        }
    }


    static SimpleFeatureCollection getReprojectedAddressFeatures() throws IOException, FactoryException {
        var uri = URI.create("https://www.registrucentras.lt/aduomenys/?byla=adr_gra_adresai_LT.zip").toURL();

        try (var inputStream = uri.openStream();
             var zipStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                var filename = entry.getName();
                System.out.println("File name: " + filename);

                if (filename.equals("adr_gra_adresai_LT.json")) {
                    var featureCollection = new FeatureJSON().readFeatureCollection(zipStream);

                    return DataUtilities.simple(featureCollection);
                }
            }
        }
        throw new RuntimeException("Unable to parse addresses geojson");
    }
}
