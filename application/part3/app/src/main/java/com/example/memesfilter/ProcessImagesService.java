package com.example.memesfilter;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessImagesService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // fill in with new device data:
        ArrayList<String> galleryImagesPaths = listAllImages(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/Camera");
        for (String imagePath : galleryImagesPaths) {
            new ProcessImageTask().execute(imagePath);
        }

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ArrayList<String> listAllImages(String path) {
        ArrayList<String> imagesPaths = new ArrayList<>();
        List<String> legalPicturesExtensions = Arrays.asList("jpg", "png", "jpeg");

        File root = new File(path);
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (legalPicturesExtensions.contains(FilenameUtils.getExtension(f.getAbsolutePath()))) {
                    imagesPaths.add(f.getAbsolutePath());
                }
            }
        }
        return imagesPaths;
    }
}
