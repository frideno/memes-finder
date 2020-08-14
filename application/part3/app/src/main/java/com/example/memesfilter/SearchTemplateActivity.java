package com.example.memesfilter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import java.util.Collection;
import java.util.Collections;

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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_search_template);

        buildTemplates(true);
    }

    private void buildTemplates(final boolean shuffle) {
        // todo: move to loaing screen activity:
        // connect to firebase database of templates
        dbRef = FirebaseDatabase.getInstance().getReference().child("templates");
        recyclerView = findViewById(R.id.templates);
        searchView = findViewById(R.id.searchView);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);


        if (dbRef != null) {
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        templates = new ArrayList<>();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            templates.add(ds.getValue(GalleryCell.class));
                        }
                        if (shuffle)
                            Collections.shuffle(templates);

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
