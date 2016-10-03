from __future__ import print_function

import boto3
import json

print('Loading function')

def lambda_handler(event, context):
    firehose = boto3.client('firehose')

    print("Received event: " + json.dumps(event, indent=2))
    
    for record in event['Records']:
        print(record['eventID'])
        print(record['eventName'])
        #print("DynamoDB Record: " + json.dumps(record['dynamodb'], indent=2))
        firehose.put_record(DeliveryStreamName="firehoseToS3", Record={"Data":json.dumps(record['dynamodb'])})
        
    return 'Successfully processed {} records.'.format(len(event['Records']))
