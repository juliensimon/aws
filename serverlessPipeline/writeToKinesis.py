import boto3
import json
import time

print('Loading function')

def lambda_handler(event, context):
    print("Received event: " + json.dumps(event, indent=2))
    event['timestamp'] = int(time.time())
    return boto3.client('kinesis').put_record(StreamName="APIToDynamoDB", Data=json.dumps(event), PartitionKey=str(event['userId']))
