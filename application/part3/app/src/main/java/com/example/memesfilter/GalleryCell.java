package com.example.memesfilter;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GalleryCell)) return false;
        GalleryCell that = (GalleryCell) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, path);
    }
}
