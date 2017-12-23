#!/usr/bin/env python
# -*- coding: utf-8 -*-

import boto3

defaultRegion = 'us-east-1'

def connect(regionName=defaultRegion):
    return boto3.client('rekognition', region_name=regionName)

def detectText(client, bucket, image):
	response = client.detect_text(Image={'S3Object': {'Bucket':bucket, 'Name':image}})
	text = '' 
	for t in response['TextDetections']:
		if t['Type'] == 'LINE':
			text = text+t['DetectedText']+' '
	return text

if __name__ == '__main__':
	client = connect()
	print(detectText(client, 'jsimon-public-us', 'billboard.jpg'))

