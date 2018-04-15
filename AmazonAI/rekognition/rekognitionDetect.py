#!/usr/bin/env python

# Author : Julien Simon <julien@julien.org>, 29/10/2016
# Disclaimer : this is for educational purposes only.
# If this script destroys your infrastructure, sets your car on fire
# or breaks your marriage, it's all your fault, not mine.

import sys
import rekognitionUtils as utils
import rekognitionApi as api
import awsUtils

import PollyApi

def usage():
    print('\nrekognitionDetect <S3BucketName> <image> [copy|nocopy]\n')
    print('S3BucketName  : the S3 bucket where Rekognition will find the image')
    print('image         : the image to process')
    print('copy          : copy the image to S3 (local name must match the parameter above)')
    print('nocopy        : don\'t copy the image to S3 (it\'s already there)\n')
    print('Output : reko_<image> (local file), displaying a box around each detected face')
    print('         labels & face information (stdout)\n')

if (len(sys.argv) != 4):
    usage()
    sys.exit()

imageBucket = str(sys.argv[1])
image       = str(sys.argv[2])

if (str(sys.argv[3]) == 'copy'):
    awsUtils.copyLocalFileToS3(image, imageBucket)

imageInfo   = utils.openLocalImage(image)

reko = api.connectToRekognitionService()
polly = PollyApi.connectToPolly()

labels = api.detectLabels(reko, imageBucket, image, maxLabels=10, minConfidence=70.0)
utils.printLabelsInformation(labels)

faceList = api.detectFaces(reko, imageBucket, image)
faceCounter = 0
for face in faceList:
    utils.printFaceInformation(face, faceCounter)
    utils.drawLinesAroundFace(imageInfo, face)
    utils.drawLegendForFace(imageInfo, face, faceCounter)
    faceCounter=faceCounter+1
utils.saveImage(image, imageInfo)

if (faceCounter == 0):
    message = "No face has been detected, sorry"
else:
    if (faceCounter == 1):
        message = "A single face has been detected"
    else:
        message = str(faceCounter)+ " faces have been detected"

labelText = ''
for l in labels:
    if (l['Confidence'] > 80.0):
        labelText = labelText + l['Name'] + ", "

PollyApi.speak(polly, message)
if (labelText != ''):
    PollyApi.speak(polly, "Here are some keywords about this picture: " + labelText)
