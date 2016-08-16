package com.scanner.cardreader.classifier;

/**
 * Created by aviisekh on 7/29/16.
 */
public class NeuralNetwork {
    private NNMatrix weights_at_layer2,weights_at_layer3;
    private NNMatrix biases_at_layer2,biases_at_layer3;

    public NeuralNetwork(NNMatrix biases_at_layer2,NNMatrix biases_at_layer3,NNMatrix weight_at_layer2,NNMatrix weight_at_layer3)
    {
        this.weights_at_layer2 = weight_at_layer2;
        this.weights_at_layer3 = weight_at_layer3;
        this.biases_at_layer2 = biases_at_layer2;
        this.biases_at_layer3 = biases_at_layer3;
    }

    public NNMatrix FeedForward(NNMatrix a)
    {
        a = ((this.weights_at_layer2.times(a)).plus(this.biases_at_layer2)).sigmoid();
        a= ((this.weights_at_layer3.times(a)).plus(this.biases_at_layer3)).sigmoid();

        return a;
    }

}
