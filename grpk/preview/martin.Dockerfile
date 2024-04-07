FROM ghcr.io/maplibre/martin:v0.13.0

WORKDIR /opt/grpk

COPY docker/martin/config.yaml config.yaml

COPY styles/geoportal/fonts styles/open-map-tiles/fonts styles/positron/fonts  fonts/

COPY styles/geoportal/sprites styles/geoportal/sprites
COPY styles/open-map-tiles/sprites styles/open-map-tiles/sprites
COPY styles/positron/sprites styles/positron/sprites

COPY data/output/grpk.pmtiles pmtiles/grpk.pmtiles

CMD ["--config", "config.yaml"]