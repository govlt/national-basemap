FROM caddy:2.7

EXPOSE 80

# Copy Caddyfile
COPY ./caddy/Caddyfile /etc/caddy/Caddyfile

# Copy built files from the build stage
COPY ./tiles ./tile.json /srv/