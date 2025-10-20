#!/bin/sh
BASE_PATH=$(echo "$HOST" | sed 's|^[^/]*//[^/]*||')
    
if [ -n "$BASE_PATH" ] && [ "$BASE_PATH" != "/" ]; then
    echo "Setting base_path to: $BASE_PATH"
    # Use | as delimiter since BASE_PATH contains /
    sed -i "s|base_path: /|base_path: ${BASE_PATH}|g" config.yaml
fi

echo "Setting host: $HOST"

for style_file in styles/*/style.json; do
    if [ -f "$style_file" ]; then
        sed -i "s#http://localhost:3000#${HOST}#g" "$style_file"
    fi
done

exec "$@"
