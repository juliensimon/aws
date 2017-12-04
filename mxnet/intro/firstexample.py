import mxnet as mx
import numpy as np
import logging

logging.basicConfig(level=logging.INFO)

# Number of samples and how to split them between training and validation
sample_count = 10000
train_count = 8000
valid_count = sample_count - train_count

# Number of features for each sample
feature_count = 100
# Number of categories (labels)
category_count = 10 
# Batch size
batch = 16

# Create random samples from a [0,1] uniform distribution
X = mx.nd.uniform(low=0, high=1, shape=(sample_count,feature_count))
# Create random labels
Y = mx.nd.empty((sample_count,))
for i in range(0,sample_count-1):
  Y[i] = np.random.randint(0,category_count)

# Split samples and labels to create training set 
X_train = mx.nd.crop(X, begin=(0,0), end=(train_count,feature_count))
Y_train = Y[0:train_count]
# Split samples and labels to create validation set
X_valid = mx.nd.crop(X, begin=(train_count,0), end=(sample_count,feature_count))
Y_valid = Y[train_count:sample_count]

print(X.shape, Y.shape, X_train.shape, Y_train.shape, X_valid.shape, Y_valid.shape)

# Build network: input -> 1 fully connected hidden layer -> output 
data = mx.sym.Variable('data')
fc1 = mx.sym.FullyConnected(data, name='fc1', num_hidden=1024)
relu1 = mx.sym.Activation(fc1, name='relu1', act_type="relu")
fc2 = mx.sym.FullyConnected(relu1, name='fc2', num_hidden=category_count)
out = mx.sym.SoftmaxOutput(fc2, name='softmax')
mod = mx.mod.Module(out)

# Build training iterator
train_iter = mx.io.NDArrayIter(data=X_train,label=Y_train,batch_size=batch)

# This is how you would print samples and labels in the training set
#for batch in train_iter:
#  print batch.data
#  print batch.label
#train_iter.reset()

# Bind training iterator to model, set initial weights, set optimizer
mod.bind(data_shapes=train_iter.provide_data, label_shapes=train_iter.provide_label)
mod.init_params(initializer=mx.init.Xavier(magnitude=2.))
mod.init_optimizer(optimizer='sgd', optimizer_params=(('learning_rate', 0.1), ))
# Train model
mod.fit(train_iter, num_epoch=50)

# Build validation iterator
val_iter = mx.io.NDArrayIter(data=X_valid,label=Y_valid, batch_size=batch)

# This is how you would predict and display predicted labels
#for batch in val_iter:
  #print batch.label
  #mod.forward(batch)
  #prob = mod.get_outputs()[0].asnumpy()
  #print prob
#val_iter.reset()

# Measure model accuracy
metric = mx.metric.Accuracy()
mod.score(val_iter, metric)
print metric.get()
