download-grpk:
	mkdir -p data/sources
	wget -nv -T 900 -P data/sources https://www.geoportal.lt/download/opendata/GRPK/GRPK_Open_GDB.zip
	unzip data/sources/GRPK_Open_GDB.zip -d data/sources

prepare-layers:
	mkdir -p data/sources/layers

	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/ribos.gpkg "data/sources/GRPK.gdb" RIBOS
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/pastat.gpkg "data/sources/GRPK.gdb" PASTAT
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/keliai.gpkg "data/sources/GRPK.gdb" KELIAI
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/gelezink.gpkg "data/sources/GRPK.gdb" GELEZINK
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/vietov_t.gpkg "data/sources/GRPK.gdb" VIETOV_T
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/miskas_l.gpkg "data/sources/GRPK.gdb" MISKAS_L
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/hidro-l.gpkg "data/sources/GRPK.gdb" -nlt "MULTILINESTRING" HIDRO_L
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/plotai.gpkg "data/sources/GRPK.gdb" -nlt "MULTIPOLYGON" PLOTAI

tileserver:
	tileserver-gl-light data/grpk.pmtiles

generate-pmtiles:
	pmtiles convert data/biip-maps.mbtiles data/biip-maps.pmtiles


upload:
	rclone copy data/grpk.pmtiles  biip-tiles:tiles/poc/grpk

