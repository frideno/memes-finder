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
                new FindSimilarPictures().searchAndShow(activity, ImagesCache.getInstance().bitmapsCache.get(galleryCell.getPath()), galleryCell.getTitle());
            }
        };

    }
}
