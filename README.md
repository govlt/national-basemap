# Vector Map of Lithuania 🗺️

Welcome to the exciting world of mapping Lithuania! Dive into the beauty and functionality of our free and open-source
map project inspired by [Proton Maps](https://protomaps.com/).

## What's Inside?

This project offers an early-stage experiment in creating a free and open source vector map for Lithuania.

## Getting Started 🚀

To embark on your mapping journey, follow these simple steps:

- **Install Java:** Ensure Java is installed on your system to power up the mapping engine.
- **Install GDAL:** Download and install GDAL, the Geospatial Data Abstraction Library,
  from [here](https://gdal.org/download.html) to unlock powerful geospatial data processing capabilities.
- **Install Tileserver-GL-Light:** Optionally, enhance your mapping experience by installing Tileserver-GL-Light. Simply
  run `npm install -g tileserver-gl-light`.

## Instructions 📝

1. **Download GRPK:** Acquire Lithuania's Georeferenced Basic Cadastre (GRPK)
   from [here](https://www.geoportal.lt/geoportal/web/georeferencinio-pagrindo-kadastras-grpk).
   Execute: `make download-grpk` to automate the download process.
2. **Convert to GeoPackages (GPKG):** Transform the downloaded data into GeoPackages using `make prepare-layers`.
3. **Generate Basemap:** Fire up the map-making magic with `./gradlew run` to generate the basemap in mbtiles format.
4. **Test it!** Ensure everything is running smoothly by executing `make tileserver`.

## Contributing 🤝

Calling all GIS enthusiasts! Your expertise is invaluable to us. Whether you spot issues or have groundbreaking ideas,
feel free to open an issue or submit a pull request. Dive into
our [contribution guidelines](https://github.com/AplinkosMinisterija/.github/blob/main/CONTRIBUTING.md) for more
insights.

## License 📄

This project is licensed under the [MIT License](./LICENSE), inviting you to explore, adapt, and contribute to our
mapping adventure!

Ready to map out Lithuania like never before? Join us on this exciting journey! 🌍✨