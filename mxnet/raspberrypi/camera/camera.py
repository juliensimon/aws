import picamera
import inception_predict
import PollyApi
import tweepy

from twitter_credentials import *

filename = '/home/pi/cap.jpg'

def init():
	polly = PollyApi.connectToPolly()
	camera = picamera.PiCamera()
	camera.resolution=(640,480)
	return polly,camera

def see(polly,camera):
	camera.capture(filename)
	camera.close()
	topn = inception_predict.predict_from_local_file(filename, N=5)
	print topn
	top1 = topn[0]
	# Convert probability to integer percentage
	prob = (str)((int)(top1[0]*100))
	# Remove category number
	item = top1[1].split(' ')
	item = ' '.join(item[1:])
	message = "I'm "+prob+"% sure that this is a "+item+". "
	return message

def tweet(message):
	auth = tweepy.OAuthHandler(CON_KEY,CON_SEC)
	auth.set_access_token(TOKEN,TOKEN_SEC)
	api = tweepy.API(auth)
	api.update_with_media(filename, status=message)

polly,cam=init()
message=see(polly,cam)
#PollyApi.speak(polly,message)
#tweet(message)

