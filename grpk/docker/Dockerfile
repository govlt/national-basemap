FROM gradle:8.7-jdk21 as build

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY grpk ./grpk

RUN ./gradlew run

FROM ghcr.io/maplibre/martin:v0.13.0

# Let's use non root user
ARG USER=biip

RUN apk add --update sudo

RUN adduser -D $USER \
        && echo "$USER ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/$USER \
        && chmod 0440 /etc/sudoers.d/$USER

USER $USER

WORKDIR /opt/grpk

# Martin configuration
COPY --chown=$USER:$USER grpk/docker/martin/config.yaml config.yaml

# Fonts
COPY --chown=$USER:$USER grpk/styles/geoportal/fonts grpk/styles/openmaptiles/fonts grpk/styles/positron/fonts grpk/styles/bright/fonts  fonts/

# Sprites
COPY --chown=$USER:$USER grpk/styles/geoportal/sprites styles/geoportal/sprites
COPY --chown=$USER:$USER grpk/styles/openmaptiles/sprites styles/openmaptiles/sprites
COPY --chown=$USER:$USER grpk/styles/positron/sprites styles/positron/sprites
COPY --chown=$USER:$USER grpk/styles/bright/sprites styles/bright/sprites

# Pmtiles
COPY --chown=$USER:$USER --from=build /app/grpk/data/output/grpk.pmtiles pmtiles/grpk.pmtiles

CMD ["--config", "config.yaml"]