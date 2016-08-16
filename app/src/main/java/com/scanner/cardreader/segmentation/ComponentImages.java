package com.scanner.cardreader.segmentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
//import android.util.Log;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by mandy on 7/12/16.
 */
public class ComponentImages {
    Context context;
    public ComponentImages(Context context) {
        this.context = context;

    }


    private void SortByX(ArrayList<int[][]> componentArrayList) {
        int[][] temp;
        boolean swapped;

        int len = componentArrayList.size();
        for (int u = 0; u < len - 1; u++) {
            swapped = false;
            for (int v = 0; v < len - 1 - u; v++) {
                if (MinXY(componentArrayList.get(v))[0] > MinXY(componentArrayList.get(v + 1))[0]) {
                    temp = componentArrayList.get(v);
                    componentArrayList.set(v, componentArrayList.get(v + 1));
                    componentArrayList.set(v + 1, temp);
                    swapped = true;
                }
            }
            if(!swapped) break;
            }
        }



    public int ComponentHeight(int[][] componentArray)
    {
        int minY = MinXY(componentArray)[1];
        int maxY = MaxXY(componentArray)[1];
        return maxY-minY+1;
    }


    public void HeightSorting(int[][][] componentArray) {

        int[][] temp;
        boolean swapped;
        int len = componentArray.length;
        for (int u = 0; u < len - 1; u++) {
            swapped = false;
            for (int v = 0; v < len - 1 - u; v++) {

                int h1 = ComponentHeight(componentArray[v]);
                int h2 = ComponentHeight(componentArray[v+1]);

                if ((h1) > (h2)) {
                    temp = componentArray[v];
                    componentArray[v] = componentArray[v + 1];
                    componentArray[v + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

    }

    public ArrayList<ArrayList<int[][]>> Clustering(int[][][] componentArray) {
        ArrayList<ArrayList<int[][]>> componentClusters = new ArrayList<>();
        ArrayList<int[][]> temp;
        for (int i =0; i < componentArray.length; i++)
        {
            if(componentClusters.size() == 0)
            {
                temp = new ArrayList<>();
                temp.add(componentArray[i]);
                componentClusters.add(temp);
            }
            else
            {
                ArrayList<int[][]> lastArrayList = componentClusters.get(componentClusters.size()-1);
                if(ComponentHeight(componentArray[i]) - ComponentHeight(lastArrayList.get(lastArrayList.size()-1)) <= 7)
                {
                    lastArrayList.add(componentArray[i]);
                }
                else {
                    temp = new ArrayList<>();
                    temp.add(componentArray[i]);
                    componentClusters.add(temp);
                }
            }
        }
        for(int i = 0; i<componentClusters.size();i++){
            System.out.println(componentClusters.get(i).size());
        }

        return componentClusters;

    }

    private static int[] MaxXY(int[][] componentPixels) {
        int maxXY[] = new int[2];
        int maxX = componentPixels[0][0];
        int maxY = componentPixels[0][1];
        for (int i = 0; i < componentPixels.length; i++) {
            maxX = Math.max(maxX, componentPixels[i][0]);
            maxY = Math.max(maxY, componentPixels[i][1]);
        }
        maxXY[0] = maxX;
        maxXY[1] = maxY;
        return maxXY;

    }

    private static int[] MinXY(int[][] componentPixels) {
        int minXY[] = new int[2];
        int minX = componentPixels[0][0];
        int minY = componentPixels[0][1];
        for (int i = 0; i < componentPixels.length; i++) {
            minX = Math.min(minX, componentPixels[i][0]);
            minY = Math.min(minY, componentPixels[i][1]);
        }
        minXY[0] = minX;
        minXY[1] = minY;
        return minXY;

    }

    public ArrayList<Bitmap> CreateComponentImages(int[][][] componentArray) {
        System.out.println(componentArray.length);
        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        HeightSorting(componentArray);
        ArrayList<int[][]> componentArrayList  = Variance.CheckVarianceInClusters(Clustering(componentArray));
        SortByX(componentArrayList);
//        Log.i("Components" , ""+ componentArray.length);
//        Log.i("sortedComponents" , "" + componentArrayList.size());
        for (int component = 0; component < componentArrayList.size(); component++) {
            int minX = MinXY(componentArrayList.get(component))[0];
            int minY = MinXY(componentArrayList.get(component))[1];
            int maxX = MaxXY(componentArrayList.get(component))[0];
            int maxY = MaxXY(componentArrayList.get(component))[1];
            int componentHeight = maxY - minY + 1;
            int componentWidth = maxX - minX + 1;



            Bitmap componentSegment = Bitmap.createBitmap(componentWidth, componentHeight, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(componentSegment);
            c.drawColor(Color.WHITE);
            c.drawBitmap(componentSegment, 0, 0, null);
            for (int pixel = 0; pixel < componentArrayList.get(component).length; pixel++)
            {
                int x = componentArrayList.get(component)[pixel][0] - minX;
                int y = componentArrayList.get(component)[pixel][1] - minY;
                componentSegment.setPixel(x, y, Color.BLACK);
            }
            bitmapArrayList.add(componentSegment);
//            ImageWriter imageWriter= new ImageWriter(context);

//            imageWriter.writeImage(componentSegment, true, "aftersegment", "06_segmentation");
        }

//        System.out.println(bitmapArrayList.size());
        return bitmapArrayList;
    }



}