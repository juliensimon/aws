from flask import Flask, render_template, request, url_for
import random
import requests
import json
import os
import binascii

# Initialize the Flask application
app = Flask(__name__)

# Define a route for the default URL, which loads the form
@app.route('/')
def home():
	userId = random.randrange(111111111, 999999999)
	value = random.randrange(111111111, 999999999)
        data = binascii.b2a_hex(os.urandom(64))
	url = 'https://0vonh13wrd.execute-api.eu-west-1.amazonaws.com/prod/logger'
	headers = {'content-type': 'application/json'}
	payload = {'userId' : userId, 'value' : value, 'data' : data}
	#print payload
	response = requests.post(url, data=json.dumps(payload), headers=headers)
	#print response
	return render_template('form_action.html', i=userId, v=value, d=data)

# Run the app :)
if __name__ == '__main__':
  app.run( 
        host="0.0.0.0",
        port=int("80"),
        threaded=True
  )


