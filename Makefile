grpk-generate-basemap:
	./gradlew run

test:
	./gradlew test -i

grpk-preview:
	docker compose --project-directory grpk watch

