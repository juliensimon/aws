#! /usr/bin/python
import mxnet as mx 
import numpy as np
import cv2
import cPickle
import logging

logging.basicConfig(level=logging.DEBUG)

path="cifar-10-batches-py/"
batch=128
epochs=300

train_iter = mx.io.ImageRecordIter(path_imgrec="cifar10_train.rec", data_name="data", label_name="softmax_label", batch_size=batch, data_shape=(3,32,32), shuffle=True)
valid_iter = mx.io.ImageRecordIter(path_imgrec="cifar10_val.rec", data_name="data", label_name="softmax_label", batch_size=batch, data_shape=(3,32,32))

# Use ResNext-101
sym, arg_params, aux_params = mx.model.load_checkpoint("resnext-101",0)
mod = mx.mod.Module(symbol=sym, context=(mx.gpu(0), mx.gpu(1), mx.gpu(2), mx.gpu(3)))
mod.bind(data_shapes=train_iter.provide_data, label_shapes=train_iter.provide_label)
mod.set_params(arg_params, aux_params)
mod.fit(train_iter, eval_data=valid_iter, optimizer_params={'learning_rate':0.05, 'momentum':0.9}, num_epoch=epochs)
mod.save_checkpoint("resnext-101-001", epochs)

 
