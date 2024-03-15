plugins {
    application
}

group = "lt.biip.basemap"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://repo.osgeo.org/repository/release/")
    }

    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

application {
    mainClass = "lt.biip.basemap.Basemap"
}

dependencies {
    implementation("com.onthegomap.planetiler:planetiler-core:0.7.0")
}


tasks.test {
    useJUnitPlatform()
}
