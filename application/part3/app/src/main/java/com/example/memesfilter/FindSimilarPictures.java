package com.example.memesfilter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

public class FindSimilarPictures {

    public ArrayList<GalleryCell> find(Bitmap bmap, String title) {
        final String templateHash = SimilarPhoto.getFingerPrint(bmap);

        // find the images that are similar.
        ArrayList<GalleryCell> matchingPictures = new ArrayList<>();

        ImagesCache imagesCache = ImagesCache.getInstance();
        for (String imagePath : imagesCache.imageHashesCache.keySet()) {
            if (Utils.isFileValid(imagePath)) {
                if (SimilarPhoto.hamDist(imagesCache.imageHashesCache.get(imagePath), templateHash) < SimilarPhoto.MAX_SIMILAR_DIFF) {
                    if (imagesCache.predictionsCache.get(imagePath)) {
                        matchingPictures.add(new GalleryCell(title, imagePath));
                    }
                }
            }
        }

        return matchingPictures;
    }

}

