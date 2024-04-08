FROM ghcr.io/maplibre/martin:v0.13.0

WORKDIR /opt/grpk

COPY docker/martin/config.yaml config.yaml

COPY styles/geoportal/fonts styles/openmaptiles/fonts styles/positron/fonts styles/bright/fonts  fonts/

COPY styles/geoportal/sprites styles/geoportal/sprites
COPY styles/openmaptiles/sprites styles/openmaptiles/sprites
COPY styles/positron/sprites styles/positron/sprites
COPY styles/bright/sprites styles/bright/sprites

COPY data/output/grpk.pmtiles pmtiles/grpk.pmtiles

CMD ["--config", "config.yaml"]