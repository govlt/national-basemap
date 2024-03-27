FROM gradle:8.7.0-jdk21 as build

WORKDIR /app

COPY ../../gradle gradlew gradlew.bat settings.gradle.kts gradle.properties grpk ./

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

COPY --chown=$USER:$USER grpk/docker/martin/config.yaml config.yaml
COPY --chown=$USER:$USER grpk/styles/geoportal/fonts styles/geoportal/fonts
COPY --chown=$USER:$USER grpk/styles/geoportal/sprites styles/geoportal/sprites
COPY --chown=$USER:$USER --from=build /app/grpk/data/output/grpk.pmtiles pmtiles/grpk.pmtiles

CMD ["--config", "config.yaml"]