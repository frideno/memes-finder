package com.example.memesfilter;

import java.io.Serializable;

public class GalleryCell implements Serializable {
    private String title;
    private String path;

    public GalleryCell(String title, String path) {
        this.title = title;
        this.path = path;
    }
    public GalleryCell() {
        this.title = null;
        this.path = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
