import mxnet as mx
import logging

logging.basicConfig(level=logging.INFO)

train_iter = mx.io.MNISTIter(shuffle=True)
val_iter = mx.io.MNISTIter(image="./t10k-images-idx3-ubyte", label="./t10k-labels-idx1-ubyte")

data = mx.symbol.Variable('data')
conv1 = mx.sym.Convolution(data=data, kernel=(5,5), num_filter=20)
tanh1 = mx.sym.Activation(data=conv1, act_type="tanh")
pool1 = mx.sym.Pooling(data=tanh1, pool_type="max", kernel=(2,2), stride=(2,2))
conv2 = mx.sym.Convolution(data=pool1, kernel=(5,5), num_filter=50)
tanh2 = mx.sym.Activation(data=conv2, act_type="tanh")
pool2 = mx.sym.Pooling(data=tanh2, pool_type="max", kernel=(2,2), stride=(2,2))
flatten = mx.sym.Flatten(data=pool2)
fc1 = mx.symbol.FullyConnected(data=flatten, num_hidden=500)
tanh3 = mx.sym.Activation(data=fc1, act_type="tanh")
fc2 = mx.sym.FullyConnected(data=tanh3, num_hidden=10)
lenet = mx.sym.SoftmaxOutput(data=fc2, name='softmax')

#mod = mx.mod.Module(lenet)
#mod = mx.mod.Module(lenet, context=mx.gpu(0))
mod = mx.mod.Module(lenet, context=(mx.gpu(0), mx.gpu(1), mx.gpu(2)))

mod.bind(data_shapes=train_iter.provide_data, label_shapes=train_iter.provide_label)
mod.init_params(initializer=mx.init.Xavier(magnitude=2.))
mod.init_optimizer(optimizer_params=(('learning_rate', 0.1), ))
mod.fit(train_iter, num_epoch=10)

mod.save_checkpoint("lenet", 10)

metric = mx.metric.Accuracy()
mod.score(val_iter, metric)
print metric.get()

