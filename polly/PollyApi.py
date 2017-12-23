#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os, boto3, pygame

defaultRegion = 'eu-west-1'
defaultUrl = 'https://polly.eu-west-1.amazonaws.com'

def connectToPolly(regionName=defaultRegion, endpointUrl=defaultUrl):
    return boto3.client('polly', region_name=regionName, endpoint_url=endpointUrl)

def play(filename):
    pygame.init()
    pygame.mixer.music.load(filename)
    pygame.mixer.music.play()
    while pygame.mixer.music.get_busy():
        pygame.time.Clock().tick(10)

def speak(polly, text, format='ogg_vorbis', voice='Brian'):
    filename="/tmp/sound"
    resp = polly.synthesize_speech(OutputFormat=format, Text=text, VoiceId=voice)
    soundfile = open(filename, 'w')
    soundBytes = resp['AudioStream'].read()
    soundfile.write(soundBytes)
    soundfile.close()
    play('/tmp/sound')
    os.remove(filename)

if __name__=='__main__':
	polly = connectToPolly()
	speak(polly, "Hello world, I'm Polly. Or Brian. Or anyone you want, really.")
	frenchString = "Et bien sûr, je parle très bien français ! ça vous étonne ?"
	speak(polly, frenchString.decode('utf8'), voice='Mathieu')
	icelandicString = "Gera þú hafa allir hugmynd um hvað ég er að segja? Örugglega ekki !"
	speak(polly, icelandicString.decode('utf8'), voice='Karl')
