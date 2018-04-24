from chalice import Chalice
import base64, cv2
import numpy as np

app = Chalice(app_name="resizer")
#app.debug = True

@app.route('/', methods=['POST'])
def index():  
    body = app.current_request.json_body

    if 'data' not in body:
        raise BadRequestError('Missing image data')
    if 'height' not in body:
        raise BadRequestError('Missing image height')
    if 'width' not in body:
        raise BadRequestError('Missing image width')

    h = body['height']
    w = body['width']
    image = base64.b64decode(body['data'])
    L = len(image)

    image = np.fromstring(image, np.uint8)
    image = cv2.imdecode(image, cv2.IMREAD_COLOR)
    H = image.shape[0]
    W = image.shape[1]
    image = cv2.resize(image, (h, w,))
    image = cv2.imencode('.jpeg', image)

    data = base64.b64encode(image[1].tostring())

    print("%d %d %d %d %d " % (L, H, W, h, w))

    return { 'data': data }
