plugins {
    kotlin("js") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
}

group = "me.metag"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
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
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
            languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
            languageSettings.optIn("kotlinx.serialization.InternalSerializationApi")
            languageSettings.optIn("kotlin.RequiresOptIn")
        }


    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.5")
    implementation(npm("p5", "1.4.1"))
    implementation(npm("createloop", "0.0.12"))
    implementation(npm("open-simplex-noise", "2.5.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
}

rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}


