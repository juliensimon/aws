
import struct
import numpy as np
import cv2

labelfile = open("train-labels-idx1-ubyte")
# Read packed structure - big-endian, 2 integers: a magic number and the number of labels
magic, num = struct.unpack(">II", labelfile.read(8))
labelarray = np.fromstring(labelfile.read(), dtype=np.int8)
print labelarray.shape
# Print first labels
print labelarray[0:10]

imagefile = open("train-images-idx3-ubyte")
# Read packed structure - big-endian, 4 integers: a magic number, the number of images, rows and columns
magic, num, rows, cols = struct.unpack(">IIII", imagefile.read(16))
imagearray = np.fromstring(imagefile.read(), dtype=np.uint8)
print imagearray.shape
imagearray = imagearray.reshape(num, rows, cols)
print imagearray.shape
for i in range(0,10):
	img = imagearray[i]
	imgname = "img"+(str)(i)+".png"
	cv2.imwrite(imgname, img)


