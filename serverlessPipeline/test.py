import random
import requests
import json

url = 'https://0vonh13wrd.execute-api.eu-west-1.amazonaws.com/prod/logger'
headers = {'content-type': 'application/json'}

for x in range(1,10000):
	payload = {'userId' : random.randrange(111111111, 999999999), 'value' : random.randrange(111111111,999999999)}
	response = requests.post(url, data=json.dumps(payload), headers=headers)
	print response

