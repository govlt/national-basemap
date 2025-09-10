#!/bin/sh

for style_file in styles/*/style.json; do
    if [ -f "$style_file" ]; then
        sed -i "s#http://localhost:3000#${HOST}#g" "$style_file"
    fi
done

exec "$@"
