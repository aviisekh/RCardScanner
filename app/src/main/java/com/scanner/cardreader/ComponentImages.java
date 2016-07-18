package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mandy on 7/12/16.
 */
public class ComponentImages {
    private static String TAG = "ComponentImagesClass";


    private static int[] MaxXY(int[][] componentPixels) {
        int maxXY[] = new int[2];
        int maxX = componentPixels[0][0];
        int maxY = componentPixels[0][1];


        for (int i = 0; i < componentPixels.length; i++) {

            maxX = (maxX < componentPixels[i][0]) ? componentPixels[i][0] : maxX;
            maxY = (maxY < componentPixels[i][1]) ? componentPixels[i][1] : maxY;
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
            minX = (minX > componentPixels[i][0]) ? componentPixels[i][0] : minX;
            minY = (minY > componentPixels[i][1]) ? componentPixels[i][1] : minY;
        }

        minXY[0] = minX;
        minXY[1] = minY;

        return minXY;

    }


    public static ArrayList<Bitmap> CreateImageFromComponents(int[][][] componentArray) {

        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

        for (int component = 0; component < componentArray.length; component++) {
            int minX = MinXY(componentArray[component])[0];
            int minY = MinXY(componentArray[component])[1];
            int maxX = MaxXY(componentArray[component])[0];
            int maxY = MaxXY(componentArray[component])[1];
            int componentHeight = maxY - minY + 1;
            int componentWidth = maxX - minX + 1;
//            Log.d(TAG, String.valueOf(componentHeight));
//            Log.d(TAG, String.valueOf(componentWidth));


            Bitmap componentSegment = Bitmap.createBitmap(componentWidth, componentHeight, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(componentSegment);
            c.drawColor(Color.WHITE);
            c.drawBitmap(componentSegment, 0, 0, null);
            for (int pixel = 0; pixel < componentArray[component].length; pixel++) {
                int x = componentArray[component][pixel][0] - minX;
                int y = componentArray[component][pixel][1] - minY;

                System.out.println(x + "," + y);
                componentSegment.setPixel(x, y, Color.BLACK);
            }
            bitmapArrayList.add(componentSegment);
//                    Bitmap resizedImage = Bitmap.createBitmap(img, 0,0,w+2,h+2);
        }


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
