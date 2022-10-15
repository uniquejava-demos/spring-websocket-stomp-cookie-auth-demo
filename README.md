## Environment

- Java 17
- Spring Boot 2.7.4
- React 18.2.0

## Checkpoint

- [x] Check ws://localhost:8080/stomp endpoint is protected by spring security
- [ ] Check cookie exist in http request header when doing websocket handshake
- [ ] Check Principal is available in controller layer
- [ ] Check SecurityContextHolder is available in service layer

## #1 WS endpoint protected by default ✅

不登录，尝试直接链接websocket， 报错:
![](./doc/images/ws-protected-by-default.png)

注意你无法知道ws error的具体描述信息，
见[Websocket onerror - how to read error description?](https://stackoverflow.com/questions/18803971/websocket-onerror-how-to-read-error-description)

后台错误日志:
![](./doc/images/ws-protected-backend-log.png)

代码:

```tsx
function App() {
    const [connected, setConnected] = useState(false)

    let ws: WebSocket

    function onConnect() {
        ws = new WebSocket('ws://localhost:8080/stomp')

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
    }

    return <></>
}
```
