import { defineConfig } from 'vite'
import { resolve } from 'path'
import { watch as fsWatch } from 'fs'
import react from '@vitejs/plugin-react'

export default defineConfig({
  root: '.',
  publicDir: 'assets',

  plugins: [
    react(),
    {
      name: 'kotlin-hmr',
      configureServer(server) {
        const kotlinOut = resolve(__dirname, '.kotlin-build/build/js/packages/kp5js/kotlin/kp5js.mjs')

        // Watch main Kotlin output file for changes and trigger reload
        const watcher = fsWatch(kotlinOut, () => {
          server.ws.send({ type: 'full-reload' })
        })

        server.httpServer?.once('close', () => {
          watcher.close()
        })
      }
    }
  ],

  server: {
    port: 3000,
    fs: {
      allow: ['.'],
    },
  },

  resolve: {
    alias: {
      '@kotlin': resolve(__dirname, '.kotlin-build/build/js/packages/kp5js/kotlin'),
    },
  },

  build: {
    outDir: 'dist',
  },
})
