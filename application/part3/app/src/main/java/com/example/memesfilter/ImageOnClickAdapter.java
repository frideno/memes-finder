package com.example.memesfilter;

import android.content.Context;
import android.view.View;

public interface ImageOnClickAdapter {
    View.OnClickListener getImageOnClickListener(Context context, GalleryCell galleryCell);
}
