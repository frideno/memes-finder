package com.example.memesfilter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<GalleryCell> galleryCells;
    private Activity activity;
    private ImageOnClickAdapter imageOnClickAdapter;


    public GalleryAdapter(ArrayList<GalleryCell> galleryCells, Activity activity, ImageOnClickAdapter imageOnClickAdapter) {
        this.galleryCells = galleryCells;
        this.activity = activity;
        this.imageOnClickAdapter = imageOnClickAdapter;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_cell, viewGroup, false);
        return new GalleryAdapter.ViewHolder(view);

    }

    @Override
    public int getItemCount() {
        return galleryCells.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // bind image with its bitmap view.
        holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        SetImageFromPath(galleryCells.get(position).getPath(), holder.image);

        // add click listener to the image, which passes the gallery cell;
        holder.image.setOnClickListener(this.imageOnClickAdapter.getImageOnClickListener(activity, galleryCells.get(position)));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.gallery_image);
        }
    }

    private void SetImageFromPath(String path, ImageView imageView) {
        Bitmap bMap;
        new DownloadImageTask(imageView, activity).execute(path);

    }


}
