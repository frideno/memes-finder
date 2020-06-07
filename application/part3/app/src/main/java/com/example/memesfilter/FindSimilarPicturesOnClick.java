package com.example.memesfilter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class FindSimilarPicturesOnClick implements ImageOnClickAdapter {

    @Override
    public View.OnClickListener getImageOnClickListener(final Context context, final GalleryCell galleryCell) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // find the images that are similar.
                ArrayList<GalleryCell> galleryCells = new ArrayList<>();
                galleryCells.add(galleryCell);

                // todo: change to real.

                // send them to the gallery activity.
                Intent intent = new Intent(context, GalleryActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) galleryCells);
                intent.putExtra("BUNDLE", args);
                context.startActivity(intent);
            }
        };
    }
}
