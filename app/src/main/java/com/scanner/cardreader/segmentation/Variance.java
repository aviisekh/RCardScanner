package com.scanner.cardreader.segmentation;

import android.util.Log;

import java.util.ArrayList;


/**
 * Created by mandy on 8/10/16.
 */
public class Variance {

    private static double CalculateMean(ArrayList<Integer> minY) {
        int len = minY.size();
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += minY.get(i);
        }

        return sum / len;
    }

    private static double CalculateVariance(ArrayList<Integer> minY) {
        double valuesMean = CalculateMean(minY);
        int len = minY.size();
        double sum = 0;
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


    private static void VarianceSorting(ArrayList<ArrayList<int[][]>> clusterArrayList, int len) {
        System.out.println("Arraylist sorting by variance");
        ArrayList<int[][]> temp;
        boolean swapped;
        for (int u = 0; u < len - 1; u++) {
            swapped = false;
            for (int v = 0; v < len - 1 - u; v++) {
                ArrayList<Integer> minY1, minY2;
                minY1 = new ArrayList<>();
                minY2 = new ArrayList<>();
                for (int c1 = 0; c1 < clusterArrayList.get(v).size(); c1++)
                {
                    minY1.add(MinY(clusterArrayList.get(v).get(c1)));
                }
                for (int c2 = 0; c2 < clusterArrayList.get(v + 1).size(); c2++) {
                    minY2.add(MinY(clusterArrayList.get(v + 1).get(c2)));
                }
                double v1 = CalculateVariance(minY1);
                double v2 = CalculateVariance(minY2);
                if ((v1) > (v2)) {
                    temp = clusterArrayList.get(v);
                    clusterArrayList.set(v, clusterArrayList.get(v + 1));
                    clusterArrayList.set(v + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

    }

    public static ArrayList<int[][]> CheckVarianceInClusters(ArrayList<ArrayList<int[][]>> clusterArrayList) {
        VarianceSorting(clusterArrayList, clusterArrayList.size());
        ArrayList<Integer> minY = new ArrayList<>();
        double[] variance = new double[clusterArrayList.size()];
                for (int i = 0; i < clusterArrayList.size(); i++) {
            for (int j = 0; j < clusterArrayList.get(i).size(); j++) {
                minY.add(MinY(clusterArrayList.get(i).get(j)));
            }
            variance[i] = CalculateVariance(minY);
        }
        int count = 0;
        for(double i : variance)
        {
            Log.d("variance", "" + ++count + ":"+ i);
        }

        for (int i = 0; i < clusterArrayList.size(); i++) {
            if (clusterArrayList.get(i).size() >= 12 && clusterArrayList.get(i).size() <= 18) {

                return clusterArrayList.get(i);
            }
        }
        return clusterArrayList.get(0);
    }
}






