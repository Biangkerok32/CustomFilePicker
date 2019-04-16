package com.wangsun.android.filepicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.wangsun.android.filepicker.api.exceptions.PickerException;

import java.io.*;


public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();


    public static String getExternalFilesDir(String type, Context context) throws PickerException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean permissionGranted = checkForExternalStorageRuntimePermission(context);
            if (!permissionGranted) {
                Log.e(TAG, Manifest.permission.WRITE_EXTERNAL_STORAGE + " permission not available");
                throw new PickerException(Manifest.permission.WRITE_EXTERNAL_STORAGE + " permission not available");
            }
        }
        File directory = context.getExternalFilesDir(type);
        if (directory == null) {
            throw new PickerException("Couldn't initialize External Files Directory");
        }
        return directory.getAbsolutePath();
    }

    private static boolean checkForExternalStorageRuntimePermission(Context context) {
        boolean granted;
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        granted = permissionCheck == PackageManager.PERMISSION_GRANTED;
        return granted;
    }

    public static String getExternalCacheDir(Context context) throws PickerException {
        File directory = context.getExternalCacheDir();
        if (directory == null) {
            throw new PickerException("Couldn't intialize External Cache Directory");
        }
        return directory.getAbsolutePath();
    }

    public static void deleteDirectory() {
        File dir = new File(Environment.getExternalStorageDirectory()+"/CustomFilePicker/DuplicateFiles");
        if(dir.exists()){
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (String aChildren : children) {
                    new File(dir, aChildren).delete();
                }
            }
            dir.delete();
        }
    }

    public static String getInternalFileDirectory(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }
}
