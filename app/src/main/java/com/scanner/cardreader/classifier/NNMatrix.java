package com.scanner.cardreader.classifier;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by aviisekh on 7/29/16.
 */
public class NNMatrix {
    private final int M;             // number of rows
    private final int N;             // number of columns
    private final double[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public NNMatrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new double[M][N];
    }

    // create matrix based on 2d array
    public NNMatrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                this.data[i][j] = data[i][j];
    }

    // copy constructor
    private NNMatrix(NNMatrix A) {
        this(A.data);
    }


    // return C = A + B
    public NNMatrix plus(NNMatrix B) {
        NNMatrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        NNMatrix C = new NNMatrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j] + B.data[i][j];
        return C;
    }


    // return C = A * B
    public NNMatrix times(NNMatrix B) {
        NNMatrix A = this;
        if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");
        NNMatrix C = new NNMatrix(A.M, B.N);
        for (int i = 0; i < C.M; i++)
            for (int j = 0; j < C.N; j++)
                for (int k = 0; k < A.N; k++)
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
        return C;
    }

    public NNMatrix sigmoid() {
        NNMatrix A = new NNMatrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                //this.data[i][j] =1.0/(1+Math.exp(-this.data[i][j]));
                A.data[i][j] = 1.0 / (1 + Math.exp(-this.data[i][j]));
        return A;
    }


    // print matrix to standard output
    public void showOutputArray() {
        Log.d("OutputMatrix", Arrays.deepToString(this.data));
    }

    public int filterOutput() {
        int largestIndex = 0;
        int second_largestIndex = -1;
        double largest = this.data[largestIndex][0];
        double second_largest = -1;

        int i;
        for (i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {

                if (this.data[i][j] > largest) {
                    second_largestIndex = largestIndex;
                    largestIndex = i;
                    second_largest = largest;
                    largest = this.data[largestIndex][j];

                } else if (this.data[i][j] > second_largest && this.data[i][j] != largest) {
                    second_largestIndex = i;
                    second_largest = this.data[i][j];

                }


            }
        }
        Log.d("Output", largestIndex + " has probability " + Double.toString(largest));

        if (largest > 0.2)
            return largestIndex;
        else return -1;

    }


}


