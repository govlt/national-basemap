# Vector Map of Lithuania 🗺️

Welcome to the exciting world of mapping Lithuania! Dive into the beauty and functionality of our free and open-source
map project inspired by [Proton Maps](https://protomaps.com/).

## What's Inside?

This project offers an early-stage experiment in creating a free and open source vector map for Lithuania.

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