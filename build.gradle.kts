import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
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

dependencies {
    implementation(kotlin("reflect"))
}
//tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "1.8"
//}