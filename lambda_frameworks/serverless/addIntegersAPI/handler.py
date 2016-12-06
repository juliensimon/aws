import json

def add(event, context):

	inputDoc = json.loads(event['body'])
	sum = str(inputDoc['value1'] + inputDoc['value2'])
	result = json.dumps("{'sum':" + sum + "}")

	return { "statusCode": 200, "body": result }


