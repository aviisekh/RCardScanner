import numpy as np

def load_data():	#load raw data from trainingset files
	td = np.loadtxt("trainsets.txt",dtype=float, comments='#', delimiter=",")
	return td

def vectorized_result(j): #returns 10-dimensional vectorized output
    e = np.zeros((10, 1))
    e[j] = 1.0
    return e


def load_data_wrapper(): #produces the vectorized data
	td = load_data()
	input_data = []
	output_data = []

	for each_row in td:
		input_data.append(each_row[0:-1])
		output_data.append(each_row[-1:])

	training_inputs = [np.reshape(x, (256, 1)) for x in input_data]
	training_results = [vectorized_result(int(i)) for i in output_data]
	
	training_data = zip(training_inputs, training_results)
	return training_data
