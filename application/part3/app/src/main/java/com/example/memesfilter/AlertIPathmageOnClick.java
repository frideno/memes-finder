package com.example.memesfilter;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class AlertIPathmageOnClick implements ImageOnClickAdapter{

    @Override
    public View.OnClickListener getImageOnClickListener(final Context context, final GalleryCell galleryCell) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, galleryCell.getPath(), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
