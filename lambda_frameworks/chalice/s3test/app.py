import json
import boto3
from botocore.exceptions import ClientError

from chalice import Chalice
from chalice import NotFoundError

app = Chalice(app_name='s3test')

S3 = boto3.client('s3', region_name='eu-west-1')
BUCKET = 'jsimon-public'

@app.route('/objects/{key}', methods=['GET', 'PUT'])
def s3objects(key):
    request = app.current_request
    if request.method == 'PUT':
        S3.put_object(Bucket=BUCKET, Key=key,
                      Body=json.dumps(request.json_body))
    elif request.method == 'GET':
        try:
            response = S3.get_object(Bucket=BUCKET, Key=key)
            return json.loads(response['Body'].read())
        except ClientError as e:
            raise NotFoundError(key)

