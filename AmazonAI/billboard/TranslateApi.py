#!/usr/bin/env python
# -*- coding: utf-8 -*-

import boto3

defaultRegion = 'us-east-1'

def connect(regionName=defaultRegion):
    return boto3.client('translate', region_name=regionName)

def translateText(client, text, source, target):
	response = client.translate_text(Text=text, SourceLanguageCode=source, TargetLanguageCode=target)
	return response['TranslatedText']

if __name__ == '__main__':
	client = connect()
	print(translateText(client, 'Hello World', 'en', 'es'))

