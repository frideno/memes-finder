package com.example.memesfinder.db_handlers;

import android.util.Pair;

import androidx.core.util.Consumer;

public interface DBHandler {
    void setProcessed(Pair<String, ProcessedImageDataSchema> entry);

    void getProcessed(final Consumer dbItemHandler);

}