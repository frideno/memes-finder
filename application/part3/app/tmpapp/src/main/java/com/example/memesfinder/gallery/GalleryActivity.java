package com.example.memesfinder.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memesfinder.R;
import com.example.memesfinder.OpenImageOnClick;
import com.example.memesfinder.calculator.ImagesCalculator;
import com.example.memesfinder.calculator.ImagesCalculatorManager;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    protected ImagesCalculator imagesCalculator;
    protected String title;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_gallery);

        Intent intent = getIntent();
        String imageCalculatorKey = intent.getStringExtra("IMAGE_CALCULATOR_KEY");
        this.imagesCalculator = ImagesCalculatorManager.getInstance().getCalculator(imageCalculatorKey);

        String title = intent.getStringExtra("TITLE");
        this.title = title;
    }

    @Override
    protected void onStart() {
        super.onStart();

        showImages(imagesCalculator.getImages(), title);
    }

    protected void showImages(List<GalleryCell> galleryCells, String title) {

        // set title:
        TextView headlineTextView = (TextView) findViewById(R.id.gallery_title);
        headlineTextView.setText("\"" + title + "\" results:");

        // set images:
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gallery);
        recyclerView.setHasFixedSize(true);
        this.recyclerView = recyclerView;

        // set number of columns and rows of the gallery grid:
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        TextView noteTextView = (TextView) findViewById(R.id.gallery_note);
        if (galleryCells.size() == 0) {
            noteTextView.setText(R.string.no_results);
        } else {
            noteTextView.setText("");
        }

        DeleteableGalleryAdapter adapter = new DeleteableGalleryAdapter(galleryCells, this, new OpenImageOnClick());
        recyclerView.setAdapter(adapter);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //
            } else {
                Toast.makeText(this, "Permission not granted! can't delete files", Toast.LENGTH_SHORT).show();
            }
        }
    }


}