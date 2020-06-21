package com.example.memesfilter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public interface ImageOnClickAdapter {
    View.OnClickListener getImageOnClickListener(Activity activity, GalleryCell galleryCell);
}
