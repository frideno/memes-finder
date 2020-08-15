package com.example.memesfilter.db_handlers;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseCacheDBHandler implements DBHandler {
    private DatabaseReference dbRef;
    private FirebaseUser user;

    public FirebaseCacheDBHandler(DatabaseReference dbRef, FirebaseUser user) {
        this.dbRef = dbRef;
        this.user = user;
    }

    public void setProcessed(Pair<String, ProcessedImageDataSchema> entry) {
        String path = entry.first;
        ProcessedImageDataSchema processedImageData = entry.second;

        String fixedPath = path.replace(".", "POINT").replace("/", "SLASH");
        dbRef.child(user.getUid()).child(fixedPath).setValue(processedImageData);
    }

    public void getProcessed(final Consumer dbItemHandler) {

        if (dbRef.child(user.getUid()) != null) {
            DatabaseReference imagesDataRef = dbRef.child(user.getUid());

            // check cache db for prediction and image hash already:
            if (imagesDataRef != null) {
                imagesDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String fixedPath = ds.getKey();
                                String path = fixedPath.replace("POINT", ".").replace("SLASH", "/");

                                ProcessedImageDataSchema processed = ds.getValue(ProcessedImageDataSchema.class);

                                dbItemHandler.accept(new Pair<String, ProcessedImageDataSchema>(path, processed));

                                Log.d("Firebase Prediction Cache", path);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }

    }

}
