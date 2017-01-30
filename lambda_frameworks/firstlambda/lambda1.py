def lambda_handler(event,context):
	result = int(event['value1']) + int(event['value2'])
	return result
