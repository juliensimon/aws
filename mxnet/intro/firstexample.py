import mxnet as mx
import numpy as np
import logging

logging.basicConfig(level=logging.INFO)

sample_count = 1000
train_count = 800
valid_count = sample_count - train_count

feature_count = 100
category_count = 10 
batch = 16

X = mx.nd.uniform(low=0, high=1, shape=(sample_count,feature_count))
Y = mx.nd.empty((sample_count,))
for i in range(0,sample_count-1):
  Y[i] = np.random.randint(0,category_count)

X_train = mx.nd.crop(X, begin=(0,0), end=(train_count,feature_count))
Y_train = Y[0:train_count]

X_valid = mx.nd.crop(X, begin=(train_count,0), end=(sample_count,feature_count))
Y_valid = Y[train_count:sample_count]

print(X.shape, Y.shape, X_train.shape, Y_train.shape, X_valid.shape, Y_valid.shape)

# Build network
data = mx.sym.Variable('data')
fc1 = mx.sym.FullyConnected(data, name='fc1', num_hidden=1024)
relu1 = mx.sym.Activation(fc1, name='relu1', act_type="relu")
fc2 = mx.sym.FullyConnected(relu1, name='fc2', num_hidden=category_count)
out = mx.sym.SoftmaxOutput(fc2, name='softmax')
mod = mx.mod.Module(out)

# Build iterator
train_iter = mx.io.NDArrayIter(data=X_train,label=Y_train,batch_size=batch)
#for batch in train_iter:
#  print batch.data
#  print batch.label

# Train model
mod.bind(data_shapes=train_iter.provide_data, label_shapes=train_iter.provide_label)
mod.init_params(initializer=mx.init.Xavier(magnitude=2.))
mod.init_optimizer(optimizer='sgd', optimizer_params=(('learning_rate', 0.1), ))
mod.fit(train_iter, num_epoch=50)

val_iter = mx.io.NDArrayIter(data=X_valid,label=Y_valid, batch_size=batch)

#for batch in val_iter:
  #print batch.label
  #mod.forward(batch)
  #prob = mod.get_outputs()[0].asnumpy()
  #print prob
#val_iter.reset()

metric = mx.metric.Accuracy()
mod.score(val_iter, metric)
print metric.get()
