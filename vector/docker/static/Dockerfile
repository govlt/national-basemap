FROM caddy:2.8

ENV HOST="http://127.0.0.1:80"

COPY --chmod=755 docker-entrypoint.sh /usr/local/bin/

# Copy Caddyfile
COPY ./caddy/Caddyfile /etc/caddy/Caddyfile

# Copy built files from the build stage
COPY ./tiles /srv/

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile", "--adapter", "caddyfile"]