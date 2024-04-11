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
    implementation(libs.geotoolsProcessGeometry)

    testImplementation(libs.junitJupyter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}


application {
    mainClass = "lt.lrv.basemap.Basemap"
}
