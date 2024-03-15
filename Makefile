download-grpk:
	mkdir -p data/sources
	wget -nv -T 900 -P data/sources https://www.geoportal.lt/download/opendata/GRPK/GRPK_Open_GDB.zip
	unzip data/sources/GRPK_Open_GDB.zip -d data/sources

prepare-layers:
	mkdir -p data/sources/layers

	#ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/ribos.gpkg "data/sources/GRPK_Open_GDB/GRPK.gdb" RIBOS
	#ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/pastat.gpkg "data/sources/GRPK_Open_GDB/GRPK.gdb" PASTAT
	#ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/keliai.gpkg "data/sources/GRPK_Open_GDB/GRPK.gdb" KELIAI
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/hidro-l.gpkg "data/sources/GRPK.gdb" -nlt "MULTILINESTRING" HIDRO_L
	# ogr2ogr -t_srs EPSG:4326 -f GPKG data/sources/layers/hidro-hd.gpkg "data/sources/GRPK.gdb" -nlt "MULTIPOLYGON" -where "GKODAS LIKE '%hd%'" PLOTAI

tileserver:
	tileserver-gl-light data/biip-maps.mbtiles

generate-pmtiles:
	pmtiles convert data/biip-maps.mbtiles data/biip-maps.pmtiles

