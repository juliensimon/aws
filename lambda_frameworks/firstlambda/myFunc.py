def lambda_handler(event,context):
	result = event['value1'] + event['value2']
	return result
