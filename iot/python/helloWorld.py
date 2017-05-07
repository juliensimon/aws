from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient
from iot_credentials import *
import time

def callback(client, userdata, message):
	print "Topic="+message.topic
	print "Message="+message.payload

def connect():
	myMQTTClient = AWSIoTMQTTClient(CLIENT_ID)
	myMQTTClient.configureEndpoint(IOT_ENDPOINT, IOT_PORT)
	myMQTTClient.configureCredentials(ROOT_CA, PRIVATE_KEY, CERTIFICATE) 
	myMQTTClient.connect()
	return myMQTTClient

def disconnect(myMQTTClient):
	myMQTTClient.disconnect()

topic = "JohnnyPi"
message = "Hello world"

client = connect()
client.subscribe(topic, 1, callback)

for i in range(0,10):
	client.publish(topic, message, 0)
	time.sleep(1)

client.unsubscribe(topic)
disconnect(client)

