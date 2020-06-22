package com.example.memesfilter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_home_screen);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ProcessImagesService.class);
        startService(intent);

        TextView textView = (TextView) findViewById(R.id.home_screen_hello_message);

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        textView.setText(String.format(getResources().getString(R.string.home_page_hello_message_format), userName));

        findViewById(R.id.home_page_popular_template_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeScreenActivity.this, SearchTemplateActivity.class);
                        startActivity(intent);
                    }
                }
        );

        findViewById(R.id.home_page_all_memes_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add all memes:
                        ArrayList<GalleryCell> allMemes = new ArrayList<>();
                        ImagesCache imagesCache = ImagesCache.getInstance();

                        for (String imagePath: imagesCache.predictionsCache.keySet()) {
                            if (imagesCache.predictionsCache.get(imagePath)) {
                                allMemes.add(new GalleryCell("", imagePath));
                            }
                        }

                        // send them to the gallery activity.
                        final Intent intent = new Intent(HomeScreenActivity.this, GalleryActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) allMemes);
                        intent.putExtra("BUNDLE", args);
                        intent.putExtra("TITLE",getResources().getString(R.string.all_memes_headline));
                        HomeScreenActivity.this.startActivity(intent);
                    }
                }
        );


        findViewById(R.id.home_page_storage_template_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // recieve image:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        String[] mimeTrypes = {"image/jpg", "image/jpeg", "image/png"};
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTrypes);

                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pick an image"), GALLERY_REQUEST_CODE);

                    }
                }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    try {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));

                        new FindSimilarPictures().searchAndShow(this, selectedImageBmap, "Selected Image");
                    } catch (IOException e) {
                        Log.e("Error", e.getMessage());
                    }
            }
        }
    }
}