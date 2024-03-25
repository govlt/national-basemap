grpk-basemap:
	./gradlew run

tileserver:
	tileserver-gl-light --no-cors --config grpk/tileserver-preview-config.json
