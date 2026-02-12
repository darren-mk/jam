import { useState } from 'react'
import './App.css'
import ToggleButton from './components/ToggleButton'

interface PanelProps {
  nickname: string;
}

function Panel({ nickname }: PanelProps) {
  return <h1>Yo! {nickname}</h1>;
}

function App() {
  const [count, setCount] = useState(0)
  return (
    <>
      <h1>Vite + React</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <Panel nickname="Darren" />
        <p>
          Edit <code>src/App.tsx</code> and save to test HMR
        </p>
        <ToggleButton />
        <ToggleButton />
        <ToggleButton />
      </div>
      <p className="read-the-docs">
        Click on the Vite and React logos to learn more
      </p>
    </>
  )
}

export default App
