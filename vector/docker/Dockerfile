FROM gradle:8.7-jdk21 AS build

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY vector ./vector

RUN ./gradlew run

FROM ghcr.io/maplibre/martin:v0.18.1

# Let's use non root user
ARG USER=basemap

RUN apk add --update sudo

RUN adduser -D $USER \
        && echo "$USER ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/$USER \
        && chmod 0440 /etc/sudoers.d/$USER

USER $USER

WORKDIR /opt/vector

ENV HOST="http://127.0.0.1:3000"

# Entrypoint script
COPY --chown=$USER:$USER --chmod=755 vector/docker/martin/docker-entrypoint.sh /usr/local/bin/

# Martin configuration
COPY --chown=$USER:$USER vector/docker/martin/config.yaml config.yaml

# Fonts
COPY --chown=$USER:$USER vector/styles/fonts  fonts/

# Styles & sprites
COPY --chown=$USER:$USER vector/styles/openmaptiles styles/openmaptiles
COPY --chown=$USER:$USER vector/styles/positron styles/positron
COPY --chown=$USER:$USER vector/styles/bright styles/bright

# Pmtiles
COPY --chown=$USER:$USER --from=build /app/vector/data/output/lithuania.pmtiles pmtiles/lithuania.pmtiles

ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["martin", "--config", "config.yaml"]