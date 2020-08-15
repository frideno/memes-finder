package com.example.memesfinder.gallery;

import android.os.Handler;

import java.util.List;

public class RefreshedGalleryActivity extends GalleryActivity {
    private static final int MAX_SECONDS_REFRESH = 60 ;

    private List<GalleryCell> lastImages;

    @Override
    protected void onStart() {
        super.onStart();

         lastImages = imagesCalculator.getImages();

        Handler handler=new Handler();

        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                List<GalleryCell> newImages = imagesCalculator.getImages();
                if (! lastImages.containsAll(newImages)) {
                    showImages(newImages, title);
                    lastImages = newImages;
                }
            }
        };

        // auto refreshed each X seconds:
        for (int i = 1; i < MAX_SECONDS_REFRESH; i *= 2) {
            handler.postDelayed(refreshRunnable, i * 1000);

        }
    }

}

