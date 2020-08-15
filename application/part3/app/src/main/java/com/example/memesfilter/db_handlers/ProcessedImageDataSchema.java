package com.example.memesfilter.db_handlers;

public class ProcessedImageDataSchema {
    private Boolean prediction;
    private String imageHash;

    public Boolean getPrediction() {
        return prediction;
    }

    public void setPrediction(Boolean prediction) {
        this.prediction = prediction;
    }

    public String getImageHash() {
        return imageHash;
    }

    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }
}
