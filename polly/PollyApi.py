#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os, boto3

defaultRegion = 'us-east-1'
defaultUrl = 'https://polly.us-east-1.amazonaws.com'

def connectToPolly(regionName=defaultRegion, endpointUrl=defaultUrl):
    return boto3.client('polly', region_name=regionName, endpoint_url=endpointUrl)

def speak(polly, text, format='mp3', voice='Brian'):
    resp = polly.synthesize_speech(OutputFormat=format, Text=text, VoiceId=voice)
    soundfile = open('/tmp/sound.mp3', 'w')
    soundBytes = resp['AudioStream'].read()
    soundfile.write(soundBytes)
    soundfile.close()
    os.system('afplay /tmp/sound.mp3')  # Works only on Mac OS, sorry
    os.remove('/tmp/sound.mp3')

polly = connectToPolly()
speak(polly, "Hello world, I'm Polly. Or Brian. Or anyone you want, really.")
frenchString = "Et bien sûr, je parle très bien français ! ça vous étonne ?"
speak(polly, frenchString.decode('utf8'), voice='Mathieu')
icelandicString = "Gera þú hafa allir hugmynd um hvað ég er að segja? Örugglega ekki !"
speak(polly, icelandicString.decode('utf8'), voice='Karl')
