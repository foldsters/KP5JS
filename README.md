# Kotlin/JS + React + Vite Template

An Expo-style template for building creative coding projects with Kotlin/JS and React. Configure everything in `kotlin.config.ts` - Gradle files are auto-generated!

## What This Is

- **Kotlin/JS module** - Write p5.js sketches in Kotlin with type safety
- **React frontend** - Build your UI with React/TypeScript
- **Config-driven** - All Kotlin build settings in one `kotlin.config.ts` file
- **Hot reload** - Changes to Kotlin or React code reload automatically
- **Zero Gradle knowledge needed** - Gradle files are generated automatically

## How It Works

1. **Edit `kotlin.config.ts`** - Define your Kotlin dependencies and settings
2. **Prebuild generates Gradle files** - Runs automatically before build/dev
3. **Gradle compiles Kotlin → ES modules** - Kotlin/JS code compiled to `.mjs` files
4. **Vite serves everything** - Dev server with hot-reload for both Kotlin and React
5. **React imports Kotlin** - Use `@kotlin` alias to import compiled sketches

## Setup

```bash
# Install dependencies
npm install

# Run dev server (auto-generates Gradle files, then runs Kotlin + Vite)
npm run dev

# Build for production
npm run build
```

## Project Structure

```
src/
  react/              # React app source
    App.tsx           # Main React component
    main.tsx          # Entry point
  kotlin/             # Kotlin source files (sketches)
    jsMain/           # Kotlin/JS main source
assets/               # Static files (images, fonts, etc.)
scripts/
  prebuild.ts         # Generates Gradle files from config
  kotlin-template/    # Gradle wrapper template files
kotlin.config.ts      # YOUR config - edit this!
index.html            # Entry HTML
vite.config.ts        # Vite config

.kotlin-build/        # Auto-generated (entire folder - don't edit!)
  src -> ../src/kotlin  # Symlink to your Kotlin source
  build.gradle.kts    # Generated from kotlin.config.ts
  settings.gradle.kts # Generated from kotlin.config.ts
  gradlew/gradlew.bat # Gradle wrapper scripts
  gradle/             # Gradle wrapper jar
```

## Using Kotlin Sketches in React

Import and use any exported Kotlin sketch:

```tsx
// @ts-ignore - Kotlin compiled output
import { Penrose, Hopper } from '@kotlin/kp5js.mjs'

function App() {
  useEffect(() => {
    Penrose() // Run the sketch
  }, [])

  return <div>Your UI here</div>
}
```

## Configuration

### kotlin.config.ts

This is the only file you need to edit for Kotlin build settings:

```typescript
import { defineConfig } from './scripts/kotlin-config'

export default defineConfig({
  moduleName: 'kp5js',
  kotlinVersion: '2.1.0',

  // NPM packages your Kotlin code needs
  npmDependencies: {
    'p5': '1.4.1',
    'createloop': '0.0.12',
  },

  // Kotlin dependencies
  kotlinDependencies: [
    'org.jetbrains.kotlinx:kotlinx-html:0.7.5',
  ],

  // Compiler options
  compiler: {
    target: 'es2015',
    generateTypeScript: true,
  },
})
```

The `prebuild:kotlin` script automatically generates `kotlin/build.gradle.kts` and `kotlin/settings.gradle.kts` from this config.

## Build Artifacts

**Auto-generated** (don't edit, ignored in git):
- `.kotlin-build/` - Entire directory is generated! (includes Gradle files, wrapper, build output)
- `node_modules/` - npm packages (~100MB)
- `dist/` - Vite production build
- `.gradle/` - Gradle cache
- `.kotlin/` - Kotlin compiler cache
- `kotlin-js-store/` - Kotlin/JS npm cache

**Source files** (commit these):
- `src/kotlin/jsMain/` - Your Kotlin code
- `src/react/` - Your React code
- `assets/` - Static files
- `kotlin.config.ts` - Kotlin build config
- `package.json`, `vite.config.ts` - Frontend config
- `scripts/` - Build scripts

**You can safely delete `.kotlin-build/` and regenerate it with `npm run prebuild:kotlin`**
