package com.scanner.cardreader;

/**
 * Created by aviisekh on 7/29/16.
 */
public class NeuralNetwork {
    MeroMatrix weights_at_layer2,weights_at_layer3;
    MeroMatrix biases_at_layer2,biases_at_layer3;

    public NeuralNetwork()
    {


        this.weights_at_layer2 = new MeroMatrix(WeightReader.weight_at_layer2);
        this.weights_at_layer3 = new MeroMatrix(WeightReader.weight_at_layer3);
        this.biases_at_layer2 = new MeroMatrix(WeightReader.biases_at_layer2);
        this.biases_at_layer3 = new MeroMatrix(WeightReader.biases_at_layer3);
    }

    public MeroMatrix FeedForward(MeroMatrix a)
    {
        a = ((this.weights_at_layer2.times(a)).plus(this.biases_at_layer2)).sigmoid();
        a= ((this.weights_at_layer3.times(a)).plus(this.biases_at_layer3)).sigmoid();

        return a;
    }

}
