#!/usr/bin/env node
import { writeFileSync, mkdirSync, existsSync, cpSync } from 'fs'
import { resolve } from 'path'

// Load the kotlin config
async function loadConfig() {
  const configPath = resolve(process.cwd(), 'kotlin.config.ts')

  // Use dynamic import to load TypeScript config
  const { default: config } = await import(configPath)
  return config
}

// Generate build.gradle.kts
function generateBuildGradle(config: any) {
  const npmDeps = config.npmDependencies || {}
  const kotlinDeps = config.kotlinDependencies || []
  const optIns = config.optIns || []
  const target = config.compiler?.target || 'es2015'
  const genTypes = config.compiler?.generateTypeScript ?? true

  const npmDepsStr = Object.entries(npmDeps)
    .map(([pkg, ver]) => `        implementation(npm("${pkg}", "${ver}"))`)
    .join('\n')

  const kotlinDepsStr = kotlinDeps
    .map((dep: any) => `        implementation("${dep}")`)
    .join('\n')

  const optInsStr = optIns
    .map((opt: any) => `        optIn("${opt}")`)
    .join('\n')

  return `import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
  kotlin("multiplatform") version "${config.kotlinVersion}"
  kotlin("plugin.serialization") version "${config.kotlinVersion}"
}

group = "com.kotlin.template"
version = "1.0.0"

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
  js(IR) {
    moduleName = "${config.moduleName}"
    useEsModules()
    binaries.executable()
    ${genTypes ? 'generateTypeScriptDefinitions()' : ''}
    browser {
      webpackTask { enabled = false }
      runTask { enabled = false }
      testTask { enabled = false }
    }
  }

  sourceSets {
    all {
      languageSettings {
${optInsStr}
      }
    }

    val jsMain by getting {
      kotlin.srcDir("src")
      dependencies {
        implementation(kotlin("script-runtime"))
${kotlinDepsStr}
${npmDepsStr}
      }
    }
  }
}

tasks.withType<KotlinJsCompile>().configureEach {
  kotlinOptions {
    target = "${target}"
  }
}
`
}

// Generate settings.gradle.kts
function generateSettingsGradle(config: any) {
  return `rootProject.name = "${config.moduleName}"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
`
}

// Generate gradle.properties
function generateGradleProperties() {
  return `kotlin.code.style=official
kotlin.js.generate.executable.default=true
kotlin.incremental.js.ir=true
`
}

async function main() {
  const config = await loadConfig()
  const rootDir = process.cwd()
  const kotlinDir = resolve(rootDir, '.kotlin-build')
  const srcKotlin = resolve(rootDir, 'src/kotlin')

  // Create kotlin directory if it doesn't exist
  if (!existsSync(kotlinDir)) {
    mkdirSync(kotlinDir, { recursive: true })
  }

  // Generate files
  const buildGradle = generateBuildGradle(config)
  const settingsGradle = generateSettingsGradle(config)
  const gradleProperties = generateGradleProperties()

  writeFileSync(resolve(kotlinDir, 'build.gradle.kts'), buildGradle)
  writeFileSync(resolve(kotlinDir, 'settings.gradle.kts'), settingsGradle)
  writeFileSync(resolve(kotlinDir, 'gradle.properties'), gradleProperties)

  // Create symlink to src-kotlin
  const srcLink = resolve(kotlinDir, 'src')
  if (existsSync(srcLink)) {
    const { rmSync } = await import('fs')
    rmSync(srcLink, { recursive: true, force: true })
  }

  const { symlinkSync } = await import('fs')
  const relativeSrcPath = '../src/kotlin'
  symlinkSync(relativeSrcPath, srcLink, 'junction')

  // Copy Gradle wrapper files if they exist in template
  const wrapperTemplate = resolve(rootDir, 'scripts/kotlin-template')
  if (existsSync(wrapperTemplate)) {
    const gradlewFiles = ['gradlew', 'gradlew.bat']
    for (const file of gradlewFiles) {
      const src = resolve(wrapperTemplate, file)
      if (existsSync(src)) {
        cpSync(src, resolve(kotlinDir, file))
      }
    }

    const wrapperDir = resolve(wrapperTemplate, 'gradle/wrapper')
    if (existsSync(wrapperDir)) {
      cpSync(wrapperDir, resolve(kotlinDir, 'gradle/wrapper'), { recursive: true })
    }
  }

  console.log('✓ Generated Kotlin build files in .kotlin-build/')
}

main().catch(err => {
  console.error('Failed to generate Kotlin build files:', err)
  process.exit(1)
})
