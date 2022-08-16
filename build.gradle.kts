plugins {
    kotlin("js") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev755"
}

group = "me.metag"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("kotlin.experimental.ExperimentalTypeInference")
                optIn("kotlinx.serialization.InternalSerializationApi")
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.5")
    implementation(npm("p5", "1.4.1"))
    implementation(npm("createloop", "0.0.12"))
    implementation(npm("open-simplex-noise", "2.5.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation(compose.web.core)
    implementation(compose.runtime)
}

rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}


