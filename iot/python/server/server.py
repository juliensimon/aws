import time

from gopigo import *

import iot_connect
from iot_topics import *
import PollyApi
import camera 

def scan():
	dist=(str)(us_dist(15))
	message = "The object is "+dist+" centimeters away"
	return message

def callbackMove(client, userdata, message):
	print "Topic="+message.topic
	print "Message="+message.payload
	cmd = message.payload
	if cmd=="forward":
		fwd()	# Move forward
	elif cmd=="left":
		left()	# Turn left
	elif cmd=="right":
		right()	# Turn Right
	elif cmd=="backward":
		bwd()	# Move back
	elif cmd=="stop":
		stop()	# Stop
	elif cmd=="faster":
		increase_speed()	# Increase speed
	elif cmd=="slower":
		decrease_speed()	# Decrease speed
	else:
		print "Wrong Command, Please Enter Again"
	time.sleep(1)
	stop()

def callbackSpeak(client, userdata, message):
	print "Topic="+message.topic
	print "Message="+message.payload
	if not message.payload:
		msg = "Nothing to say, sorry"
	else:
		msg = message.payload
	global polly
	PollyApi.speak(polly, msg)

def callbackSee(client, userdata, message):
	print "Topic="+message.topic
	print "Message="+message.payload
	cmd = message.payload
        if cmd=="see":
		polly,cam=camera.init()
		message=camera.see(polly,cam)
		distMsg = scan()
		message  = message+distMsg	
		PollyApi.speak(polly,message)
        elif cmd=="tweet":
		polly,cam=camera.init()
		message=camera.see(polly,cam)
		distMsg = scan()
		message  = message+distMsg	
		PollyApi.speak(polly,message)
		camera.tweet(message)	
	else:
		print "Wrong Command, Please Enter Again"

def callbackScan(client, userdata, message):
	print "Topic="+message.topic
	print "Message="+message.payload
	cmd = message.payload
	if cmd=="scan":
		message = scan()
		global polly
		PollyApi.speak(polly,message)
        elif cmd=="left":
		global angle
		angle = angle + 30
		if (angle > 180):
			angle = 180
                servo(angle)
        elif cmd=="right":
		global angle
		angle = angle - 30
		if (angle < 0):
			angle = 0
                servo(angle)
        elif cmd=="reset":
                servo(90)
        else:
                print "Wrong Command, Please Enter Again"

# Reset servo to center position
enable_servo()
angle = 90
servo(angle)

# Connect to Polly
polly = PollyApi.connectToPolly()

# Connect to IoT Gateway and subscribe to topics
client = iot_connect.connectIot()
client.subscribe(topicMove, 1, callbackMove)
client.subscribe(topicSpeak, 1, callbackSpeak)
client.subscribe(topicSee, 1, callbackSee)
client.subscribe(topicScan, 1, callbackScan)

while True:
	time.sleep(10)

client.unsubscribe(topicMove)
client.unsubscribe(topicSpeak)
client.unsubscribe(topicSee)
iot_connect.disconnectIot(client)
disable_servo()
