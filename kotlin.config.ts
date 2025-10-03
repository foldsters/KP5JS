import { defineConfig } from './scripts/kotlin-config'

export default defineConfig({
  // Module name for the compiled output
  moduleName: 'kp5js',

  // Kotlin version
  kotlinVersion: '2.1.0',

  // NPM dependencies that Kotlin needs
  npmDependencies: {
    'p5': '1.4.1',
    'createloop': '0.0.12',
    'open-simplex-noise': '2.5.0',
  },

  // Kotlin dependencies
  kotlinDependencies: [
    'org.jetbrains.kotlinx:kotlinx-html:0.7.5',
    'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2',
  ],

  // Compiler options
  compiler: {
    target: 'es2015',
    generateTypeScript: true,
  },

  // Optional: Kotlin language features to opt-in
  optIns: [
    'kotlinx.serialization.ExperimentalSerializationApi',
    'kotlin.experimental.ExperimentalTypeInference',
    'kotlinx.serialization.InternalSerializationApi',
    'kotlin.RequiresOptIn',
  ],
})
