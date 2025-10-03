import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'

// Import p5 - it will be available globally
import p5 from 'p5'

// Import createloop - it sets window.createLoop itself
import 'createloop'

// TypeScript declarations
declare global {
  interface Window {
    p5: typeof p5
    createLoop: any
  }
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
