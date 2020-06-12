package com.example.memesfilter;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class ProcessImageTask extends AsyncTask<String, Void, Void> {

    protected Void doInBackground(String... paths) {
        Bitmap bmap;
        String path = paths[0];
        ImagesCache cache = ImagesCache.getInstance();

        //load image:

        // if path is an http/https, download it. first check in cache.
        if (path.startsWith("http")) {
            if (cache.bitmapsCache.containsKey(path)) {
                bmap = cache.bitmapsCache.get(path);
                Log.println(Log.INFO, "Cache", path);
            } else {
                bmap = ImageUtils.downloadFromUrl(path);
                cache.bitmapsCache.put(path, bmap);
            }

        }
        // if its local:
        else {
            bmap = ImageUtils.loadFromGallery(path);
        }

        // check if image is meme or not using tensorflow light model.
        boolean isMeme;
        if (cache.predictionsCache.containsKey(path)) {
            isMeme = cache.predictionsCache.get(path);
        } else {
            isMeme = true;
            cache.predictionsCache.put(path, isMeme);
        }

        // calculate image hash
        long imageHash;
        if (isMeme) {
            if (cache.imageHashesCache.containsKey(path)) {
                imageHash = cache.imageHashesCache.get(path);
            } else {
                imageHash = SimilarPhoto.getFingerPrint(bmap);
                cache.imageHashesCache.put(path, imageHash);
            }
        }

        return null;
    }

}
