package com.example.memesfinder.calculator;

import com.example.memesfinder.gallery.GalleryCell;

import java.io.Serializable;
import java.util.List;

public interface ImagesCalculator extends Serializable {
    List<GalleryCell> getImages();
}
