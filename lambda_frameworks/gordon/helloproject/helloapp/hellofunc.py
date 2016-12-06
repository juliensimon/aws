import json

def handler(event, context):
    print("Received Event: " + json.dumps(event, indent=2))
    data = "Hello " + event['name']
    #data = "Hello !"
    return data
