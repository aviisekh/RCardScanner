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
        for (int u = 0; u < len - 1; u++) {
            swapped = false;
            for (int v = 0; v < len - 1 - u; v++) {
                System.out.println("Items compared " + componentArray[v] + ", " + componentArray[v + 1]);

                if (MinXY(componentArray[v])[0] > MinXY(componentArray[v + 1])[0]) {
                    temp = componentArray[v];
                    componentArray[v] = componentArray[v + 1];
                    componentArray[v + 1] = temp;
                    swapped = true;
                } else {
                }
            }
            if (!swapped) {
                break;
            }
        }
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


    public static ArrayList<Bitmap> CreateImageFromComponents(int[][][] componentArray) {

        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        componentSort(componentArray);
        for(int i = 0; i < componentArray.length; i++){
            System.out.println(MinXY(componentArray[i])[0]);
        }
        for (int component = 0; component < componentArray.length; component++) {
            int minX = MinXY(componentArray[component])[0];
            int minY = MinXY(componentArray[component])[1];
            int maxX = MaxXY(componentArray[component])[0];
            int maxY = MaxXY(componentArray[component])[1];
            int componentHeight = maxY - minY + 1;
            int componentWidth = maxX - minX + 1;
            Bitmap componentSegment = Bitmap.createBitmap(componentWidth, componentHeight, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(componentSegment);
            c.drawColor(Color.WHITE);
            c.drawBitmap(componentSegment, 0, 0, null);
            for (int pixel = 0; pixel < componentArray[component].length; pixel++) {
                int x = componentArray[component][pixel][0] - minX;
                int y = componentArray[component][pixel][1] - minY;
                componentSegment.setPixel(x, y, Color.BLACK);
            }
            bitmapArrayList.add(componentSegment);
        }


        System.out.println(bitmapArrayList.size());



        return bitmapArrayList;
    }


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