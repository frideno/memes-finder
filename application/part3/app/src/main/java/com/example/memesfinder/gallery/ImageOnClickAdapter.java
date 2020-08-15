package com.example.memesfinder.gallery;

import android.app.Activity;
import android.view.View;

public interface ImageOnClickAdapter {
    View.OnClickListener getImageOnClickListener(Activity activity, GalleryCell galleryCell);
}
