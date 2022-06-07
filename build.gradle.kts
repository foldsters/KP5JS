plugins {
    kotlin("js") version "1.6.0"
}

group = "me.metag"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.5")
    testImplementation(kotlin("test"))
    implementation(npm("p5", "1.4.1"))
    implementation(npm("createloop", "0.0.12"))
    implementation(npm("open-simplex-noise", "2.5.0"))
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
}
