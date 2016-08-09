import random
import numpy as np
import dataExtractor
import codecs,json

import os

class Network(object):

    def __init__(self, sizes):
        self.num_layers = len(sizes)
        self.sizes = sizes
        if not os.path.exists("weights.json"):
            self.biases = [np.random.randn(y, 1) for y in sizes[1:]]
            self.weights = [np.random.randn(y, x) for x, y in zip(sizes[:-1], sizes[1:])]

        else:
            print "Previous weights retraining"
            json_data=open("weights.json").read() #Loads the required weights from json file 
            data = json.loads(json_data)
            self.weights=[np.reshape(data["layer_1_weight"],(len(data["layer_1_weight"]),len(data["layer_1_weight"][0]))),np.reshape(data["layer_2_weight"],(len(data["layer_2_weight"]),len(data["layer_2_weight"][0])))]
            self.biases=[np.reshape(data["layer_1_bias"],(len(data["layer_1_bias"]),len(data["layer_1_bias"][0]))),np.reshape(data["layer_2_bias"],(len(data["layer_2_bias"]),len(data["layer_2_bias"][0])))]


    def feedforward(self, a):
        for b, w in zip(self.biases, self.weights):
            a = sigmoid(np.dot(w, a)+b)
        return a

    def SGD(self, training_data, iters, mini_batch_size, eta): #Implementation of stochastic gradient method
        n = len(training_data)
        for j in xrange(iters):
            random.shuffle(training_data)
            mini_batches = [
                training_data[k:k+mini_batch_size]
                for k in xrange(0, n, mini_batch_size)]
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, eta)

            print "Iteration {0} complete".format(j)

    def update_mini_batch(self, mini_batch, eta):
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]
        for x, y in mini_batch:
            delta_nabla_b, delta_nabla_w = self.backprop(x, y)
            nabla_b = [nb+dnb for nb, dnb in zip(nabla_b, delta_nabla_b)]
            nabla_w = [nw+dnw for nw, dnw in zip(nabla_w, delta_nabla_w)]
        self.weights = [w-(eta/len(mini_batch))*nw for w, nw in zip(self.weights, nabla_w)]
        self.biases = [b-(eta/len(mini_batch))*nb for b, nb in zip(self.biases, nabla_b)]

    def backprop(self, x, y):
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]
        
        # feedforward
        activation = x
        activations = [x] # list to store all the activations, layer by layer
        zs = [] # list to store all the z vectors, layer by layer
        for b, w in zip(self.biases, self.weights):
            z = np.dot(w, activation)+b
            zs.append(z)
            activation = sigmoid(z)
            activations.append(activation)
        # backward pass
        delta = self.cost_derivative(activations[-1], y) * sigmoid_prime(zs[-1])
        nabla_b[-1] = delta
        nabla_w[-1] = np.dot(delta, activations[-2].transpose())

        for l in xrange(2, self.num_layers):
            z = zs[-l]
            sp = sigmoid_prime(z)
            delta = np.dot(self.weights[-l+1].transpose(), delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, activations[-l-1].transpose())
        return (nabla_b, nabla_w)

    def cost_derivative(self, output_activations, y): #returns the cost function
        return (output_activations-y)

#### Miscellaneous functions
def sigmoid(z): 
    """The sigmoid function."""
    return 1.0/(1.0+np.exp(-z))

def sigmoid_prime(z):
    """Derivative of the sigmoid function."""
    return sigmoid(z)*(1-sigmoid(z))

def train():
    td = dataExtractor.load_data_wrapper()
    net = Network([256, 20, 10])
    net.SGD(td, 1000, 10, 3.0)
    
    file_path = "weights.json"

    dump_data={}
    i=0

    for each_layer_weight,each_layer_bias in zip(net.weights,net.biases):
        i=i+1
        #for each_row in each_layer_weight.tolist():
        dump_data["layer_"+str(i)+"_weight"] = each_layer_weight.tolist()
        dump_data["layer_"+str(i)+"_bias"] = each_layer_bias.tolist()
        json.dump(dump_data, codecs.open(file_path, 'w', encoding='utf-8'), sort_keys=True, indent=4) ### this saves the array in .json format
        
    print "\nweights.json updated"
train()

