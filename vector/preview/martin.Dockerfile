FROM ghcr.io/maplibre/martin:v0.13.0

WORKDIR /opt/vector

COPY docker/martin/config.yaml config.yaml

COPY styles/fonts  fonts/

COPY styles/openmaptiles/sprites styles/openmaptiles/sprites
COPY styles/positron/sprites styles/positron/sprites
COPY styles/bright/sprites styles/bright/sprites

COPY data/output/lithuania.pmtiles pmtiles/lithuania.pmtiles

CMD ["--config", "config.yaml"]