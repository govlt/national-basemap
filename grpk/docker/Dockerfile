FROM ghcr.io/maplibre/martin:v0.13.0

# Let's user non root user
ARG USER=biip

RUN apk add --update sudo

RUN adduser -D $USER \
        && echo "$USER ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/$USER \
        && chmod 0440 /etc/sudoers.d/$USER

USER $USER

WORKDIR /opt/grpk

COPY --chown=$USER:$USER docker/martin/config.yaml config.yaml
COPY --chown=$USER:$USER styles/geoportal/fonts styles/geoportal/fonts
COPY --chown=$USER:$USER styles/geoportal/sprites styles/geoportal/sprites
COPY --chown=$USER:$USER data/output/grpk.pmtiles pmtiles/grpk.pmtiles

CMD ["--config", "config.yaml"]