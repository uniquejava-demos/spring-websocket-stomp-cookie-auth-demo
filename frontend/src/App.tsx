import { useState } from 'react'
import Login from './components/Login'

function App() {
  const [connected, setConnected] = useState(false)
  const [ws, setWs] = useState<null | WebSocket>(null)

  function onConnect() {
    const ws = new WebSocket('ws://localhost:8080/stomp')

    ws.onopen = (event) => {
      console.log('on open ..')
      ws.send('Hello Server!')
      setConnected(true)
    }

    ws.onmessage = (event) => {
      console.log('on message: ', event.data)
    }

    ws.onerror = (event) => {
      console.log('onerror: ', event)
    }

    ws.onclose = (event) => {
      console.log('onclose: ', event.code, event.reason)
      setConnected(false)
    }

    setWs(ws)
  }

  return (
    <>
      <h1> websocket demo</h1>
      <Login />

      <hr />

      <div>
        {connected ? null : <button onClick={() => onConnect()}>Connect to Chat server</button>}
      </div>
    </>
  )
}

export default App
