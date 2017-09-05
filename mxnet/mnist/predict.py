import mxnet as mx
import numpy as np
import cv2
from collections import namedtuple

def loadModel():
	model, arg_params, aux_params = mx.model.load_checkpoint("mlp", 10)
	#model, arg_params, aux_params = mx.model.load_checkpoint("lenet", 10)
	mod = mx.mod.Module(model)
	mod.bind(for_training=False, data_shapes=[('data', (1,1,28,28))])
	mod.set_params(arg_params, aux_params)
	return mod

def loadImage(filename):
	img = cv2.imread(filename, cv2.IMREAD_GRAYSCALE)
	img = img / 255
	#print img
	img = np.expand_dims(img, axis=0)
	img = np.expand_dims(img, axis=0)
	return mx.nd.array(img)

def predict(model, filename):
	array = loadImage(filename)
	
	Batch = namedtuple('Batch', ['data'])
	mod.forward(Batch([array]))
	pred = mod.get_outputs()[0].asnumpy()
	return pred

np.set_printoptions(precision=3, suppress=True)

mod = loadModel()
print predict(mod, "./0.png")
print predict(mod, "./1.png")
print predict(mod, "./2.png")
print predict(mod, "./3.png")
print predict(mod, "./4.png")
print predict(mod, "./5.png")
print predict(mod, "./6.png")
print predict(mod, "./7.png")
print predict(mod, "./8.png")
print predict(mod, "./9.png")

