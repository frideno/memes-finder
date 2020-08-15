package com.example.memesfinder;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;

import com.example.memesfinder.image_processing.ProcessingManager;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessImagesService extends Service {

    private final String[] IMAGES_DIRS_PATHS = {Environment.DIRECTORY_PICTURES,
            "WhatsApp/Media/WhatsApp Images"};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        final ProcessingManager processingManager = new ProcessingManager(ProcessImagesService.this);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                processingManager.initCached();
                final ArrayList<String> cachedImages = new ArrayList<String>(ImagesCache.getInstance().predictionsCache.keySet());

                final ArrayList<String> galleryImagesPaths = new ArrayList<>();
                for (String path : IMAGES_DIRS_PATHS) {
                    galleryImagesPaths.addAll(ProcessImagesService.listAllImages(Environment.getExternalStorageDirectory() + "/" + path));
                }

                galleryImagesPaths.removeAll(cachedImages);

                processingManager.startProcessing(galleryImagesPaths);

                return null;
            }
        }.execute();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static ArrayList<String> listAllImages(String path) {
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


