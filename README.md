# National Basemap of Lithuania

Revolutionizing mapping technology for Lithuania with a free and completely open-source vector Basemap of Lithuania with
no
restrictions on usage. This project utilizes cutting-edge vector mapping technology, akin to giants like Google Maps,
Apple Maps, and Mapbox, and is compliant with the OpenMapTiles standard.

## Key features

1. **Lightning-Fast Updates**: vector basemap updates are lightning-fast, taking approximately ~2 minutes to build on an
   M1 MacBook and ~6 minutes on a free GitHub runner.
2. **Compact and Efficient**: Unlike traditional raster-based technologies, vector basemap of Lithuania occupies only
   ~350 MB as a single file. Say goodbye to managing millions of image files!
3. **Efficient Data Delivery**: The average vector tile size is approximately 20 KB[^1], with a maximum tile size of
   less than 80 KB. This ensures lightning-fast data delivery over the internet, resulting in smoother and
   more responsive maps.
4. **Instant Style Changes**: Customize your maps on the fly with instant style changes. No more waiting for complete
   rebuilds as with raster based maps.
5. **Precision and Accuracy**: Utilizing data from the Georeferenced Cadastral
   Register ([GRPK](https://www.geoportal.lt/geoportal/web/georeferencinio-pagrindo-kadastras-grpk)), [State Cadastre of Protected Areas](https://stvk.lt)
   and [address registry](https://www.registrucentras.lt/p/1187), vector basemap offers unparalleled accuracy.
6. **Infrastructure Flexibility**: Seamlessly integrate vector basemap into your existing infrastructure with just one
   style url or self-host it independently. Enjoy the freedom to choose what works best for you.
7. **Various Self-Hosting Options**: Explore multiple self-hosting options, including hosting a single
   ~350MB [PMTiles](https://docs.protomaps.com/pmtiles/) file and style files in your S3
   or file storage. Or using our provided docker image based on Martin tile server.
8. **OpenMapTiles Standard**: We adhere to the [OpenMapTiles](https://openmaptiles.org/about/) standard, allowing
   effortless integration of various
   open-source styles, including unconventional ones like the [Pirate style](https://openmaptiles.org/styles/) 🏴‍☠️
9. **Easy Usage**: With [Pseudo-Mercator](https://en.wikipedia.org/wiki/Web_Mercator_projection) projection (EPSG:3857),
   integration is seamless. Vector basemap aligns effortlessly with popular map services like Google Maps and
   OpenStreetMap.
10. **Completely Open Source and Free**: Join our community of contributors and users in shaping the future of mapping.
    Everything, from the basemap building process to its usage, is open-source and free of restrictions.

[^1]:
    20 KB is gzipped average vector tile size is calculated using weighted average based on OSM traffic. It wouldn't
    be fair to take average of all tiles, because tile sizes of sea are less than 1 KB.

## Usage

Using vector basemap is straightforward. Just follow these steps:

1. **Choose a Style**: Select the style you prefer from the available options listed [below](#styles).

2. **Integrate with Your Library**: Incorporate the chosen style into your favorite mapping library.

Here's a basic example using OpenLayers and the [ol-mapbox-style](https://github.com/openlayers/ol-mapbox-style)
library:

```js
import Map from "ol/Map.js";
import { MapboxVectorLayer } from "ol-mapbox-style";

const map = new Map({
  target: "map",
  layers: [
    new MapboxVectorLayer({
      styleUrl: "https://basemap.biip.lt/styles/bright/style.json",
    }),
  ],
});
```

You can follow a similar process with [MapLibre GL JS](https://maplibre.org/maplibre-gl-js/docs/) or any other mapping
library you prefer.

### Styles

Currently, the following styles are available:

- **Topographic (Light)**: Offers detailed data representation. Style
  URL: `https://basemap.biip.lt/styles/bright/style.json` (based
  on [OSM Bright](https://openmaptiles.org/styles/#osm-bright)).
- **Gray**: Provides a subtle basemap. Style URL: `https://basemap.biip.lt/styles/positron/style.json` (
  based on [Positron](https://openmaptiles.org/styles/#positron)).

You can explore these styles and their features using [Maputnik](https://maplibre.org/maputnik/#6/55.59/23.54). These
styles are already utilized by the BĮIP team in production.

Additionally, the basemap data source is compatible with the OpenMapTiles standard, allowing you to utilize any
OpenMapTiles compatible style. For instance, you can use any styles from
the [OpenMapTiles styles](https://openmaptiles.org/styles/).

### PMTiles

If you need to use vector basemap offline or prefer to avoid using a tile server, you can directly read PMTiles
archives.

The latest stable basemap PMTiles archive is hosted
at `https://cdn.biip.lt/tiles/vector/pmtiles/lithuania.pmtiles`. You can
inspect it using
the [PMTiles viewer](https://protomaps.github.io/PMTiles/?url=https%3A%2F%2Fcdn.biip.lt%2Ftiles%2Fvector%2Fpmtiles%2Flithuania.pmtiles).

For instructions on reading PMTiles directly, refer to
the [PMTiles in the browser](https://docs.protomaps.com/pmtiles/maplibre) documentation.

## Self-hosting

You have the option to host the vector basemap on your own infrastructure.

### Docker Vector Tiles

Utilize the provided Docker
image [national-basemap-vector](https://github.com/govlt/national-basemap/pkgs/container/national-basemap-vector),
which includes vector tiles, fonts, and sprites, enabling it to serve vector tiles on-the-fly.

Here's an example of its usage with Docker Compose:

```yaml
services:
  national-basemap-vector:
    image: ghcr.io/govlt/national-basemap-vector:stable
    pull_policy: always
    restart: unless-stopped
    environment:
      # Change to your host
      HOST: https://vector.yourdomain.com
    ports:
      - "80:80"
    healthcheck:
      test:
        [
          "CMD-SHELL",
          "wget --no-verbose --tries=1 --spider http://127.0.0.1:80/health || exit 1",
        ]
      interval: 5s
      timeout: 3s
      start_period: 5s
      retries: 5
```

### Docker Vector Tiles with Martin Tile Server

Utilize the provided Docker image [national-basemap-vector-martin](https://github.com/govlt/national-basemap/pkgs/container/national-basemap-vector-martin), which includes PMTiles archive, style JSONs, fonts, and sprites served by [Martin tile server](https://martin.maplibre.org/). This image provides a complete vector tile solution with built-in styles.

Here's an example of its usage with Docker Compose:

```yaml
services:
  national-basemap-vector-martin:
    image: ghcr.io/govlt/national-basemap-vector-martin:stable
    pull_policy: always
    restart: unless-stopped
    environment:
      # Change to your host URL
      HOST: https://basemap.yourdomain.com
    ports:
      - "3000:3000"
    healthcheck:
      test:
        [
          "CMD-SHELL",
          "wget --no-verbose --tries=1 --spider http://127.0.0.1:3000/health || exit 1",
        ]
      interval: 5s
      timeout: 3s
      start_period: 5s
      retries: 5
```

#### Available Styles

Once deployed, the following styles will be available:

- **Bright Style**: `https://basemap.yourdomain.com/style/bright` - Topographic (Light) style
- **Positron Style**: `https://basemap.yourdomain.com/style/positron` - Gray basemap style
- **OpenMapTiles Style**: `https://basemap.yourdomain.com/style/openmaptiles` - Standard OpenMapTiles style

#### Usage Example

```js
import Map from "ol/Map.js";
import { MapboxVectorLayer } from "ol-mapbox-style";

const map = new Map({
  target: "map",
  layers: [
    new MapboxVectorLayer({
      styleUrl: "https://basemap.yourdomain.com/style/bright",
    }),
  ],
});
```

**Note**: The `HOST` environment variable automatically updates all style URLs to match your deployment domain, ensuring proper font and sprite

### PMTiles

Periodically download the PMTiles archive from `https://cdn.biip.lt/tiles/vector/pmtiles/lithuania.pmtiles` to
your own S3
or file storage and utilize it as needed.

### Individual Vector Tile Files

You can host MapBox vector tile files with a zxy directory structure yourself. For example, there are only **51,888**
individual
MapBox vector tile files for the entire national basemap of Lithuania!

To get started:

1. Download the [tiles archive](https://cdn.biip.lt/tiles/vector/mvt/tiles.zip).
2. Upload the extracted files to your preferred file storage, such as AWS S3.
3. Alternatively, use a reverse proxy server like Caddy, Apache, or Nginx to serving static files.

You can also host these tiles on static websites like GitHub Pages, which is a cost-effective option that doesn't
require a server.

## Recipes

### Extracting Basemap for a Specific Area

To extract a basemap for a specific area, you can utilize the [PMTiles CLI](https://docs.protomaps.com/pmtiles/cli).
This tool allows you to specify either a bounding box or a shape for extraction.

For example, if you want to extract the basemap for Vilnius Old Town, you can use the following command:

```bash
pmtiles extract https://cdn.biip.lt/tiles/vector/pmtiles/lithuania.pmtiles vilnius-old-town.pmtiles --bbox=25.276352,54.694638,25.302195,54.671628
```

The resulting basemap for Vilnius Old Town occupies less than 1 MB!

## Architecture

```mermaid
flowchart TD
    grpk["GeoPortal.lt<br><a href="https://www.geoportal.lt/geoportal/web/georeferencinio-pagrindo-kadastras-grpk">Georeferenced Cadastral Register (GRPK)</a>"]-->transform-grpk["<a href="https://github.com/govlt/national-basemap/blob/main/.github/workflows/basemap-vector-data-source.yml">Transform</a>"]-->|"<a href="https://cdn.biip.lt/tiles/vector/sources/grpk/grpk-espg-4326.shp.zip">grpk-espg-4326.shp.zip</a>"|S3
    ar["State Enterprise Centre of Registers<br><a href="https://www.registrucentras.lt/p/1187">Address Registry</a>"]-->transform-ar["<a href="https://github.com/govlt/national-basemap/blob/main/.github/workflows/basemap-vector-data-source.yml">Transform</a>"]-->|"<a href="https://cdn.biip.lt/tiles/vector/sources/address-registry/houses-espg-4326.gpkg.zip">houses-espg-4326.gpkg.zip</a>"|S3
    stvk["State service for protected areas<br><a href="https://stvk.lt/">State Cadastre of Protected Areas</a>"]-->transform-stvk["<a href="https://github.com/govlt/national-basemap/blob/main/.github/workflows/basemap-vector-data-source.yml">Transform</a>"]-->|"<a href="https://cdn.biip.lt/tiles/vector/sources/stvk/stvk-4326.gpkg.zip">stvk-4326.gpkg.zip</a>"|S3
S3-->Planetiler-->PMTiles["PMTiles archive"]

PMTiles-->s3-pmtiles["S3<br><a href="https://cdn.biip.lt/tiles/vector/pmtiles/lithuania.pmtiles">lithuania.pmtiles</a>"]

PMTiles-->mvt["Mapbox Vector Tiles"]
mvt-->tiles["S3<br><a href="https://cdn.biip.lt/tiles/vector/mvt/tiles.zip">tiles.zip</a>"]
mvt-->docker-image["Docker image<br><a href="https://github.com/govlt/national-basemap/pkgs/container/national-basemap-vector">national-basemap-vector</a>"]
```

## Getting Started Development

To embark on your mapping journey, follow these simple steps:

- **Install Java 21+:** Ensure Java is installed on your system to power up the mapping engine.

## Instructions

### Generating basemap vector

To generate the vector basemap in PMTiles format, execute the following command:

```shell
make vector-basemap-generate
```

The generated PMTiles will be saved in `vector/data/output/lithuania.pmtiles`.

This process may take some time as data sources will be downloaded if they don't exist already.

#### Previewing

Before previewing, make sure you have [Docker](https://www.docker.com/get-started/) installed, preferably with Docker
Compose version 2.22 and later.

After generating the vector basemap, execute the following command:

```shell
make vector-basemap-preview
```

This will start:

- [Tileserver-GL](https://github.com/maptiler/tileserver-gl) at http://localhost:8080, which allows previewing PMTiles
  archive
  and styles;
- [Martin tile server](https://martin.maplibre.org/) at http://localhost:3000, serving PMTiles, fonts, and sprites.
  Visit http://localhost:3000/catalog for more details;
- [Maputnik](https://maplibre.org/maputnik/) at http://localhost:8888, used for style editing;

Note: Docker will watch all required directories, so you don't need to rerun this command once PMTiles are regenerated
or styles are changed.

## Frequently Asked Questions

### Can the national vector map based on the Web Mercator projection be used with state cadastres and registers?

There is a myth that state cadastre and register maps can only use the LKS-94 coordinate system. However, this is not a
requirement. Spatial data for cadastres and registers must be collected and managed in the LKS-94 coordinate system.

When it comes to online maps, this is a matter of implementation. For example, the Protected Areas Cadastre online map
is currently published in Web Mercator, while the data is collected and managed in LKS-94. Therefore, the national
vector map base can be used for state cadastre and register maps.

## Contributing

Calling all GIS enthusiasts! Your expertise is invaluable to us. Whether you spot issues or have groundbreaking ideas,
feel free to open an issue or submit a pull request. Dive into
our [contribution guidelines](https://github.com/govlt/.github/blob/main/CONTRIBUTING.md) for more insights.

## License

This project is licensed under the [MIT License](./LICENSE), inviting you to explore, adapt, and contribute to our
mapping adventure!

### Attribution Requirements for Styles

When using the provided styles, please ensure to include the appropriate copyright notices found within the style files.
For more information about licensing see:

- **Bright**: See [LICENSE.md](https://github.com/openmaptiles/osm-bright-gl-style/blob/master/LICENSE.md)
- **Positron**: See [LICENSE.md](https://github.com/openmaptiles/positron-gl-style/blob/master/LICENSE.md)

### Data Attribution

If you are using data with your own styles, you may need to attribute OpenMapTiles, as the generated layers are based on
the OpenMapTiles schema. For more details, visit
the [OpenMapTiles schema documentation](https://openmaptiles.org/schema/).

Ready to map out Lithuania like never before? Join us on this exciting journey! 🌍✨
