#! /usr/bin/python
import mxnet as mx 
import numpy as np
import cv2
import cPickle
import logging
from symbols import resnext

logging.basicConfig(level=logging.DEBUG)


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
    print training_data.shape    
    print training_label.shape    
    return training_data, training_label


#####################################################################

path="cifar-10-batches-py/"
examples=50000
batch=132
epochs=300

# Method 1: load from raw files
#training_data, training_label = buildTrainingSet(path)
#train_iter = mx.io.NDArrayIter(data=training_data,label=training_label,batch_size=batch, shuffle=True)
#valid_data, valid_label = extractImagesAndLabels(path, "data_batch_5")
#valid_iter = mx.io.NDArrayIter(data=valid_data,label=valid_label,batch_size=batch, shuffle=True)

# Method 2: load from Image RecordIO files
train_iter = mx.io.ImageRecordIter(path_imgrec="cifar10_train.rec", data_name="data", label_name="softmax_label", batch_size=batch, data_shape=(3,32,32), shuffle=True)
valid_iter = mx.io.ImageRecordIter(path_imgrec="cifar10_val.rec", data_name="data", label_name="softmax_label", batch_size=batch, data_shape=(3,32,32))

# Use ResNext-110
sym = resnext.get_symbol(10, 110, "3,32,32")
mod = mx.mod.Module(symbol=sym, context=(mx.gpu(0), mx.gpu(1), mx.gpu(2), mx.gpu(3)))
mod.bind(data_shapes=train_iter.provide_data, label_shapes=train_iter.provide_label)
mod.init_params(initializer=mx.init.Xavier())

#steps_per_epoch = examples / batch 
#decrease_lr_after_steps = steps_per_epoch * 25
#lr_sch = mx.lr_scheduler.FactorScheduler(step=decrease_lr_after_steps,factor=0.9)

#mod.fit(train_iter, eval_data=valid_iter, optimizer='adadelta', optimizer_params={'learning_rate':0.1, 'lr_scheduler':lr_sch}, num_epoch=epochs)
mod.fit(train_iter, eval_data=valid_iter, optimizer='adadelta', num_epoch=epochs)
mod.save_checkpoint("resnext-101-symbol", epochs)


 
