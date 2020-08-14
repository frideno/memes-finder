package com.example.memesfilter;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ProcessImagesService extends Service {

    private final int NUM_THREADS = 8;
    private final String[] IMAGES_DIRS_PATHS = {Environment.DIRECTORY_PICTURES,
                                                    "WhatsApp/Media/WhatsApp Images"};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // fill in with new device data:
        final ArrayList<String> galleryImagesPaths = new ArrayList<>();
        for (String path: IMAGES_DIRS_PATHS) {
                galleryImagesPaths.addAll(listAllImages(Environment.getExternalStorageDirectory() + "/" + path));

        }
        final AssetManager assetManager = this.getAssets();

        // run all process images task in threadpool executor:
        final int imagesPerThread = (int) (galleryImagesPaths.size() / NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            final int finalI = i;
            final Classify classifier = new Classify(assetManager);

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    for (int j = finalI * imagesPerThread; j < (finalI +1) * imagesPerThread; j++) {
                        String imagePath = galleryImagesPaths.get(j);
                        executor.submit(new ProcessImageTask(classifier, imagePath));
                    }

                    return null;
                }

            }.execute();
        }



        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ArrayList<String> listAllImages(String path) {
        ArrayList<String> imagesPaths = new ArrayList<>();
        List<String> legalPicturesExtensions = Arrays.asList("jpg", "png", "jpeg");

        File root = new File(path);
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (legalPicturesExtensions.contains(FilenameUtils.getExtension(f.getAbsolutePath()))) {
                    imagesPaths.add(f.getAbsolutePath());
                }
            }
        }
        return imagesPaths;
    }
}
