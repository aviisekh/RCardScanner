package com.scanner.cardreader.classifier;

/**
 * Created by aviisekh on 7/29/16.
 */
public class NeuralNetwork {
    NNMatrix weights_at_layer2,weights_at_layer3;
    NNMatrix biases_at_layer2,biases_at_layer3;

    public NeuralNetwork(double [][]biases_at_layer2,double[][] biases_at_layer3,double[][] weight_at_layer2,double[][] weight_at_layer3)
    {


        this.weights_at_layer2 = new NNMatrix(weight_at_layer2);
        this.weights_at_layer3 = new NNMatrix(weight_at_layer3);
        this.biases_at_layer2 = new NNMatrix(biases_at_layer2);
        this.biases_at_layer3 = new NNMatrix(biases_at_layer3);
    }

    public NNMatrix FeedForward(NNMatrix a)
    {
        a = ((this.weights_at_layer2.times(a)).plus(this.biases_at_layer2)).sigmoid();
        a= ((this.weights_at_layer3.times(a)).plus(this.biases_at_layer3)).sigmoid();

        return a;
    }

}
