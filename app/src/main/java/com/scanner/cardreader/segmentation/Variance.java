package com.scanner.cardreader.segmentation;

import java.util.ArrayList;


/**
 * Created by mandy on 8/10/16.
 */
public class Variance {

    public static int CalculateMean(ArrayList<Integer> minY) {
        int len = minY.size();
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum += minY.get(i);
        }

        return sum / len;
    }

    static int CalculateVariance(ArrayList<Integer> minY) {
        int valuesMean = CalculateMean(minY);
        int len = minY.size();
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum += (minY.get(i) - valuesMean) * (minY.get(i) - valuesMean);
        }

        return sum / len;


    }

    private static int MinY(int[][] componentPixels) {

        int minY = componentPixels[0][1];
        for (int i = 0; i < componentPixels.length; i++) {
            minY = Math.min(minY, componentPixels[i][1]);
        }
        return minY;
    }


    public static ArrayList<int[][]> CheckVarianceInClusters(ArrayList<ArrayList<int[][]>> clusterArrayList) {
        ArrayList<Integer> minY;
        minY = new ArrayList<>();
        int[] variance = new int[clusterArrayList.size()];


        for (int i = 0; i < clusterArrayList.size(); i++) {
            for (int j = 0; j < clusterArrayList.get(i).size(); j++) {
                minY.add(MinY(clusterArrayList.get(i).get(j)));
            }
            variance[i] = CalculateVariance(minY);
        }


        int index = 0;
        int minVariance = variance[0];
        for (int i = 0; i < variance.length; i++) {
            if (minVariance > variance[i]) {
                index = i;
            }
        }
        return clusterArrayList.get(index);
    }
}
