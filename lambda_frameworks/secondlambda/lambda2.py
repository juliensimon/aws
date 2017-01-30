import os
import json
import time
import boto3

def lambda_handler(event,context):
    # Transform JSON event to Python dictionary
    b = json.loads(event['body'])
    # Add the integers
    result = int(b['value1']) + int(b['value2'])
    # Store the result in the event
    event['result'] = result
    # Use a timestamp as item id in the DynamoDB table
    event['id'] = str(time.time())
    # Store the event in the DynamoDB table
    tableName = os.environ['TABLE_NAME']
    table = boto3.resource('dynamodb').Table(tableName)
    table.put_item(Item=event)
