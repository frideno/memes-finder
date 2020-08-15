package com.example.memesfilter;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Image cache singelton.
 * store image uri->bitmap.
 * uri can be http url of image or a local storage path.
 *
 * TODO: expiration time.
 * */
public class ImagesCache implements Serializable {

    private static ImagesCache instance = null;

    public ConcurrentHashMap<String, Bitmap> bitmapsCache;
    public ConcurrentHashMap<String, Boolean> predictionsCache;
    public ConcurrentHashMap<String, String> imageHashesCache;



    private ImagesCache() {
        bitmapsCache = new ConcurrentHashMap<>();
        predictionsCache = new ConcurrentHashMap<>();
        imageHashesCache = new ConcurrentHashMap<>();
    }

    public static ImagesCache getInstance() {
        if (instance == null) {
            instance = new ImagesCache();
        }
        return instance;
    }

}
