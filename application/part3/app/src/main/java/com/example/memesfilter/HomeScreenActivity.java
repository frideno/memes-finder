package com.example.memesfilter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.memesfilter.calculator.ImagesCalculator;
import com.example.memesfilter.calculator.ImagesCalculatorManager;
import com.example.memesfilter.gallery.GalleryActivity;
import com.example.memesfilter.gallery.GalleryCell;
import com.example.memesfilter.model.FindSimilarImages;
import com.example.memesfilter.utils.FileUtils;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST_CODE = 123;
    private boolean processedYet = false;

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

        if (!processedYet) {
            Intent intent = new Intent(this, ProcessImagesService.class);
            startService(intent);
            processedYet = true;
        }

        TextView greetingTextView = (TextView) findViewById(R.id.home_screen_hello_message);

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String greeting;
        if (userName != null) {
            if (userName.length() == 0) {
                userName = "guest";
            }
            greeting = String.format(getResources().getString(R.string.home_page_hello_message_format), userName);
        } else {
            greeting = "Welcome!";
        }
        greetingTextView.setText(greeting);

        findViewById(R.id.home_page_popular_template_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeScreenActivity.this, SearchTemplateActivity.class);
                        startActivity(intent);
                    }
                }
        );

        ImagesCalculator allMemesImagesCalculator = new ImagesCalculator() {
            @Override
            public List<GalleryCell> getImages() {
                ArrayList<GalleryCell> allMemes = new ArrayList<>();
                ImagesCache imagesCache = ImagesCache.getInstance();

                for (String imagePath : imagesCache.predictionsCache.keySet()) {
                    if (imagesCache.predictionsCache.get(imagePath) && FileUtils.isFileValid(imagePath)) {
                        allMemes.add(new GalleryCell("", imagePath));
                    }
                }
                return allMemes;
            }
        };
        final String imageCalculatorKey = ImagesCalculatorManager.getInstance().addCalculator(allMemesImagesCalculator);

        findViewById(R.id.home_page_all_memes_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // send them to the gallery activity.
                        final Intent intent = new Intent(HomeScreenActivity.this, GalleryActivity.class);
                        intent.putExtra("IMAGE_CALCULATOR_KEY", imageCalculatorKey);
                        intent.putExtra("TITLE", getString(R.string.all_memes_headline));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    ImagesCalculator imagesCalculator = new ImagesCalculator() {
                        @Override
                        public List<GalleryCell> getImages() {
                            try {
                                Uri selectedImageUri = data.getData();
                                Bitmap selectedImageBmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));

                                FindSimilarImages similarFinder = new FindSimilarImages();
                                return similarFinder.find(selectedImageBmap, "Selected Image");

                            } catch (IOException e) {
                                Log.e("Error", e.getMessage());
                                return new ArrayList<GalleryCell>();
                            }
                        }
                    };
                    final String imageCalculatorKey = ImagesCalculatorManager.getInstance().addCalculator(imagesCalculator);

                    // send them to the gallery activity.
                    final Intent intent = new Intent(HomeScreenActivity.this, GalleryActivity.class);
                    intent.putExtra("IMAGE_CALCULATOR_KEY", imageCalculatorKey);
                    intent.putExtra("TITLE", "Selected Image");
                    startActivity(intent);

            }
        }
    }
}