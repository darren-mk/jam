# https://pypi.org/project/websocket-client/

from websocket import create_connection

url = "wss://ws-postman.eu-gb.mybluemix.net/ws/echo"
ws = create_connection(url)
ws.send("Hello, you got this?")
result = ws.recv()
print(result)
ws.close()
