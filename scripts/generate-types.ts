#!/usr/bin/env node
import { readFileSync, writeFileSync, existsSync } from 'fs'
import { resolve } from 'path'

const rootDir = process.cwd()
const kotlinDtsPath = resolve(rootDir, '.kotlin-build/build/js/packages/kp5js/kotlin/kp5js.d.ts')
const outputPath = resolve(rootDir, 'src/kotlin.d.ts')

if (!existsSync(kotlinDtsPath)) {
  console.warn('⚠ Kotlin .d.ts file not found. Run build first.')
  process.exit(0)
}

const kotlinDts = readFileSync(kotlinDtsPath, 'utf-8')

// Wrap the Kotlin types in a module declaration
const wrappedTypes = `// TypeScript definitions for Kotlin/JS compiled output
// Auto-generated from .kotlin-build/build/js/packages/kp5js/kotlin/kp5js.d.ts
// DO NOT EDIT - regenerate with: npm run generate:types

declare module '@kotlin/kp5js.mjs' {
${kotlinDts}
}
`

writeFileSync(outputPath, wrappedTypes)
console.log('✓ Generated TypeScript definitions in src/kotlin.d.ts')
