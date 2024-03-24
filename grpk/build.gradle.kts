plugins {
    application
}

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

dependencies {
    implementation(libs.planetiler)
}

application {
    // Define the main class for the application.
    mainClass = "lt.biip.basemap.Basemap"
}
