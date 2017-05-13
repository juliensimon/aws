import mxnet as mx
import numpy as np
import cv2, os, urllib, time
from collections import namedtuple
Batch = namedtuple('Batch', ['data'])

# Load the symbols for the networks
with open('./synset.txt', 'r') as f:
    synsets = [l.rstrip() for l in f]

# Load the network parameters
sym, arg_params, aux_params = mx.model.load_checkpoint('Inception-BN', 0)

# Load the network into an MXNet module and bind the corresponding parameters
mod = mx.mod.Module(symbol=sym, context=mx.cpu())
mod.bind(for_training=False, data_shapes=[('data', (1,3,224,224))])
mod.set_params(arg_params, aux_params)
 
'''
Function to predict objects by giving the model a pointer to an image file and running a forward pass through the model.

inputs:
filename = jpeg file of image to classify objects in
mod = the module object representing the loaded model
synsets = the list of symbols representing the model
N = Optional parameter denoting how many predictions to return (default is top 5)

outputs:
python list of top N predicted objects and corresponding probabilities
'''
def predict(filename, mod, synsets, N=5):
    tic = time.time()
    img = cv2.cvtColor(cv2.imread(filename), cv2.COLOR_BGR2RGB)
    if img is None:
        return None
    img = cv2.resize(img, (224, 224))
    img = np.swapaxes(img, 0, 2)
    img = np.swapaxes(img, 1, 2)
    img = img[np.newaxis, :]
    print "pre-processed image in "+str(time.time()-tic)
 
    toc = time.time()
    mod.forward(Batch([mx.nd.array(img)]))
    prob = mod.get_outputs()[0].asnumpy()
    prob = np.squeeze(prob)
    print "forward pass in "+str(time.time()-toc)
 
 
    topN = []
    a = np.argsort(prob)[::-1]
    for i in a[0:N]:
        print('probability=%f, class=%s' %(prob[i], synsets[i]))
        topN.append((prob[i], synsets[i]))
    return topN


# Code to download an image from the internet and run a prediction on it
def predict_from_url(url, N=5):
    filename = url.split("/")[-1]
    urllib.urlretrieve(url, filename)
    img = cv2.imread(filename)
    if img is None:
        print "Failed to download"
    else:
        return predict(filename, mod, synsets, N)

# Code to predict on a local file
def predict_from_local_file(filename, N=5):        
    return predict(filename, mod, synsets, N)

