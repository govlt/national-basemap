# Vector Basemap of Lithuania

Revolutionizing mapping technology for Lithuania with a free and completely open-source vector Basemap of Lithuania with
no
restrictions on usage. This project utilizes cutting-edge vector mapping technology, akin to giants like Google Maps,
Apple Maps, and Mapbox, and is compliant with the VectorMapTiles standard.

## Key features

1. **Lightning-Fast Updates**: vector basemap updates are lightning-fast, taking approximately ~2 minutes to build on an
   M1 MacBook and ~6 minutes on a free GitHub runner.
2. **Compact and Efficient**: Unlike traditional raster-based technologies, vector basemap of Lithuania occupies only
   ~350 MB as a single file. Say goodbye to managing millions of image files!
3. **Instant Style Changes**: Customize your maps on the fly with instant style changes. No more waiting for complete
   rebuilds as with raster based maps.
4. **Precision and Accuracy**: Utilizing data from the Georeferenced Cadastral
   Register ([GRPK](https://www.geoportal.lt/geoportal/web/georeferencinio-pagrindo-kadastras-grpk))
   and [address registry](https://www.registrucentras.lt/p/1187), vector basemap offers unparalleled accuracy.
5. **Infrastructure Flexibility**: Seamlessly integrate vector basemap into your existing infrastructure with just one
   style url or self-host it independently. Enjoy the freedom to choose what works best for you.
6. **Various Self-Hosting Options**: Explore multiple self-hosting options, including hosting a single
   ~350MB [PMTiles](https://docs.protomaps.com/pmtiles/) file and style files in your S3
   or file storage. Or using our provided docker image based on Martin tile server.
7. **OpenVectorTiles Standard**: We adhere to the [VectorMapTiles](https://openmaptiles.org/about/) standard, allowing
   effortless integration of various
   open-source styles, including unconventional ones like the [Pirate style](https://openmaptiles.org/styles/) 🏴‍☠️
8. **Efficient Data Delivery**: Vector tiles are designed for compactness and optimization, ensuring lightning-fast data
   delivery over the internet, making map smoother and more responsive.
9. **Easy Usage**: With [Pseudo-Mercator](https://en.wikipedia.org/wiki/Web_Mercator_projection) projection (EPSG:3857),
   integration is seamless. Vector basemap aligns effortlessly with popular map services like Google Maps and
   OpenStreetMap.
10. **Completely Open Source and Free**: Join our community of contributors and users in shaping the future of mapping.
    Everything, from the basemap building process to its usage, is open-source and free of restrictions.

## Usage

Using vector basemap is straightforward. Just follow these steps:

1. **Choose a Style**: Select the style you prefer from the available options listed [below](#styles).

2. **Integrate with Your Library**: Incorporate the chosen style into your favorite mapping library.

Here's a basic example using OpenLayers and the [ol-mapbox-style](https://github.com/openlayers/ol-mapbox-style)
library:

```js
import Map from 'ol/Map.js';
import { MapboxVectorLayer } from 'ol-mapbox-style';

const map = new Map({
  target: 'map',
  layers: [
    new MapboxVectorLayer({
      styleUrl: 'https://cdn.biip.lt/tiles/grpk/styles/bright/style.json'
    })
  ]
});
```

You can follow a similar process with [MapLibre GL JS](https://maplibre.org/maplibre-gl-js/docs/) or any other mapping
library you prefer.

### Styles

Currently, the following styles are available:

- **Topographic (Light)**: Offers detailed data representation. Style
  URL: `https://cdn.biip.lt/tiles/grpk/styles/bright/style.json` (based
  on [OSM Bright](https://openmaptiles.org/styles/#osm-bright)).
- **Gray**: Provides a subtle basemap. Style URL: `https://cdn.biip.lt/tiles/grpk/styles/positron/style.json` (based
  on [Positron](https://openmaptiles.org/styles/#positron)).

You can explore these styles and their features using [Maputnik](https://maplibre.org/maputnik/#6/55.59/23.54). These
styles are already utilized by the BĮIP team in production.

Additionally, the basemap data source is compatible with the OpenMapTiles standard, allowing you to utilize any
OpenMapTiles compatible style. For instance, you can use any styles from
the [OpenMapTiles styles](https://openmaptiles.org/styles/). Use this style source
URL: `https://gis.biip.lt/basemap/grpk/grpk`.

### PMTiles

If you need to use vector basemap offline or prefer to avoid using a tile server, you can directly read PMTiles
archives.

The latest stable basemap PMTiles archive is hosted at https://cdn.biip.lt/tiles/grpk/grpk.pmtiles. You can inspect it
using
the [PMTiles viewer](https://protomaps.github.io/PMTiles/?url=https%3A%2F%2Fcdn.biip.lt%2Ftiles%2Fgrpk%2Fgrpk.pmtiles).

For instructions on reading PMTiles directly, refer to
the [PMTiles in the browser](https://docs.protomaps.com/pmtiles/maplibre) documentation.

## Self-hosting

You have the option to host the vector basemap on your own infrastructure.

### Vector Tile Server

Utilize the provided Docker
image [biip-grpk-basemap](https://github.com/AplinkosMinisterija/biip-vector-basemap/pkgs/container/biip-grpk-basemap),
which includes a vector tile server based on [Martin Tile Server](https://maplibre.org/martin/).
This Docker image embeds PMTiles archive, fonts, and sprites inside it, enabling it to serve vector tiles on-the-fly.

Here's an example of its usage with Docker Compose:

```yaml
services:
  biip-grpk-basemap:
    image: ghcr.io/aplinkosministerija/biip-grpk-basemap:stable
    pull_policy: always
    restart: unless-stopped
    ports:
      - "3000:3000"
    healthcheck:
      test: [ "CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:3000/health || exit 1" ]
      interval: 5s
      timeout: 3s
      start_period: 5s
      retries: 5
```

### PMTiles

Periodically download the PMTiles archive from `https://cdn.biip.lt/tiles/grpk/grpk.pmtiles` to your own S3 or file
storage and utilize it as needed.

## Getting Started Development

To embark on your mapping journey, follow these simple steps:

- **Install Java 21+:** Ensure Java is installed on your system to power up the mapping engine.

## Instructions

### Generating GRPK Basemap

To generate the basemap in PMTiles format, execute the following command:

```shell
make grpk-generate-basemap
```

The generated PMTiles will be saved in `grpk/data/output/grpk.pmtiles`.

This process may take some time as the GRPK data source will be downloaded if it doesn't exist already.

#### Previewing

Before previewing, make sure you have [Docker](https://www.docker.com/get-started/) installed, preferably with Docker
Compose version 2.22 and later.

After generating the GRPK basemap, execute the following command:

```shell
make grpk-preview
```

This will start:

- [Tileserver-GL](https://github.com/maptiler/tileserver-gl) at http://localhost:8080, which allows previewing PMTiles
  archive
  and styles;
- [Martin tile server](https://martin.maplibre.org/) at http://localhost:3000, serving PMTiles, fonts, and sprites.
  Visit http://localhost:3000/catalog for more details;
- [Maputnik](https://maplibre.org/maputnik/) at http://localhost:8000, used for style editing;

Note: Docker will watch all required directories, so you don't need to rerun this command once PMTiles are regenerated
or styles are changed.

## Contributing

Calling all GIS enthusiasts! Your expertise is invaluable to us. Whether you spot issues or have groundbreaking ideas,
feel free to open an issue or submit a pull request. Dive into
our [contribution guidelines](https://github.com/AplinkosMinisterija/.github/blob/main/CONTRIBUTING.md) for more
insights.

## License

This project is licensed under the [MIT License](./LICENSE), inviting you to explore, adapt, and contribute to our
mapping adventure!

Ready to map out Lithuania like never before? Join us on this exciting journey! 🌍✨