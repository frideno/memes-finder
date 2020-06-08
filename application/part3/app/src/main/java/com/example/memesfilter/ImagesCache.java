package com.example.memesfilter;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Image cache singelton.
 * store image uri->bitmap.
 * uri can be http url of image or a local storage path.
 *
 * TODO: expiration time.
 * */
public class ImagesCache {

    private static ImagesCache instance = null;

    public ConcurrentHashMap<String, Bitmap> cache;

    private ImagesCache() {
        cache = new ConcurrentHashMap<>();
    }

    public static ImagesCache getInstance() {
        if (instance == null) {
            instance = new ImagesCache();
        }
        return instance;
    }

}
