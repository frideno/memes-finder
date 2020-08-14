package com.example.memesfilter;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProcessImageTask implements Runnable {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private ImagesCache cache;
    private Classify classifier;
    private String path;

    public ProcessImageTask(Classify classifier, String path) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users-cached-data");
        cache = ImagesCache.getInstance();
        this.classifier = classifier;
        this.path = path;
    }

    public void run() {
        Bitmap bmap;

        ProcessedImageDataSchema processedImageData = new ProcessedImageDataSchema();
        //load image:
        bmap = ImageUtils.loadFromGallery(path);

        // check cache db for prediction and image hash already:
        if (dbRef.child(user.getUid()) != null) {
            DatabaseReference imageDataRef = dbRef.child(user.getUid()).child(path.replace(".", "POINT").replace("/", "SLASH"));
            if (imageDataRef != null) {
                imageDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String path = dataSnapshot.getKey().replace("POINT", ".").replace("SLASH", "/");
                            ProcessedImageDataSchema imageData = dataSnapshot.getValue(ProcessedImageDataSchema.class);

                            if (imageData.getPrediction() != null) {
                                cache.predictionsCache.put(path, imageData.getPrediction());
                            }
                            if (imageData.getImageHash() != null) {
                                cache.imageHashesCache.put(path, imageData.getImageHash());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }

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
                imageHash = SimilarPhoto.getFingerPrint(bmap);
                cache.imageHashesCache.put(path, imageHash);

                processedImageData.setImageHash(imageHash);
            }
        }

        // upload results to cache db.
        dbRef.child(user.getUid()).child(path.replace(".", "POINT").replace("/", "SLASH")).setValue(processedImageData);
    }

}
