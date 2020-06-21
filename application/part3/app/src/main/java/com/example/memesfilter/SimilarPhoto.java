package com.example.memesfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SimilarPhoto {

    private static final String TAG = SimilarPhoto.class.getSimpleName();
    private static final int size1 = 64;
    private static final int size2 = 16;

    public static final int MAX_SIMILAR_DIFF = (size2 * size2) / 4;

    public static String getFingerPrint(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, size1, size1, false);
        bitmap = replaceBlackWhiteWithAverage(bitmap);

        bitmap = Bitmap.createScaledBitmap(bitmap, size2, size2, false);
        double[][] grayPixels = getGrayPixels(bitmap);
        double grayAvg = getGrayAvg(grayPixels);
        return getFingerPrint(grayPixels, grayAvg);
    }

    private static String getFingerPrint(double[][] pixels, double avg) {
        int width = pixels[0].length;
        int height = pixels.length;

        byte[] bytes = new byte[height * width];

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] >= avg) {
                    bytes[i * height + j] = 1;
                    stringBuilder.append("1");
                } else {
                    bytes[i * height + j] = 0;
                    stringBuilder.append("0");
                }
            }
        }

        Log.d(TAG, "getFingerPrint: " + stringBuilder.toString());

        return stringBuilder.toString();
    }

    private static Bitmap replaceBlackWhiteWithAverage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // compute average pixel
        int avgPixel = computeAvergePixel(bitmap);

        Bitmap output = Bitmap.createBitmap(bitmap);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = bitmap.getPixel(i, j);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = (pixel) & 255;
                if (red == green && green == blue) {
                    output.setPixel(i, j, avgPixel);
                }
            }
        }

        return output;
    }

    private static int computeAvergePixel(Bitmap bitmap) {
        if (null == bitmap) return Color.TRANSPARENT;

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int alphaBucket = 0;

        boolean hasAlpha = bitmap.hasAlpha();
        int size = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[size];
        int pixelCount = 0;
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y = 0, h = bitmap.getHeight(); y < h; y++) {
            for (int x = 0, w = bitmap.getWidth(); x < w; x++) {
                int color = pixels[x + y * w]; // x + y * width
                int red = (color >> 16) & 0xFF; // Color.red
                int green = (color >> 8) & 0xFF; // Color.greed
                int blue = (color & 0xFF); // Color.blue
                if (red != green || green != blue) {
                    redBucket += red;
                    greenBucket += green;
                    blueBucket += blue;
                    if (hasAlpha) alphaBucket += (color >>> 24); // Color.alpha
                    pixelCount += 1;
                }
            }
        }

        if (pixelCount <= 0) {
            return Color.WHITE;

        } else {
            return Color.argb(
                    (hasAlpha) ? (alphaBucket / pixelCount) : 255,
                    redBucket / pixelCount,
                    greenBucket / pixelCount,
                    blueBucket / pixelCount);
        }
    }

    private static double getGrayAvg(double[][] pixels) {
        int width = pixels[0].length;
        int height = pixels.length;
        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count += pixels[i][j];
            }
        }
        return count / (width * height);
    }


    private static double[][] getGrayPixels(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double[][] pixels = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = computeGrayValue(bitmap.getPixel(i, j));

            }
        }
        return pixels;
    }

    private static double computeGrayValue(int pixel) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = (pixel) & 255;
        return 0.3 * red + 0.59 * green + 0.11 * blue;
    }

    public static int hamDist(String finger1, String finger2) {
        if (finger1.length() != finger2.length())
            return Integer.MAX_VALUE;

        int length = finger1.length();
        int count = 0;
        for (int i = 0; i < length; i++) {
            if (finger1.charAt(i) != finger2.charAt(i)) {
                count++;
            }
        }
        return count;
    }

}
