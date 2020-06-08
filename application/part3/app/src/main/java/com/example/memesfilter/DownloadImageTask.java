package com.example.memesfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.net.ssl.HttpsURLConnection;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    private Context context;

    public DownloadImageTask(ImageView bmImage, Context context) {
        this.bmImage = bmImage;
        this.context = context;
    }

    private Bitmap loadFromGallery(String path) {
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

    private Bitmap downloadFromUrl(String url) {
        try {
            InputStream in = new java.net.URL(url.replace("https", "http")).openStream();
            return BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    protected Bitmap doInBackground(String... paths) {
        Bitmap bmap;
        String path = paths[0];
        // TODO: cache.
        ImagesCache imagesCache = ImagesCache.getInstance();
        if (imagesCache.cache.containsKey(path)) {
            bmap = imagesCache.cache.get(path);
            Log.println(Log.INFO, "Cache", path);
        }
        else {
            // if path is an http/https, download it.
            if (path.startsWith("http")) {
                bmap = downloadFromUrl(path);
            }
            // if its local:
            else {
                bmap = loadFromGallery(path);
            }
            // add to cache.
            imagesCache.cache.put(path, bmap);
        }
        return bmap;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        scaleImage(bmImage);
    }

    private void scaleImage(ImageView view) {
        Bitmap bitmap;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (Exception e) {
            return;
        }
        view.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels,
                false));
    }

}