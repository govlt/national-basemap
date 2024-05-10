FROM gradle:8.7-jdk21 as build

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY vector ./vector

RUN ./gradlew run

FROM ghcr.io/maplibre/martin:v0.13.0

# Let's use non root user
ARG USER=basemap

RUN apk add --update sudo

RUN adduser -D $USER \
        && echo "$USER ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/$USER \
        && chmod 0440 /etc/sudoers.d/$USER

USER $USER

WORKDIR /opt/vector

# Martin configuration
COPY --chown=$USER:$USER vector/docker/martin/config.yaml config.yaml

# Fonts
COPY --chown=$USER:$USER vector/styles/fonts  fonts/

# Sprites
COPY --chown=$USER:$USER vector/styles/openmaptiles/sprites styles/openmaptiles/sprites
COPY --chown=$USER:$USER vector/styles/positron/sprites styles/positron/sprites
COPY --chown=$USER:$USER vector/styles/bright/sprites styles/bright/sprites

# Pmtiles
COPY --chown=$USER:$USER --from=build /app/vector/data/output/lithuania.pmtiles pmtiles/lithuania.pmtiles

CMD ["--config", "config.yaml"]