package com.example.memesfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;

import javax.net.ssl.HttpsURLConnection;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Context context;

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

        // if path is an http/https, download it.
        // TODO: cache.
        if (paths[0].startsWith("http")) {
            bmap = downloadFromUrl(paths[0]);
        }
        // if its local:
        else {
            bmap = loadFromGallery(paths[0]);
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

    private void scaleImage3(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (Exception e) {
            return;
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}