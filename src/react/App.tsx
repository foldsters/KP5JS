import { useEffect, useRef } from 'react'
// @ts-ignore - Kotlin compiled output
import { Penrose } from '@kotlin/kp5js.mjs'

function App() {
  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      background: '#1a1a1a',
      color: 'white',
      padding: '2rem'
    }}>
      <h1>Kotlin/JS + React Template</h1>
      <p>Edit sketches in <code>src/jsMain/kotlin/</code> - they'll hot reload!</p>

      <KotlinSketch sketch={Penrose} />
    </div>
  )
}

// Component to mount a Kotlin p5.js sketch
function KotlinSketch({ sketch }: { sketch: () => void }) {
  const mountedRef = useRef(false)

  useEffect(() => {
    if (!mountedRef.current) {
      sketch()
      mountedRef.current = true
    }
  }, [sketch])

  return null
}

export default App
