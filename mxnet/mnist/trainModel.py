import mxnet as mx
import logging

logging.basicConfig(level=logging.INFO)

nb_epochs=100

train_iter = mx.io.MNISTIter(shuffle=True)
val_iter = mx.io.MNISTIter(image="./t10k-images-idx3-ubyte", label="./t10k-labels-idx1-ubyte")

data = mx.sym.Variable('data')
data = mx.sym.Flatten(data=data)
fc1  = mx.sym.FullyConnected(data=data, name='fc1', num_hidden=128)
act1 = mx.sym.Activation(data=fc1, name='relu1', act_type="relu")
fc2  = mx.sym.FullyConnected(data=act1, name='fc2', num_hidden = 64)
act2 = mx.sym.Activation(data=fc2, name='relu2', act_type="relu")
fc3  = mx.sym.FullyConnected(data=act2, name='fc3', num_hidden=10)
mlp  = mx.sym.SoftmaxOutput(data=fc3, name='softmax')

mod = mx.mod.Module(mlp)
mod.bind(data_shapes=train_iter.provide_data, label_shapes=train_iter.provide_label)
mod.init_params(initializer=mx.init.Xavier(magnitude=2.))
mod.init_optimizer('adam')
mod.fit(train_iter, eval_data=val_iter, num_epoch=nb_epochs,
	batch_end_callback=mx.callback.Speedometer(64, 100))

mod.save_checkpoint("mlp", nb_epochs)

metric = mx.metric.Accuracy()
mod.score(val_iter, metric)
print metric.get()

