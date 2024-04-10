# Vector Basemap of Lithuania 🗺️

Revolutionizing mapping technology for Lithuania with a free and completely open-source vector Basemap of Lithuania with no
restrictions on usage. This project utilizes cutting-edge vector mapping technology, akin to giants like Google Maps,
Apple Maps, and Mapbox, and is compliant with the VectorMapTiles standard.

## Key features

1. **Lightning-Fast Updates**: vector basemap updates are lightning-fast, taking approximately ~2 minutes to build on an
   M1 MacBook and ~6 minutes on a free GitHub runner.
2. **Compact and Efficient**: Unlike traditional raster-based technologies, vector basemap of Lithuania occupies only
   ~350 MB as a single file. Say goodbye to managing millions of image files!
3. **Instant Style Changes**: Customize your maps on the fly with instant style changes. No more waiting for complete
   rebuilds as with raster based maps.
4. **Precision and Accuracy**: Utilizing data from
   the [Georeferenced Cadastral Register (GRPK)]((https://www.geoportal.lt/geoportal/web/georeferencinio-pagrindo-kadastras-grpk))
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

## Getting Started 🚀

To embark on your mapping journey, follow these simple steps:

- **Install Java 21+:** Ensure Java is installed on your system to power up the mapping engine.

## Instructions 📝

### Generating GRPK Basemap

To generate the basemap in pmtiles format, execute the following command:

```shell
make grpk-generate-basemap
```

The generated pmtiles will be saved in `grpk/data/output/grpk.pmtiles`.

This process may take some time as the GRPK data source will be downloaded if it doesn't exist already.

#### Previewing

Before previewing, make sure you have [Docker](https://www.docker.com/get-started/) installed, preferably with Docker
Compose version 2.22 and later.

After generating the GRPK basemap, execute the following command:

```shell
make grpk-preview
```

This will start:

- [Tileserver-GL](https://github.com/maptiler/tileserver-gl) at http://localhost:8080, which allows previewing pmtiles
  and styles;
- [Martin tile server](https://martin.maplibre.org/) at http://localhost:3000, serving pmtiles, fonts, and sprites.
  Visit http://localhost:3000/catalog for more details;
- [Maputnik](https://maplibre.org/maputnik/) at http://localhost:8000, used for style editing;

Note: Docker will watch all required directories, so you don't need to rerun this command once pmtiles are regenerated
or styles are changed.

## Contributing 🤝

Calling all GIS enthusiasts! Your expertise is invaluable to us. Whether you spot issues or have groundbreaking ideas,
feel free to open an issue or submit a pull request. Dive into
our [contribution guidelines](https://github.com/AplinkosMinisterija/.github/blob/main/CONTRIBUTING.md) for more
insights.

## License 📄

This project is licensed under the [MIT License](./LICENSE), inviting you to explore, adapt, and contribute to our
mapping adventure!

Ready to map out Lithuania like never before? Join us on this exciting journey! 🌍✨