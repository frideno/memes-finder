package com.example.memesfilter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.example.memesfilter.gallery.GalleryCell;
import com.example.memesfilter.gallery.ImageOnClickAdapter;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Objects;

public class OpenImageOnClick implements ImageOnClickAdapter {
    @Override
    public View.OnClickListener getImageOnClickListener(final Activity activity, final GalleryCell galleryCell) {

        File image = new File(galleryCell.getPath());
        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(activity),BuildConfig.APPLICATION_ID + ".provider", image);
        
        final Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setStream(uri) // uri from FileProvider
                .setType("image/" + FilenameUtils.getExtension(galleryCell.getPath()))
                .getIntent()
                .setAction(Intent.ACTION_VIEW) //Change if needed
                .setDataAndType(uri, "image/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(intent);
            }
        };
    }
}
