import { useEffect, useState } from 'react'
import { Client } from '@stomp/stompjs'
import Login from './components/Login'

function App() {
  const [connected, setConnected] = useState(false)
  const [client, setClient] = useState<null | Client>(null)
  const [messageInput, setMessageInput] = useState<string>('')

  useEffect(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8080/stomp',
      connectHeaders: {
        login: 'user',
        passcode: 'password',
      },
      debug: function (str) {
        console.log(str)
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })

    client.onConnect = function (frame) {
      // Do something, all subscribes must be done is this callback
      // This is needed because this will be executed after a (re)connect
      setConnected(true)
    }

    client.onDisconnect = function () {
      console.log('Client disconnected.')
      setConnected(false)
    }

    client.onWebSocketClose = function (evt) {
      console.log('WebSocket closed.')
      console.log(evt)
    }

    client.onStompError = function (frame) {
      // Will be invoked in case of error encountered at Broker
      // Bad login/passcode typically will cause an error
      // Complaint brokers will set `message` header with a brief message. Body may contain details.
      // Compliant brokers will terminate the connection after any error
      console.log('Broker reported error: ' + frame.headers['message'])
      console.log('Additional details: ' + frame.body)
    }

    setClient(client)
  }, [])

  function onConnect() {
    client?.activate()
  }

  function onDisconnect() {
    client?.deactivate()
  }

  function onSend() {
    if (messageInput) {
      client?.publish({
        destination: '/app/class403',
        body: messageInput,
      })
      setMessageInput('')
    }
  }

  return (
    <>
      <h1> websocket demo</h1>
      <Login />

      <hr />

      <div>
        {connected ? (
          <div>
            <input
              onChange={(event) => {
                setMessageInput(event.target.value)
              }}
              value={messageInput}
            />
            <>
              <button onClick={onSend} disabled={!messageInput}>
                send
              </button>
              <button onClick={onDisconnect}>disconnect</button>
            </>
          </div>
        ) : (
          <button onClick={() => onConnect()}>Connect to Chat server</button>
        )}
      </div>
    </>
  )
}

export default App
