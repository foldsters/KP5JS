import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
  kotlin("multiplatform") version "2.1.0"
  kotlin("plugin.serialization") version "2.1.0"
}

group = "me.metag"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
  google()
}

kotlin {
  js(IR) {
    useCommonJs()
    useEsModules()
    binaries.executable()
    generateTypeScriptDefinitions()
    browser {
      commonWebpackConfig {
        cssSupport {
          enabled.set(true)
        }
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

    val jsMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.5")
        implementation(npm("p5", "1.4.1"))
        implementation(npm("createloop", "0.0.12"))
        implementation(npm("open-simplex-noise", "2.5.0"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
        implementation(kotlin("script-runtime"))
      }
    }
  }

}

rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
  versions.webpackCli.version = "4.10.0"
}

tasks.withType<KotlinJsCompile>().configureEach {
  kotlinOptions {
    target = "es2015"
  }
}


