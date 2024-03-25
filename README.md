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
make grpk-basemap
```

The generated pmtiles will be saved in `grpk/data/output/grpk.pmtiles`.

This process may take some time as the GRPK data source will be downloaded if it doesn't exist already.

#### Previewing

To preview the generated GRPK basemap locally, make sure you have
installed [Tileserver-GL-Light](https://www.npmjs.com/package/tileserver-gl-light) by
running `npm install -g tileserver-gl-light`.

Then execute the following command:

```shell
make tileserver
```

## Contributing 🤝

Calling all GIS enthusiasts! Your expertise is invaluable to us. Whether you spot issues or have groundbreaking ideas,
feel free to open an issue or submit a pull request. Dive into
our [contribution guidelines](https://github.com/AplinkosMinisterija/.github/blob/main/CONTRIBUTING.md) for more
insights.

## License 📄

This project is licensed under the [MIT License](./LICENSE), inviting you to explore, adapt, and contribute to our
mapping adventure!

Ready to map out Lithuania like never before? Join us on this exciting journey! 🌍✨