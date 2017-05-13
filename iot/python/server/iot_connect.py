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
