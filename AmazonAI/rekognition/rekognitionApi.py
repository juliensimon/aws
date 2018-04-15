# Author : Julien Simon <julien@julien.org>, 29/10/2016
# Disclaimer : this is for educational purposes only.
# If this script destroys your infrastructure, sets your car on fire
# or breaks your marriage, it's all your fault, not mine.

import boto3

defaultRegion = 'eu-west-1'
defaultUrl = 'https://rekognition.eu-west-1.amazonaws.com'

def connectToRekognitionService(regionName=defaultRegion, endpointUrl=defaultUrl):
    return boto3.client('rekognition', region_name=regionName, endpoint_url=endpointUrl)

def detectFaces(rekognition, imageBucket, imageFilename, attributes='ALL'):
    resp = rekognition.detect_faces(
            Image = {"S3Object" : {'Bucket' : imageBucket, 'Name' : imageFilename}},
            Attributes=[attributes])
    return resp['FaceDetails']

def compareFaces(rekognition, imageBucket, imageSourceFilename, imageTargetFilename):
    resp = rekognition.compare_faces(
            SourceImage = {"S3Object" : {'Bucket' : imageBucket, 'Name' : imageSourceFilename}},
            TargetImage = {"S3Object" : {'Bucket' : imageBucket, 'Name' : imageTargetFilename}})
    return resp['FaceMatches']

def detectLabels(rekognition, imageBucket, imageFilename, maxLabels=100, minConfidence=0):
    resp = rekognition.detect_labels(
        Image = {"S3Object" : {'Bucket' : imageBucket, 'Name' : imageFilename}},
        MaxLabels = maxLabels, MinConfidence = minConfidence)
    return resp['Labels']
