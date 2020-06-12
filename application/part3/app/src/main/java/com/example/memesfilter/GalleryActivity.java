package com.example.memesfilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private ArrayList<GalleryCell> galleryCells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Object> imagesObjectsList = (ArrayList<Object>) args.getSerializable("ARRAYLIST");
        ArrayList<GalleryCell> imagesList = new ArrayList<>(imagesObjectsList.size());
        for (Object object : imagesObjectsList) {
            imagesList.add((GalleryCell) object);
        }
        String title = intent.getStringExtra("TITLE");
        showImages(imagesList, title);
    }

    private void showImages(ArrayList<GalleryCell> galleryCells, String title) {

        // set title:
        TextView textView = (TextView) findViewById(R.id.gallery_title);
        textView.setText("\"" + title + "\" results:");

        // set images:
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gallery);
        recyclerView.setHasFixedSize(true);

        // set number of columns and rows of the gallery grid:
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        GalleryAdapter adapter = new GalleryAdapter(galleryCells, this, new ShareImageOnClick());
        recyclerView.setAdapter(adapter);

    }



}