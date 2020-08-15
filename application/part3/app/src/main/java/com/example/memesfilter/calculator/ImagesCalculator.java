package com.example.memesfilter.calculator;

import com.example.memesfilter.gallery.GalleryCell;

import java.io.Serializable;
import java.util.List;

public interface ImagesCalculator extends Serializable {
    List<GalleryCell> getImages();
}
