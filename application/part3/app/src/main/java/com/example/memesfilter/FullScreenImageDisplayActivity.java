package com.example.memesfilter;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FullScreenImageDisplayActivity  extends AppCompatActivity {

    private ImageView imageView;
    private ImageView shareButtonImageView;
    private ImageView deleteButtonImageView;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.full_screen_image_activity);

        imageView = findViewById(R.id.full_screen_image);
        shareButtonImageView = findViewById(R.id.share_image);
        deleteButtonImageView = findViewById(R.id.delete_image);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Object> imagesObjectsList = (ArrayList<Object>) args.getSerializable("ARRAYLIST");
        GalleryCell galleryCell = (GalleryCell) imagesObjectsList.get(0);

        new DownloadImageTask(imageView, this).execute(galleryCell.getPath());

        shareButtonImageView.setOnClickListener(new ShareImageOnClick().getImageOnClickListener(this, galleryCell));
        deleteButtonImageView.setOnClickListener(new DeleteImageOnClick().getImageOnClickListener(this, galleryCell));

    }
}
