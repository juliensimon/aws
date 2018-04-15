#!/usr/bin/env python

# Author : Julien Simon <julien@julien.org>, 29/10/2016
# Disclaimer : this is for educational purposes only.
# If this script destroys your infrastructure, sets your car on fire
# or breaks your marriage, it's all your fault, not mine.

import sys
import rekognitionUtils as utils
import rekognitionApi as api
import awsUtils

def usage():
    print('\nrekognitionCompare <S3BucketName> <sourceImage> <targetImage> [copy|nocopy]\n')
    print('S3BucketName   : the S3 bucket where Rekognition will find the images')
    print('sourceImage : the image containing the reference face')
    print('targetImage    : the image to match for the reference face')
    print('copy           : copy the images to S3 (local names must match the parameters above)')
    print('nocopy         : don\'t copy the images to S3 (they\'re already there)\n')
    print('Output : reko_<targetImage> (local file), displaying a box around the matched face')
    print('         face information (stdout)\n')

if (len(sys.argv) != 5):
    usage()
    sys.exit()


imageBucket  = str(sys.argv[1])
sourceImage  = str(sys.argv[2])
targetImage  = str(sys.argv[3])
copyFiles    = str(sys.argv[4])

if (copyFiles == 'copy'):
    awsUtils.copyLocalFileToS3(sourceImage, imageBucket)
    awsUtils.copyLocalFileToS3(targetImage, imageBucket)

targetInfo = utils.openLocalImage(targetImage)

reko = api.connectToRekognitionService()
matchList = api.compareFaces(reko, imageBucket, sourceImage, targetImage)
faceCounter = 0
for match in matchList:
    utils.printFaceMatchInformation(match)
    face = utils.getFaceFromFaceMatch(match)
    utils.drawLinesAroundFace(targetInfo, face)
    utils.drawLegendForFace(targetInfo, face, faceCounter)
    faceCounter=faceCounter+1

utils.saveImage(targetImage, targetInfo)
