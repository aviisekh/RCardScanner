package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by mandy on 7/12/16.
 */
public class ComponentImages {
//    private static String TAG = "ComponentImagesClass";

private static void componentSort(int[][][] componentArray) {
    int[][] temp;

    boolean swapped;
    int len = componentArray.length;
    for(int u = 0; u < len - 1; u++){
        swapped = false;
        for(int v = 0; v < len - 1 - u ; v++){
            System.out.println("Items compared " + componentArray[v] + ", " + componentArray[v + 1]);

            if(MinXY(componentArray[v])[0] > MinXY(componentArray[v+1])[0]){
                temp = componentArray[v];
                componentArray[v] = componentArray[v+1];
                componentArray[v + 1] = temp;

                swapped = true;

                System.out.println("swapped"  + componentArray[v] + ", " + componentArray[v + 1]);


            }
            else{
                System.out.println("not swapped");
            }


        }

        if(!swapped){
            break;
        }



    }


    for(int i = 0; i < componentArray.length; i++){
        System.out.println(MinXY(componentArray[i])[0]);
    }





//    for (int i = 0; i < componentArray.length; i++) {
//        System.out.println(MinXY(componentArray[i])[0]);
//    }
//    int minX = (MinXY(componentArray[0])[0]);
//    for (int i = 0; i < componentArray.length - 1; i++)
//    {
//        int componentX1 = MinXY(componentArray[i + 1])[0];
//        if (minX > componentX1)
//        {
//            temp = componentArray[i];
//            componentArray[i] = componentArray[i + 1];
//            componentArray[i + 1] = temp;
//            minX = componentArray[i][0][0];
//
//        }
//
//
//
//
//}
//    for (int i = 0; i < componentArray.length; i++) {
//        System.out.println(MinXY(componentArray[i])[0]);
//    }
////        int temp[][];
//        for(int i = 0; i < componentArray.length; i++){
//            for(int j = 1; j < (componentArray.length - i); j++){
//
//
//
//        }

//
    }
//    return componentArray;



