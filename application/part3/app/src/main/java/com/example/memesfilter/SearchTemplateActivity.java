package com.example.memesfilter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchTemplateActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private ArrayList<GalleryCell> templates;
    private RecyclerView recyclerView;
    private SearchView searchView;

    public SearchTemplateActivity() {
        templates = new ArrayList<>();

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_template);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //
            } else {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        // todo: move to loaing screen activity:
        super.onStart();
        // connect to firebase database of templates
        dbRef = FirebaseDatabase.getInstance().getReference().child("templates");
        recyclerView = findViewById(R.id.templates);
        searchView = findViewById(R.id.searchView);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        // request gallery permissions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        } else {
            // startGallery();
        }

        if (dbRef != null) {
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        templates = new ArrayList<>();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            templates.add(ds.getValue(GalleryCell.class));
                        }

                        GalleryAdapter galleryAdapter = new GalleryAdapter(templates, SearchTemplateActivity.this, new FindSimilarPicturesOnClick());
                        recyclerView.setAdapter(galleryAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SearchTemplateActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }

    private void search(String query) {
        ArrayList<GalleryCell> results = new ArrayList<>();
        for (GalleryCell cell: templates) {
            if (cell.getTitle().toLowerCase().contains(query.toLowerCase())) {
                results.add(cell);
            }
        }
        GalleryAdapter galleryAdapter = new GalleryAdapter(results, this, new FindSimilarPicturesOnClick());
        recyclerView.setAdapter(galleryAdapter);
    }

}