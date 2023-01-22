# reference:
# https://websockets.readthedocs.io/en/stable/

import asyncio
import websockets

async def echo(websocket):
    async for message in websocket:
        await websocket.send(message)

async def main():
    async with websockets.serve(echo, "localhost", 8765):
        await asyncio.Future()

asyncio.run(main())

# ws://localhost:8765
