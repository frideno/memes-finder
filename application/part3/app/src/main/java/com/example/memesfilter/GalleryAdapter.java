package com.example.memesfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.net.ssl.HttpsURLConnection;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<GalleryCell> galleryCells;
    private Context context;
    private ImageOnClickAdapter imageOnClickAdapter;


    public GalleryAdapter(ArrayList<GalleryCell> galleryCells, Context context, ImageOnClickAdapter imageOnClickAdapter) {
        this.galleryCells = galleryCells;
        this.context = context;
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
        holder.image.setOnClickListener(this.imageOnClickAdapter.getImageOnClickListener(context, galleryCells.get(position)));

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
        new DownloadImageTask(imageView, context).execute(path);

    }


}
