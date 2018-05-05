from slacker import Slacker
import websockets
import asyncio
import json
import os

async def main():
    token = os.environ['SLACK_TOKEN']
    slack = Slacker(token)

    response = slack.rtm.connect()
    print(str(response))
    sock_endpoint = response.body['url']
    # print(sock_endpoint)

    print(slack.im.list())

    async with websockets.connect(sock_endpoint) as websocket:
        message = await websocket.recv()
        print(str(message))

        message_dict = {'id': '1', 'type': 'message', 'channel': 'DAJ671XUG', 'text': 'Hello, World!'}
        await websocket.send(json.dumps(message_dict))

#########################################################

# start_server = websockets.serve(main, sock_endpoint, 8888)

asyncio.get_event_loop().run_until_complete(main())
# asyncio.get_event_loop().run_forever()