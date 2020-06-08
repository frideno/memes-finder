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


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class FindSimilarPicturesOnClick implements ImageOnClickAdapter {

    @Override
    public View.OnClickListener getImageOnClickListener(final Activity activity, final GalleryCell galleryCell) {
        final ImagePHash phash = new ImagePHash();
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // find the images that are similar.
                ArrayList<GalleryCell> matchingPictures = new ArrayList<>();
                //galleryCells = listAllImages();

                File root = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
                Bitmap templateBitmap = ((BitmapDrawable) (((ImageView) v).getDrawable())).getBitmap();

                String templateHash = phash.culcPHash(templateBitmap);

                for (File file : root.listFiles()) {
                    Bitmap fileBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    String fileHash = phash.culcPHash(fileBitmap);

                    if (phash.distance(templateHash, fileHash) < 15) {
                        matchingPictures.add(new GalleryCell(galleryCell.getTitle(), file.getAbsolutePath()));
                    }
                }

//                long templateHash = SimilarPhoto.getFingerPrint(scaleBitmap(templateBitmap));
//
//                for (File file : root.listFiles()) {
//                    Bitmap fileBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    long fileHash = SimilarPhoto.getFingerPrint(scaleBitmap(fileBitmap));
//
//                    if (SimilarPhoto.hamDist(fileHash, templateHash) < 5) {
//                        matchingPictures.add(new GalleryCell(galleryCell.getTitle(), file.getAbsolutePath()));
//                    }
//                }


                // send them to the gallery activity.
                final Intent intent = new Intent(activity, GalleryActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) matchingPictures);
                intent.putExtra("BUNDLE", args);
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

    private ArrayList<GalleryCell> listAllImages(String pathName) {
        ArrayList<GalleryCell> galleryFiles = new ArrayList<>();
        File file = new File(pathName);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                galleryFiles.add(new GalleryCell("Drake", f.getAbsolutePath()));
            }
        }
        return galleryFiles;
    }
}
