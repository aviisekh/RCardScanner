package com.scanner.cardreader;

/**
 * Created by aviisekh on 7/29/16.
 */
public class NeuralNetwork {
    private int[] sizes ;
    private int num_layers;
    MeroMatrix weights_at_layer2,weights_at_layer3;
    MeroMatrix biases_at_layer2,biases_at_layer3;

    public NeuralNetwork()
    {
        this.sizes = new int[]{2, 4};
        this.num_layers = sizes.length;

        this.weights_at_layer2 = new MeroMatrix(new double[][]{{-3.29264863,0.53933425},{1.1062452 , 1.52553444},{ 0.32705708, -1.93363139}});
        this.weights_at_layer3 = new MeroMatrix(new double[][]{{-0.41548526, -0.16566116, -0.44771736}});
        this.biases_at_layer2 = new MeroMatrix(new double[][]{ {0.00186898},{-0.3278478},{-0.47481281}});
        this.biases_at_layer3 = new MeroMatrix(new double[][]{{1.19160034}});
    }

    public MeroMatrix FeedForward(MeroMatrix a)
    {
        //MeroMatrix z = input;

        a = ((this.weights_at_layer2.times(a)).plus(this.biases_at_layer2)).sigmoid();
        a= ((this.weights_at_layer3.times(a)).plus(this.biases_at_layer3)).sigmoid();

        return a;
    }





   // public double[][]
}
