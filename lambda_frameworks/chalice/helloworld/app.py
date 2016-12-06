from chalice import Chalice

app = Chalice(app_name="helloworld")

@app.route("/")
def index():
    return {"hello": "world"}

@app.route('/hello/{value}', methods=['PUT'])
def put_test(value):
    return {"hello": value}
