package com.example.memesfilter;

import java.io.Serializable;
import java.util.List;

public interface ImagesCalculator extends Serializable {
    List<GalleryCell> getImages();
}
