package com.example.memesfilter.image_processing;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import androidx.core.util.Consumer;

import com.example.memesfilter.db_handlers.FirebaseCacheDBHandler;
import com.example.memesfilter.model.Classify;
import com.example.memesfilter.ImagesCache;
import com.example.memesfilter.db_handlers.LocalCacheDBHandler;
import com.example.memesfilter.db_handlers.ProcessedImageDataSchema;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessingManager {

    private Context context;

    private FirebaseCacheDBHandler cacheDBHandler;
    private LocalCacheDBHandler localCacheDBHandler;

    private final int NUM_THREADS = 8;

    private Consumer<Pair<String, ProcessedImageDataSchema>> getCacheConsumer;
    private Consumer<Pair<String, ProcessedImageDataSchema>> setCacheConsumer;


    public ProcessingManager(Context context) {
        this.context = context;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbReff = FirebaseDatabase.getInstance().getReference().child("users-cached-data");
        this.cacheDBHandler = new FirebaseCacheDBHandler(dbReff, user);
        this.localCacheDBHandler = new LocalCacheDBHandler(context);

        this.getCacheConsumer = new Consumer<Pair<String, ProcessedImageDataSchema>>() {
            final ImagesCache imageCache = ImagesCache.getInstance();

            @Override
            public void accept(Pair<String, ProcessedImageDataSchema> entry) {
                if (entry.second.getPrediction() != null) {
                    imageCache.predictionsCache.put(entry.first, entry.second.getPrediction());
                }
                if (entry.second.getImageHash() != null) {
                    imageCache.imageHashesCache.put(entry.first, entry.second.getImageHash());
                }
            }
        };

        this.setCacheConsumer = new Consumer<Pair<String, ProcessedImageDataSchema>>() {
            final ImagesCache imageCache = ImagesCache.getInstance();

            @Override
            public void accept(Pair<String, ProcessedImageDataSchema> entry) {
                cacheDBHandler.setProcessed(entry);
                localCacheDBHandler.setProcessed(entry);
            }
        };
    }

    public void initCached() {
        cacheDBHandler.getProcessed(getCacheConsumer);
        localCacheDBHandler.getProcessed(getCacheConsumer);
    }

    public void startProcessing(final List<String> imagesPathsToProcess) {

        // run all process images task in threadpool executor:
        final int imagesPerThread = (int) (imagesPathsToProcess.size() / NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            final int finalI = i;
            final Classify classifier = new Classify(context.getAssets());

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    for (int j = finalI * imagesPerThread; j < (finalI +1) * imagesPerThread; j++) {
                        String imagePath = imagesPathsToProcess.get(j);
                        executor.submit(new ProcessImageTask(classifier, setCacheConsumer, imagePath));
                    }

                    return null;
                }

            }.execute();
        }
    }


}
