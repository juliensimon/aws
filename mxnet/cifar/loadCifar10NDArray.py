#! /usr/bin/python
import mxnet as mx 
import numpy as np
import cv2
import cPickle
import logging

def extractImagesAndLabels(path, file):
    f = open(path+file, 'rb')
    dict = cPickle.load(f)
    images = dict['data']
    images = np.reshape(images, (10000, 3, 32, 32))
    labels = dict['labels']
    imagearray = mx.nd.array(images)
    labelarray = mx.nd.array(labels)
    return imagearray, labelarray

def buildTrainingSet(path):
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
    return training_data, training_label


path="cifar-10-batches-py/"
batch=128

training_data, training_label = buildTrainingSet(path)
train_iter = mx.io.NDArrayIter(data=training_data,label=training_label,batch_size=batch, shuffle=True)
valid_data, valid_label = extractImagesAndLabels(path, "test_batch")
valid_iter = mx.io.NDArrayIter(data=valid_data,label=valid_label,batch_size=batch, shuffle=True)

print training_data.shape
print training_label.shape
print valid_data.shape
print valid_label.shape