    public static void sort(int[] array) {
        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i <= array.length - 2; i++) {
                if (array[i] > array[i + 1]) {
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
            swapped = false;
            for (int i = array.length - 2; i >= 0; i--) {
                if (array[i] > array[i + 1]) {
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }

    private static int[] MaxXY(int[][] componentPixels) {
        int maxXY[] = new int[2];
        int maxX = componentPixels[0][0];
        int maxY = componentPixels[0][1];


        for (int i = 0; i < componentPixels.length; i++) {

//            maxX = (maxX < componentPixels[i][0]) ? componentPixels[i][0] : maxX;
            maxX = Math.max(maxX, componentPixels[i][0]);
//            maxX = (maxX < componentPixels[i][0]) ? componentPixels[i][0] : maxX;
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
//            minX = (minX > componentPixels[i][0]) ? componentPixels[i][0] : minX;
            minX = Math.min(minX, componentPixels[i][0]);

//            minY = (minY > componentPixels[i][1]) ? componentPixels[i][1] : minY;
            minY = Math.min(minY, componentPixels[i][1]);

        }

        minXY[0] = minX;
        minXY[1] = minY;

        return minXY;

    }


    public static ArrayList<Bitmap> CreateImageFromComponents(int[][][] componentArray) {

        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
//componentArray[0] = componentArray[3];
//        Log.d("XCoordinate", String.valueOf(MinXY(componentArray[0])[0]));
//        for (int[][] ci:
//            componentArray   ) {
//            System.out.println(MinXY(ci)[0]);
//        }

        ArrayList<Integer> toBeSorted = new ArrayList<>();
        for(int i = 0; i<componentArray.length;i++){
            toBeSorted.add(MinXY(componentArray[i])[0]);
        }
        int min = toBeSorted.get(0);
        for(int i : toBeSorted){
            min = min < i ? min : i;
        }
        componentSort(componentArray);
//        for (int[][] ci:
//                componentArray   ) {
//            System.out.println(MinXY(ci)[0]);
//        }

//        Log.d("XCoordinate", String.valueOf(MinXY(componentArray[0])[0]));
//        for (int i = 0; i < componentArray.length; i++) {
//            System.out.println(MinXY(componentArray[i])[0]);
//        }
        for (int component = 0; component < componentArray.length; component++) {
            int minX = MinXY(componentArray[component])[0];
            int minY = MinXY(componentArray[component])[1];
            int maxX = MaxXY(componentArray[component])[0];
            int maxY = MaxXY(componentArray[component])[1];
            int componentHeight = maxY - minY + 1;
            int componentWidth = maxX - minX + 1;

//            System.out.println("component" + component + "=" + componentHeight);
//            Log.d(TAG, String.valueOf(componentHeight));
//            Log.d(TAG, String.valueOf(componentWidth));
            Bitmap componentSegment = Bitmap.createBitmap(componentWidth, componentHeight, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(componentSegment);
            c.drawColor(Color.WHITE);
            c.drawBitmap(componentSegment, 0, 0, null);
            for (int pixel = 0; pixel < componentArray[component].length; pixel++) {
                int x = componentArray[component][pixel][0] - minX;
                int y = componentArray[component][pixel][1] - minY;
//                System.out.println(x + "," + y);
                componentSegment.setPixel(x, y, Color.BLACK);
            }
            bitmapArrayList.add(componentSegment);
//                    Bitmap resizedImage = Bitmap.createBitmap(img, 0,0,w+2,h+2);
        }
//        for(int i = 0; i<toBeSorted.length; i++){
//            System.out.println(toBeSorted[i]);
//        }
//        sort(toBeSorted);
//        for(int i = 0; i<toBeSorted.length; i++){
//            System.out.println(toBeSorted[i]);
//        }


//              Bitmap resizedImage = Bitmap.createBitmap(w+2, h+2, Bitmap.Config.RGB_565);
//                Canvas g = new Canvas();
//                g.setBitmap(blankBitmapImage);
//                g.drawColor(Color.WHITE);
//                g.drawBitmap(,1,1,null);

//                for (int count = 0; count < componentArray[component][pixel].length; count++)
//                {
//                    bitmapImage.setPixel(componentArray[component][pixel][0] - minX, componentArray[component][pixel][1] - minY, Color.BLACK);
//                }
//                bitmapArrayList.add(bitmapImage);


        System.out.println(bitmapArrayList.size());


//
//        for(int i = 0; i<componentArray[0].length; i++){
//            System.out.println(componentArray[0][i][0]);
//            System.out.println(componentArray[0][i][1]);
//        }
//
//
//        int maxX=0;
//        int minX=0;
//        int maxY=0;
//        int minY=0;
//


        //        int maxX=Integer.MIN_VALUE;

        //        int maxY=Integer.MIN_VALUE;

        //        int minX=Integer.MAX_VALUE;

        //        int minY=Integer.MAX_VALUE;

//        for (int component = componentArray.length-1 ; component>=0; component--)
//        {
//            int max[] = new int[2];
//            int min[] = new int[2];
//
//
//            for(int pixel = componentArray[component].length -1; pixel>=0; pixel--)
//
//            {
//
//
//                max = MaxXY(componentArray[component]);
//                min = MinXY(componentArray[component]);
////                minX = componentArray[component][pixel][0];
////                minY = componentArray[component][pixel][1];
//
//                 maxX = (componentArray[component][pixel][0] > maxX) ? componentArray[component][pixel][0] : maxX;
//                 maxY = (componentArray[component][pixel][1] > maxY) ? componentArray[component][pixel][1] : maxY;
//                 minX = (componentArray[component][pixel][0] < minX) ? componentArray[component][pixel][1] : minX;
//                 minY = (componentArray[component][pixel][0] < minY) ? componentArray[component][pixel][1] : minY;
//                      }
//            System.out.println("Max X, Max Y" + maxX + "," + maxY);
//            System.out.println("Min X, Min Y" + minX + "," + minY);
//


        return bitmapArrayList;
    }


//
//        Log.d("components", String.valueOf(componentArray.length));
//                        Log.d(TAG, String.valueOf(componentArray[0].length));
//                        Log.d(TAG, String.valueOf(componentArray[0][0].length));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][0][0]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][0][1]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][1][0]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][1][1]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][2][0]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][2][1]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][3][0]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][3][1]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][4][0]));
////                        Log.d(TAG, String.valueOf(ComponentArray[0][5][1]));
//
//
////        for (int[][] i : componentArray)
////        {
////            int maxX = 0; int maxY =0;
////
////            for (int j[] : i)
////            {
////                Log.d(TAG, String.valueOf(j[0]));
////
////
////            }
////
////        }
//
//        //                        for(int i = 0; i < ComponentArray.length; i++) {
////
////                        for(int j = 0; j<ComponentArray[i].length; j++){
////                            for(int k = 0; k<ComponentArray[i][j].length; k++){
////                                Log.d("X,y", String.valueOf(ComponentArray[i][j][k]));
////
////                            }
////                        }
////                        }
//
//
//
//
//

//        return new Bitmap[0];
//    }
}