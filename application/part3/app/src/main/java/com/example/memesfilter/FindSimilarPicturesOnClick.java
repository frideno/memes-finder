package com.example.memesfilter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FindSimilarPicturesOnClick implements ImageOnClickAdapter {

    @Override
    public View.OnClickListener getImageOnClickListener(final Activity activity, final GalleryCell galleryCell) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Long templateHash = SimilarPhoto.getFingerPrint(ImagesCache.getInstance().bitmapsCache.get(galleryCell.getPath()));

                // find the images that are similar.
                ArrayList<GalleryCell> matchingPictures = new ArrayList<>();

                ImagesCache imagesCache = ImagesCache.getInstance();
                for (String imagePath : imagesCache.imageHashesCache.keySet()) {
                    if (SimilarPhoto.hamDist(imagesCache.imageHashesCache.get(imagePath), templateHash) < 5) {
                        matchingPictures.add(new GalleryCell(galleryCell.getTitle(), imagePath));
                        Log.d("Similar:", imagePath + '\t' + galleryCell.getPath() +
                                SimilarPhoto.hamDist(imagesCache.imageHashesCache.get(imagePath), templateHash));
                    }
                }

                // send them to the gallery activity.
                final Intent intent = new Intent(activity, GalleryActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) matchingPictures);
                intent.putExtra("BUNDLE", args);
                intent.putExtra("TITLE", galleryCell.getTitle());
                activity.startActivity(intent);
            }
        };
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        float scale_width, scale_height;
        scale_width = 8.0f / bitmap.getWidth();
        scale_height = 8.0f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale_width, scale_height);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return scaledBitmap;
    }

}
