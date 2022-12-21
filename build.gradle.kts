plugins {
    kotlin("jvm") version "1.7.21"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation("com.fathzer:javaluator:3.0.3")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src/main")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}