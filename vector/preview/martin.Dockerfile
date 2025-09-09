FROM ghcr.io/maplibre/martin:v0.18.1

ENV HOST="http://127.0.0.1:3000"

WORKDIR /opt/vector

COPY --chmod=755 docker/martin/docker-entrypoint.sh /usr/local/bin/

COPY docker/martin/config.yaml config.yaml

COPY styles/fonts  fonts/

COPY styles/openmaptiles styles/openmaptiles
COPY styles/positron styles/positron
COPY styles/bright styles/bright

COPY data/output/lithuania.pmtiles pmtiles/lithuania.pmtiles

ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["martin", "--config", "config.yaml"]