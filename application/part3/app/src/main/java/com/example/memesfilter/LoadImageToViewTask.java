package com.example.memesfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.core.ThreadBackgroundExecutor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.net.ssl.HttpsURLConnection;

public class LoadImageToViewTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    private Context context;

    public LoadImageToViewTask(ImageView bmImage, Context context) {
        this.bmImage = bmImage;
        this.context = context;
    }

    protected Bitmap doInBackground(String... paths) {
        Bitmap bmap;
        String path = paths[0];

        // if path is an http/https, download it. first check in cache.
        if (path.startsWith("http")) {
            ImagesCache imagesCache = ImagesCache.getInstance();
            if (imagesCache.bitmapsCache.containsKey(path)) {
                bmap = imagesCache.bitmapsCache.get(path);
                Log.println(Log.INFO, "Cache", path);
            }
            else {
                bmap = ImageUtils.downloadFromUrl(path);
                if (bmap != null) {
                    imagesCache.bitmapsCache.put(path, bmap);
                }
            }

        }
        // if its local:
        else {
            bmap = ImageUtils.loadFromGallery(path);
        }
        return bmap;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            scaleImage(bmImage, result);
        }
    }

    private void scaleImage(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels,
                false));
    }

}