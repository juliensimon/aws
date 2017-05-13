from __future__ import print_function

from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient
from iot_config import *

def connectIot():
	myMQTTClient = AWSIoTMQTTClient(CLIENT_ID)
	myMQTTClient.configureEndpoint(IOT_ENDPOINT, IOT_PORT)
	myMQTTClient.configureCredentials(ROOT_CA, PRIVATE_KEY, CERTIFICATE) 
	myMQTTClient.connect()
	return myMQTTClient

def disconnectIot(myMQTTClient):
	myMQTTClient.disconnect()

print('Loading function')

def lambda_handler(event, context):
    print("Received event: " + json.dumps(event, indent=2))
    topic = event['topic']
    message = event['message']
    client = connectIot()
    client.publish(topic, message, 0)
    disconnectIot(client)


