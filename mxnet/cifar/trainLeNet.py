#! /usr/bin/python
import mxnet as mx 
import numpy as np
import cv2
import cPickle
import logging

logging.basicConfig(level=logging.DEBUG)

def extractImagesAndLabels(path, file):
    f = open(path+file, 'rb')
    dict = cPickle.load(f)
    images = dict['data']
    images = np.reshape(images, (10000, 3, 32, 32))
    labels = dict['labels']
    imagearray = mx.nd.array(images)
    labelarray = mx.nd.array(labels)
    return imagearray, labelarray

def extractCategories(path, file):
    f = open(path+file, 'rb')
    dict = cPickle.load(f)
    return dict['label_names'] 

def saveCifarImage(array, path, file):
    # array is 3x32x32. cv2 needs 32x32x3
    array = array.transpose(1,2,0)
    # array is RGB. cv2 needs BGR
    array = cv2.cvtColor(array, cv2.COLOR_RGB2BGR)
    # save to PNG file
    return cv2.imwrite(path+file+".png", array)

def buildLeNet():
    data = mx.symbol.Variable('data')
    conv1 = mx.sym.Convolution(data=data, kernel=(5,5), num_filter=128)
    tanh1 = mx.sym.Activation(data=conv1, act_type="tanh")
    pool1 = mx.sym.Pooling(data=tanh1, pool_type="max", kernel=(2,2), stride=(2,2))
    conv2 = mx.sym.Convolution(data=pool1, kernel=(5,5), num_filter=256)
    tanh2 = mx.sym.Activation(data=conv2, act_type="tanh")
    pool2 = mx.sym.Pooling(data=tanh2, pool_type="max", kernel=(2,2), stride=(2,2))
    flatten = mx.sym.Flatten(data=pool2)
    fc1 = mx.symbol.FullyConnected(data=flatten, num_hidden=500)
    tanh3 = mx.sym.Activation(data=fc1, act_type="tanh")
    fc2 = mx.sym.FullyConnected(data=tanh3, num_hidden=10)
    lenet = mx.sym.SoftmaxOutput(data=fc2, name='softmax')
    return lenet

path="cifar-10-batches-py/"
batch=128

#for i in range(0,10):
#    saveCifarImage(i1[i], "./", "img"+(str)(i))
#    print cats[l1[i]]

training_data = []
training_label = []

for f in ("data_batch_1", "data_batch_2", "data_batch_3", "data_batch_4", "data_batch_5"):
    imgarray, lblarray = extractImagesAndLabels(path, f)
    if not training_data:
        training_data = imgarray
        training_label = lblarray
    else:
        training_data = mx.nd.concatenate([training_data, imgarray])
        training_label = mx.nd.concatenate([training_label, lblarray])

print training_data.shape    
print training_label.shape    

train_iter = mx.io.NDArrayIter(data=training_data,label=training_label,batch_size=batch)

valid_data, valid_label = extractImagesAndLabels(path, "test_batch")
valid_iter = mx.io.NDArrayIter(data=valid_data,label=valid_label,batch_size=batch)

lenet = buildLeNet()
mod = mx.mod.Module(lenet, context=(mx.gpu(0), mx.gpu(1), mx.gpu(2), mx.gpu(3)))
mod.bind(data_shapes=train_iter.provide_data, label_shapes=train_iter.provide_label)
mod.init_params(initializer=mx.init.Normal())
mod.fit(train_iter, eval_data=valid_iter, optimizer_params={'learning_rate':0.01, 'momentum': 0.9}, num_epoch=300)

mod.save_checkpoint("lenet", 300)

