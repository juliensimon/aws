#!/usr/bin/env python
# -*- coding: utf-8 -*-

import boto3

defaultRegion = 'us-east-1'

def connect(regionName=defaultRegion):
    return boto3.client('comprehend', region_name=regionName)

def detectLanguage(client,text):
	resp = client.detect_dominant_language(Text=text)
	return resp['Languages'][0]['LanguageCode']

if __name__ == '__main__':
	client = connect()
	print(detectLanguage(client, "Hello World"))

