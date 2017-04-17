
import mxnet as mx
import numpy as np
import cv2
from collections import namedtuple

def loadInceptionv3():
	sym, arg_params, aux_params = mx.model.load_checkpoint('Inception-BN', 0)
	mod = mx.mod.Module(symbol=sym)
	mod.bind(for_training=False, data_shapes=[('data', (1,3,224,224))])
	mod.set_params(arg_params, aux_params)
	return mod

def loadCategories():
	synsetfile = open('synset.txt', 'r')
	synsets = []
	for l in synsetfile:
		synsets.append(l.rstrip())
	return synsets

def prepareNDArray(filename):
	img = cv2.imread(filename)
	img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
	img = cv2.resize(img, (224, 224,))
	img = np.swapaxes(img, 0, 2)
    	img = np.swapaxes(img, 1, 2)
    	img = img[np.newaxis, :]
	return mx.nd.array(img) 

def predict(filename, model, categories, n):
	array = prepareNDArray(filename)
	Batch = namedtuple('Batch', ['data'])
	model.forward(Batch([array]))
    	prob = model.get_outputs()[0].asnumpy()
	prob = np.squeeze(prob)
    	sortedprobindex = np.argsort(prob)[::-1]
	topn = []
    	for i in sortedprobindex[0:n]:
        	topn.append((prob[i], categories[i]))
    	return topn

def init(): 
	model = loadInceptionv3()
	cats = loadCategories()
	return model, cats

m,c = init()
topn = predict("/tmp/kreator.jpeg",m,c,5)
print topn
