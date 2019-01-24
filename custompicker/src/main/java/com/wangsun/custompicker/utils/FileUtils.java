package com.wangsun.custompicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.wangsun.custompicker.api.exceptions.PickerException;
import com.wangsun.custompicker.storage.StoragePreferences;

import java.io.*;


public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();

    public static String getExternalFilesDirectory(String type, Context context) throws PickerException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean permissionGranted = checkForExternalStorageRuntimePermission(context);
            if (!permissionGranted) {
                Log.e(TAG, Manifest.permission.WRITE_EXTERNAL_STORAGE + " permission not available");
                throw new PickerException(Manifest.permission.WRITE_EXTERNAL_STORAGE + " permission not available");
            }
        }
        File directory = Environment.getExternalStorageDirectory();
        String appName = getAppName(context);
        String appDirectory = directory.getAbsolutePath() + File.separator + appName;
        File fileAppDirectory = new File(appDirectory);
        if (!fileAppDirectory.exists()) {
            fileAppDirectory.mkdir();
        }
        String appTypeDirectory = fileAppDirectory.getAbsolutePath() + File.separator + appName + " " + type;
        File finalDirectory = new File(appTypeDirectory);
        if (!finalDirectory.exists()) {
            finalDirectory.mkdir();
        }
        if (finalDirectory == null) {
            throw new PickerException("Couldn't initialize External Storage Path");
        }
        return finalDirectory.getAbsolutePath();
    }

    private static String getAppName(Context context) {
        StoragePreferences preferences = new StoragePreferences(context);
        String savedFolderName = preferences.getFolderName();
        if (savedFolderName == null || savedFolderName.isEmpty()) {
            try {
                ApplicationInfo info = context.getApplicationInfo();
                savedFolderName = context.getString(info.labelRes);
            } catch (Exception e) {
                String packageName = context.getPackageName();
                if (packageName.contains(".")) {
                    int index = packageName.lastIndexOf(".");
                    savedFolderName = packageName.substring(index + 1);
                } else {
                    savedFolderName = context.getPackageName();
                }
                preferences.setFolderName(savedFolderName);
            }
        }
        return savedFolderName;
    }

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



    public static String getInternalFileDirectory(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }
}
