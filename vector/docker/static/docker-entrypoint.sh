#!/bin/sh

sed -i "s#http://localhost:80#${HOST}#g" tile.json

exec "$@"
