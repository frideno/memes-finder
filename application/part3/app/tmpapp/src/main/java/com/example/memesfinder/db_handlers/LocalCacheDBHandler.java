package com.example.memesfinder.db_handlers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.Pair;

import androidx.core.util.Consumer;

import com.example.memesfinder.sqlite_local_db.LocalDatabaseHelper;
import com.example.memesfinder.sqlite_local_db.LocalDatabaseManager;

public class LocalCacheDBHandler implements DBHandler {
    private LocalDatabaseManager localDatabaseManager;


    public LocalCacheDBHandler(Context context) {
        localDatabaseManager = new LocalDatabaseManager(context);
    }

    public void setProcessed(Pair<String, ProcessedImageDataSchema> entry) {
        String path, prediction, imageHash;

        path = entry.first;
        if (entry.second.getPrediction()) {
            prediction = "YES";
        } else {
            prediction = "NO";
        }
        imageHash = entry.second.getImageHash();

        localDatabaseManager.insert(path, prediction, imageHash);
    }

    public void getProcessed(final Consumer dbItemHandler) {

        Cursor cursor = localDatabaseManager.fetch();
        while (cursor != null && !cursor.isAfterLast()) {
            String path = cursor.getString(cursor.getColumnIndex(LocalDatabaseHelper._PATH));
            boolean prediction = cursor.getString(cursor.getColumnIndex(LocalDatabaseHelper.PREDICTION)).equals("YES");
            String imageHash = cursor.getString(cursor.getColumnIndex(LocalDatabaseHelper.IMAGE_HASH));

            ProcessedImageDataSchema item = new ProcessedImageDataSchema();
            item.setPrediction(prediction);
            item.setImageHash(imageHash);

            dbItemHandler.accept(new Pair<String, ProcessedImageDataSchema>(path, item));

            Log.d("SQLite Prediction Cache", path);

            cursor.moveToNext();

        }
    }

}
