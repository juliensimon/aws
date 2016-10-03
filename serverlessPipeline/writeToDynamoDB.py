from __future__ import print_function

import boto3
import json
import time

print('Loading function')

def lambda_handler(event, context):

    print("Received event: " + json.dumps(event, indent=2))
    event['timestamp'] = int(time.time())
    table = boto3.resource('dynamodb').Table("eventTable")
    return table.put_item(Item=event)
