package com.example.memesfilter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class FullScreenImageOnClick implements ImageOnClickAdapter{

    @Override
    public View.OnClickListener getImageOnClickListener(final Activity activity, final GalleryCell galleryCell) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // find the images that are similar.
                ArrayList<GalleryCell> galleryCells = new ArrayList<>();
                galleryCells.add(galleryCell);

                // todo: change to real.

                // send them to the gallery activity.
                Intent intent = new Intent(activity, FullScreenImageDisplayActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) galleryCells);
                intent.putExtra("BUNDLE", args);
                activity.startActivity(intent);
            }
        };
    }
}
