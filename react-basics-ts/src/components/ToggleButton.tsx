import { useState } from 'react'

function ToggleButton() {
  const [isToggled, setIsToggled] = useState(false)
  const f = () => setIsToggled(!isToggled)
  return (
    <button onClick={f}>
      {isToggled ? 'ON' : 'OFF'}
    </button>
  )
}

export default ToggleButton