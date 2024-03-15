download-grpk:
	mkdir -p data/input
	wget -nv -T 900 -P data/input https://www.geoportal.lt/download/opendata/GRPK/GRPK_Open_GDB.zip
	unzip data/input/GRPK_Open_GDB.zip -d data/input

prepare-layers:
	mkdir -p data/input/layers

	ogr2ogr -t_srs EPSG:4326 -f GPKG data/input/layers/ribos.gpkg "data/input/GRPK_Open_GDB/GRPK.gdb" RIBOS
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/input/layers/pastat.gpkg "data/input/GRPK_Open_GDB/GRPK.gdb" PASTAT
	ogr2ogr -t_srs EPSG:4326 -f GPKG data/input/layers/keliai.gpkg "data/input/GRPK_Open_GDB/GRPK.gdb" KELIAI

tileserver:
	tileserver-gl-light data/biip-maps.mbtiles