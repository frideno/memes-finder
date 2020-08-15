package com.example.memesfilter.image_processing;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.core.util.Consumer;

import com.example.memesfilter.model.Classify;
import com.example.memesfilter.utils.ImageUtils;
import com.example.memesfilter.ImagesCache;
import com.example.memesfilter.db_handlers.ProcessedImageDataSchema;
import com.example.memesfilter.model.SimilarImage;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ProcessImageTask implements Runnable {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private ImagesCache cache;
    private Classify classifier;
    private String path;
    private Consumer<Pair<String, ProcessedImageDataSchema>> processedUpdateListener;

    public ProcessImageTask(Classify classifier, Consumer<Pair<String, ProcessedImageDataSchema>> listener, String path) {

        cache = ImagesCache.getInstance();
        this.classifier = classifier;
        this.path = path;
        this.processedUpdateListener = listener;
    }

    public void run() {
        Bitmap bmap;

        ProcessedImageDataSchema processedImageData = new ProcessedImageDataSchema();
        //load image:
        bmap = ImageUtils.loadFromGallery(path);

        // check if image is meme or not using tensorflow light model.
        boolean isMeme;
        if (cache.predictionsCache.containsKey(path)) {
            isMeme = cache.predictionsCache.get(path);
        } else {
            String memeClass = classifier.classify(bmap);
            isMeme = memeClass.equals("Yes");
            Log.i("Predictor", path + ": " + memeClass);
            cache.predictionsCache.put(path, isMeme);
        }

        processedImageData.setPrediction(isMeme);

        // calculate image hash
        String imageHash;
        if (isMeme) {
            if (cache.imageHashesCache.containsKey(path)) {
                imageHash = cache.imageHashesCache.get(path);
            } else {
                imageHash = SimilarImage.getFingerPrint(bmap);
                cache.imageHashesCache.put(path, imageHash);

                processedImageData.setImageHash(imageHash);
            }
        }

        processedUpdateListener.accept(new Pair<String, ProcessedImageDataSchema>(path, processedImageData));

    }

}
