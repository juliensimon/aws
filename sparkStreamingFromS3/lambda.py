import boto3
import json

print('Loading function')

def lambda_handler(event, context):
    firehose = boto3.client('firehose')

    #print("Received EC2 event: " + json.dumps(event))
    
    id = event['detail']['instance-id']
    state = event['detail']['state']
    print("Instance id : " + id)
    print("Instance state : " + state)
    
    #if (state == "pending"):
        # Run some initialization tasks
    #elif (state == "terminating"):
        # Run some cleanup tasks

    firehose.put_record(DeliveryStreamName="firehoseToS3", Record={"Data":json.dumps(event)})

