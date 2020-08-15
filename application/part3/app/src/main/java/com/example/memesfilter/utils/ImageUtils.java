package com.example.memesfilter.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtils {
    public static Bitmap loadFromGallery(String path) {
        try {
            FileInputStream in = new FileInputStream(path);
            BufferedInputStream buf = new BufferedInputStream(in);
            byte[] bMapArray = new byte[buf.available()];
            buf.read(bMapArray);
            return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    public static Bitmap downloadFromUrl(String inputUrl) {
        try {
            URL url = new URL(inputUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.i("Downloaded", inputUrl);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

}
