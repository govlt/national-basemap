#!/bin/bash

set -euo pipefail

CURL_OPTS=(
    -f -L
    --max-redirs 5
    --retry 5
    --retry-delay 5
    --retry-all-errors
    --connect-timeout 20
    -A "NationalBasemap/1.0 (+https://github.com/govlt/national-basemap)"
)

# Function to calculate MD5 checksum
calculate_md5() {
    local file="$1"
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        md5 -r "$file"
    else
        # Linux and other Unix-like systems
        md5sum "$file"
    fi
}

download_data_source_and_md5() {
  local filename="$1"
  local url="$2"

  if ! curl "${CURL_OPTS[@]}" -o "data-sources/$filename" "$url"; then
    echo "Download failed."
    return 1
  fi

  # Calculate the MD5 checksum of the downloaded file
  calculate_md5 data-sources/"$filename" >> data-sources/data-source-checksums.txt
}

echo "Starting data processing"

rm -rf houses-espg-4326.gpkg.zip data-sources

mkdir -p data-sources

echo "Importing addresses data into GeoPackage"

download_data_source_and_md5 adr_stat_lr.csv "https://www.registrucentras.lt/aduomenys/?byla=adr_stat_lr.csv"
ogr2ogr -f GPKG "data-sources/addresses.gpkg" "data-sources/adr_stat_lr.csv" -nln info

echo "Importing address points for each municipality"

download_data_source_and_md5 adr_savivaldybes.csv "https://www.registrucentras.lt/aduomenys/?byla=adr_savivaldybes.csv"

csvcut "data-sources/adr_savivaldybes.csv" -d "|" -c "SAV_KODAS" | tail -n +2 | while read -r code; do
  echo "Converting https://www.registrucentras.lt/aduomenys/?byla=adr_gra_$code.json"

  download_data_source_and_md5 "addresses-$code.json" "https://www.registrucentras.lt/aduomenys/?byla=adr_gra_$code.json"

  ogr2ogr -append -f GPKG "data-sources/addresses.gpkg" "data-sources/addresses-$code.json" -nln points
done

echo "Creating houses GeoPackage"

ogr2ogr -f GPKG -t_srs EPSG:4326 -xyRes "0.0000001" -sql "SELECT points.fid, points.geom, points.AOB_KODAS, info.sav_kodas, points.gyv_kodas, points.gat_kodas, info.nr, info.pasto_kodas, info.korpuso_nr FROM points INNER JOIN info USING (AOB_KODAS) ORDER BY AOB_KODAS" houses-espg-4326.gpkg.zip "data-sources/addresses.gpkg" -nln houses

echo "GeoPackage database created successfully"
