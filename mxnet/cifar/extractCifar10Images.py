import mxnet as mx
import numpy as np
import cPickle
import cv2

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
    array = array.asnumpy().transpose(1,2,0)
    # array is RGB. cv2 needs BGR
    array = cv2.cvtColor(array, cv2.COLOR_RGB2BGR)
    # save to PNG file
    return cv2.imwrite(path+file+".png", array)

imgarray, lblarray = extractImagesAndLabels("cifar-10-batches-py/", "data_batch_1")
print imgarray.shape
print lblarray.shape

categories = extractCategories("cifar-10-batches-py/", "batches.meta") 

cats = []
for i in range(0,10):
    saveCifarImage(imgarray[i], "./", "img"+(str)(i))
    category = lblarray[i].asnumpy()
    category = (int)(category[0])
    cats.append(categories[category])
print cats


