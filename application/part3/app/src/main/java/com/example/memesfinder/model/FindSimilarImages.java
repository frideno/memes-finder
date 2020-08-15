package com.example.memesfinder.model;

import android.graphics.Bitmap;

import com.example.memesfinder.gallery.GalleryCell;
import com.example.memesfinder.ImagesCache;
import com.example.memesfinder.utils.FileUtils;

import java.util.ArrayList;

public class FindSimilarImages {

    public ArrayList<GalleryCell> find(Bitmap bmap, String title) {
        final String templateHash = SimilarImage.getFingerPrint(bmap);

        // find the images that are similar.
        ArrayList<GalleryCell> matchingPictures = new ArrayList<>();

        ImagesCache imagesCache = ImagesCache.getInstance();
        for (String imagePath : imagesCache.imageHashesCache.keySet()) {
            if (FileUtils.isFileValid(imagePath)) {
                if (SimilarImage.hamDist(imagesCache.imageHashesCache.get(imagePath), templateHash) < SimilarImage.MAX_SIMILAR_DIFF) {
                    if (imagesCache.predictionsCache.get(imagePath)) {
                        matchingPictures.add(new GalleryCell(title, imagePath));
                    }
                }
            }
        }

        return matchingPictures;
    }

}

