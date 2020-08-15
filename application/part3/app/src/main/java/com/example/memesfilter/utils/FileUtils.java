package com.example.memesfilter.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static boolean isFileValid(String fileFullPath) {
        File f = new File(fileFullPath);
        return f.exists() && f.isFile();
    }

    public static void deleteFile(String fileFullPath) {
        File f = new File(fileFullPath);
        if (f.exists() && f.delete()) {
            Log.i("Deleted", fileFullPath);
        }
        else {
            Log.i("Failed Delete", fileFullPath);
        }
    }

    public static void shareFiles(Activity activity, List<String> filePaths) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("*/*");

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(String path : filePaths) {
            File file = new File(path);
            Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider",  file);
            files.add(uri);
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        activity.startActivity(intent);

    }
}
