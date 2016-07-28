import numpy as np
import cv2
import os 
from processImage import cropImage,scaleImage
import json

json_data=open("weights.json").read() #Loads the required weights from json file 
data = json.loads(json_data)

weights=[np.reshape(data["layer_1_weight"],(len(data["layer_1_weight"]),len(data["layer_1_weight"][0]))),np.reshape(data["layer_2_weight"],(len(data["layer_2_weight"]),len(data["layer_2_weight"][0])))]
biases=[np.reshape(data["layer_1_bias"],(len(data["layer_1_bias"]),len(data["layer_1_bias"][0]))),np.reshape(data["layer_2_bias"],(len(data["layer_2_bias"]),len(data["layer_2_bias"][0])))]

def feedforward(a):	#produces the output
	for b, w in zip(biases, weights):
		a = sigmoid(np.dot(w, a)+b)
	return a

def sigmoid(z):
    """The sigmoid function."""
    return 1.0/(1.0+np.exp(-z))	


home=os.getcwd()+"/testImages"
for paths,subdirs,content_files in os.walk(home):
	files = content_files


#td = train.data_extractor.load_data_wrapper()
#net = train.Network([256, 20, 10])
#net.SGD(td, 1000, 10, 3.0, None)

for img in files:
	#print img
	file_path = home + "/"+img
	img = cv2.imread(file_path,cv2.IMREAD_GRAYSCALE)
	ret,output = cv2.threshold(img,127,255,cv2.THRESH_BINARY)

	width, height = img.shape

	if width != 16 or height != 16:
		img_cropped = cropImage(output)
		output = scaleImage(img_cropped)

	# cv2.imshow("outputimage",output)
	# cv2.waitKey(0)
	# cv2.destroyAllWindows()
	output_array=[]
	for x in output :
		for y in x:
			if y == 255:
				y = 1
			output_array.append(y)
			


	#test = np.loadtxt("test.txt",dtype=float, comments='#', delimiter=",")
	test_data = np.reshape(output_array, (256, 1))

	ans = feedforward(test_data)
	#print np.around(ans)
	#print ans

	print np.where(np.around(ans) == 1)[0][0]


