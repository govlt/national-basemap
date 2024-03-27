FROM ghcr.io/maplibre/martin:v0.13.0

WORKDIR /opt/grpk

COPY --chown=$USER:$USER docker/martin/config.yaml config.yaml
COPY --chown=$USER:$USER styles/geoportal/fonts styles/geoportal/fonts
COPY --chown=$USER:$USER styles/geoportal/sprites styles/geoportal/sprites
COPY --chown=$USER:$USER data/output/grpk.pmtiles pmtiles/grpk.pmtiles

CMD ["--config", "config.yaml"]