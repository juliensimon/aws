
import numpy as np
import cv2

def splitRGBImage(filename):
	img = cv2.imread(filename)
	red = np.copy(img)
	red[:,:,0].fill(0)
	red[:,:,1].fill(0)
	red = cv2.resize(red, (224, 224,))
	cv2.imwrite("red_"+filename, red)
	green = np.copy(img)
	green[:,:,0].fill(0)
	green[:,:,2].fill(0)
	green = cv2.resize(green, (224, 224,))
	cv2.imwrite("green_"+filename, green)
	blue = np.copy(img)
	blue[:,:,1].fill(0)
	blue[:,:,2].fill(0)
	blue = cv2.resize(blue, (224, 224,))
	cv2.imwrite("blue_"+filename, blue)

