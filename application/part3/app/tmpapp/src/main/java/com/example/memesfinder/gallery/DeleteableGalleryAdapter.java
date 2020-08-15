package com.example.memesfinder.gallery;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memesfinder.LoadImageToViewTask;
import com.example.memesfinder.R;
import com.example.memesfinder.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class DeleteableGalleryAdapter extends RecyclerView.Adapter<DeleteableGalleryAdapter.ViewHolder> {
    private List<GalleryCell> galleryCells;
    private AppCompatActivity activity;
    private ImageOnClickAdapter imageOnClickAdapter;

    private boolean multiSelected;
    private ActionMode actionMode;
    private List<GalleryCell> selectedCells;

    public DeleteableGalleryAdapter(List<GalleryCell> galleryCells, AppCompatActivity activity, ImageOnClickAdapter imageOnClickAdapter) {
        this.galleryCells = galleryCells;
        this.activity = activity;
        this.imageOnClickAdapter = imageOnClickAdapter;
        this.multiSelected = false;

        this.selectedCells = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeleteableGalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_cell, viewGroup, false);
        return new DeleteableGalleryAdapter.ViewHolder(view);

    }

    @Override
    public int getItemCount() {
        return galleryCells.size();
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selection_menu, menu);
            multiSelected = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            // delete button:
            if (item.getItemId() == R.id.select_menu_delete) {
                // creates an "are you sure you want to delete it dialog"
                AlertDialog.Builder deleteConfirmDialogBuilder = new AlertDialog.Builder(activity);
                deleteConfirmDialogBuilder.setTitle("delete");
                deleteConfirmDialogBuilder.setMessage("Are you sure you want to delete?");
                deleteConfirmDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // check for delete permissions:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2000);
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED) {
                            for (GalleryCell selectedCell : selectedCells) {
                                FileUtils.deleteFile(selectedCell.getPath());
                                galleryCells.remove(selectedCell);
                            }

                            dialog.dismiss();
                            notifyDataSetChanged();
                            onDestroyActionMode(mode);
                        }
                    }
                });
                deleteConfirmDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                deleteConfirmDialogBuilder.create().show();


            }

            // share button:
            if (item.getItemId() == R.id.select_menu_share) {
                ArrayList<String> filePathsToShare = new ArrayList<>();
                for (GalleryCell selectedCell: selectedCells) {
                    filePathsToShare.add(selectedCell.getPath());
                }

                FileUtils.shareFiles(activity, filePathsToShare);
            }


            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelected = false;
            selectedCells.clear();
        }
    };


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // bind image with its bitmap view.
        holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        SetImageFromPath(galleryCells.get(position).getPath(), holder.image);

        final GalleryCell cell = galleryCells.get(position);
        final View.OnClickListener regularImageOnClickListener = this.imageOnClickAdapter.getImageOnClickListener(activity, cell);

        // add click listener to the image, which passes the gallery cell;
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!multiSelected) {
                    regularImageOnClickListener.onClick(v);
                } else {
                    holder.selectCell(cell);
                }

            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!multiSelected) {
                    actionMode = activity.startSupportActionMode(actionModeCallback);
                    holder.selectCell(cell);
                    return true;
                }
                return false;
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private FrameLayout frameLayout;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.gallery_image);
            frameLayout = (FrameLayout) view.findViewById(R.id.gallery_image_frame_layout);
        }

        private void selectCell(GalleryCell cell) {
            if (multiSelected) {
                // unselect cell:
                if (selectedCells.contains(cell)) {
                    selectedCells.remove(cell);
                    frameLayout.setBackgroundColor(activity.getColor(R.color.gallery_background));

                }
                // select cell:
                else {
                    selectedCells.add(cell);
                    frameLayout.setBackgroundColor(activity.getColor(R.color.selected_color));
                }


                // if now the selected is empty closes menu, else updates its selected count.
                if (selectedCells.size() == 0) {
                    actionMode.finish();
                } else {
                    actionMode.setTitle(String.format("%d images selected", selectedCells.size()));

                }


            }
        }

    }


    private void SetImageFromPath(String path, ImageView imageView) {
        new LoadImageToViewTask(imageView, activity).execute(path);

    }


}
