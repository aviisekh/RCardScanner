import os,math
import numpy as np
import cv2
home=os.getcwd()+"/trainingImages"
path = []
for paths,subdirs,content_files in os.walk(home):
	path.append(paths)

path.remove(path[0])
path.sort()


def cropImage(img):	#Crops Image to the boundary if needed
	rows = img.shape[0]
	columns = img.shape[1] 
	tlx=tly=bry=brx=0
	flag = 0

	for x in range(1,rows):
		for y in range(columns):
			if img[x,y]==0:
				flag = 1
				tly = x
				break

		if flag == 1:
			flag = 0
			break

	for x in range(rows-1,0,-1):
		for y in range(columns):
			if img[x,y]==0:
				flag =1
				bry=x
				break

		if flag == 1:
			flag = 0
			break


	for y in range(1,columns):
		for x in range(rows):
			if img[x,y]==0:
				flag =1
				tlx = y
				break

		if flag ==1:
			flag = 0
			break

	for y in range(columns-1,0,-1):
		for x in range(rows):
			if img[x,y]==0:
				flag = 1
				brx =y
				break

		if flag==1:
			flag = 0
			break

	croppedImage = img[tly:bry,tlx:brx]
	return croppedImage

def scaleImage(img):		#Scales to 16x16 image if needed
	scaledImage = np.zeros((16,16), dtype=np.uint8)
	rows = img.shape[0]
	columns = img.shape[1] 
	for x in range(16):
		for y in range(16):
			yd =math.ceil(y*columns/16)
			xd = math.ceil(x*rows/16)

			scaledImage[x,y]= img[int(xd),int(yd)]
	return scaledImage


def generateBinaryData():
	"""
	Crawls through the trainingset directory and generates the training sets
	and writes it to trainingsets.txt
	trainingsets contains 1 for white pixel and 0 for black pixel and output 
	is appended in the last of training set
	"""
	for single_directory in path:
		folder = single_directory
		#print folder
		for paths,subdirs,content_files in os.walk(folder):
			for each_file in content_files:
				file_path = folder+"/"+each_file
				img = cv2.imread(file_path,cv2.IMREAD_GRAYSCALE)
				ret,output = cv2.threshold(img,127,255,cv2.THRESH_BINARY)

				width, height = img.shape

				if width != 16 or height != 16:
					img_cropped = cropImage(output)
					output = scaleImage(img_cropped)
				
				# #th2 = cv2.adaptiveThreshold(img,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY,11,2)
				# #scaled = cv2.resize(img_cropped,(16,16),interpolation = cv2.INTER_AREA)
				# #ret,th2 = cv2.threshold(scaled,127,255,cv2.THRESH_BINARY)
				
				#cv2.imshow("croppedImage",output)
				#cv2.waitKey(0)
				#cv2.destroyAllWindows()
				f=open("trainsets.txt","a")
				for x in output :
					for y in x:
				 		if y == 255:
				 			y = 1
				 		f.write(str(y)+",") 

				f.write(folder[-1:]+"\n")
				f.close()
				print ".",


