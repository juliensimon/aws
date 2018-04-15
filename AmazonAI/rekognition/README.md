# Rekognition scripts in Python 

These scripts are built on top of:
- the Amazon Rekognition service
- the boto3 SDK (https://github.com/boto/boto3)
- the Pillow image processing library (https://github.com/python-pillow/Pillow)

<b>rekognitionCompare.py</b>

This script tries to find a reference face (stored in a source image) inside an image. If the face is found, it will be highlighted by a box and a new image will be saved. 

rekognitionCompare S3BucketName sourceImage targetImage [copy|nocopy]
- S3BucketName   : the S3 bucket where Rekognition will find the images
- sourceImage    : the image containing the reference face
- targetImage    : the image to match for the reference face
- copy           : copy the images to S3 (local names must match the parameters above)
- nocopy         : don't copy the images to S3 (they're already there)

Output: 
- reko_targetImage (local file), displaying a box around the matched face
- face information (stdout)

<b>rekognitionDetect.py</b>

This script tries to detect faces inside an image. If faces are found, each of them will be highlighted by a box and a new image will be saved. The script will also output image labels and face information (gender, beard, glasses, emotions, etc.)

rekognitionDetect S3BucketName image [copy|nocopy]
- S3BucketName  : the S3 bucket where Rekognition will find the image
- image         : the image to process
- copy          : copy the image to S3 (local name must match the parameter above)
- nocopy        : don't copy the image to S3 (it's already there)

Output: 
- reko_image (local file), displaying a box around each detected face
- labels & face information (stdout)

<b>Other files</b>

- rekognitionApi.py: wrapper functions for the Rekognition API, taking care of connection, JSON and default parameters
- rekognitionUtils.py: image processing functions (coordinates, drawing, output, etc.)
- awsUtils.py: what the name says ;)

