plugins {
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
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
    // `run` forks its own JVM, so org.gradle.jvmargs does not apply here. Without this the
    // build gets the JVM default of 25% of RAM (4 GB on a 16 GB GitHub runner). The rest of
    // the 16 GB is left to the OS page cache, which Planetiler relies on for its mmap-ed
    // temp feature files.
    applicationDefaultJvmArgs = listOf("-Xmx8g")
}

tasks.register<JavaExec>("syncAddressRegistry") {
    group = "syncAddressRegistry"
    mainClass.set("lt.lrv.basemap.preparations.AddressRegistryHouseNumbers")
    classpath = sourceSets["main"].runtimeClasspath
}