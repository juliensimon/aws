#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os, boto3

defaultRegion = 'eu-west-1'
defaultUrl = 'https://polly.eu-west-1.amazonaws.com'

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

