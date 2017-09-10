from keras.models import load_model
from keras.datasets import cifar10
from keras.utils import np_utils
from keras.optimizers import SGD
from keras.backend import backend
import numpy as np

# CIFAR-10 classes: airplane, automobile, bird, cat, deer, dog, frog, horse, ship, truck
# training set: 10 classes with 5000 samples each (50,000 samples total)
# test set: 10 classes with 1000 samples each (10,000 samples total)
nb_classes=10

first_class=1 
second_class=7
nb_samples=5000
nb_samples_test=1000

nb_epochs=10

nb_gpus=1
batch=32

def get_gpus(count):
	gpu_list = []
	for i in range(count): 
		gpu_list.append('gpu(%d)' % i)
	return gpu_list

def normalize_samples(x):
	# Divide pixel values by 255 to obtain a a float value between 0 and 1
	x = x.astype('float32')
	x /= 255
	return x

def get_samples(x, y, cifar10_class, count):
	# Find indexes of labels matching the right category
	y_indexes = np.where(y == cifar10_class)[0]
	# Take the 'count' first indexes
	y_indexes = y_indexes[:count]
	# Extract samples and labels for these indexes
	y_samples = y[y_indexes]
	x_samples = x[y_indexes] 
	# Normalize pixel values (this is how the model was trained)
	x_samples = normalize_samples(x_samples)
	return x_samples, y_samples

def prepare_dataset(x, y):
	# Get 'nb_samples' samples and labels for first category
	x0, y0 = get_samples(x, y, first_class, nb_samples)
	# Get 'nb_samples' samples and labels for second category
	x1, y1 = get_samples(x, y, second_class, nb_samples)
	# Build the sample dataset
	X = np.concatenate((x0, x1))
	# Build the label dataset
	Y = np.concatenate((y0, y1))
	Y = np_utils.to_categorical(Y, nb_classes) # One-hot encode the category
	return X,Y

def set_tensorflow_config():
	import tensorflow as tf
	from keras.backend.tensorflow_backend import set_session
	config = tf.ConfigProto()
	#config.gpu_options.per_process_gpu_memory_fraction = 0.9 
	config.gpu_options.visible_device_list = ",".join(str(i) for i in range(nb_gpus))
	set_session(tf.Session(config=config))

def load_pretrained_model():
	if backend()=='mxnet':
		model = 'resnet50-mxnet011rc3-0200.h5'
	elif backend()=='tensorflow':
		set_tensorflow_config()
		model = 'resnet50-tensorflow12-0200.h5'
	else:
		print 'No model is available, sorry'
		exit()
	return load_model(model)

def freeze_layers(model):
	for layer in model.layers[0:-1]:
		layer.trainable = False
	return model

if __name__ == '__main__':
	# Load training and test samples for CIFAR-10
	(x_train, y_train), (x_test, y_test) = cifar10.load_data()
	# Prepare subsets for training and test
	X, Y = prepare_dataset(x_train, y_train)
	X_test, Y_test = prepare_dataset(x_test, y_test)

	# Load pre-trained model
	model = load_pretrained_model()
	# Freeze all layers but the last one
	model = freeze_layers(model)
	# Prepare model for retraining
	#opt = SGD(lr=1e-3, decay=1e-6, momentum=0.9, nesterov=True)
	opt = 'adagrad'
	model.compile(loss='categorical_crossentropy', optimizer=opt, metrics=['accuracy'], context=get_gpus(nb_gpus))

	# Evaluate base model accuracy on the test subset
	scores = model.evaluate(X_test, Y_test, batch_size=batch, verbose=1)
	print scores
	# Retrain model on the training subset
	model.fit(X, Y, batch_size=batch, shuffle=True, nb_epoch=nb_epochs, validation_data=(X_test, Y_test), verbose=1)
	# Evaluate retrained model accuracy on the test subset
	scores = model.evaluate(X_test, Y_test, batch_size=batch, verbose=1)
	print scores

