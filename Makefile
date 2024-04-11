vector-basemap-generate:
	./gradlew run

vector-basemap-test:
	./gradlew test -i

vector-basemap-preview:
	docker compose --project-directory vector watch

