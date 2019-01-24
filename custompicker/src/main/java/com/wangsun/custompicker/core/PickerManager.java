package com.wangsun.custompicker.core;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.wangsun.custompicker.api.CacheLocation;
import com.wangsun.custompicker.api.exceptions.PickerException;
import com.wangsun.custompicker.storage.StoragePreferences;
import com.wangsun.custompicker.utils.LogUtils;

/**
 * Abstract class for all types of Pickers
 */
public abstract class PickerManager {
    private final static String TAG = PickerManager.class.getSimpleName();
    private Activity activity;
    private Fragment fragment;
    private android.app.Fragment appFragment;

    public static boolean debugglable;

    protected final int pickerType;
    protected int requestId;

    protected int cacheLocation = CacheLocation.EXTERNAL_STORAGE_PUBLIC_DIR;

    protected Bundle extras;

    protected boolean allowMultiple;

    public PickerManager(Activity activity, int pickerType) {
        this.activity = activity;
        this.pickerType = pickerType;
        initProperties();
    }

    public PickerManager(Fragment fragment, int pickerType) {
        this.fragment = fragment;
        this.pickerType = pickerType;
        initProperties();
    }

    public PickerManager(android.app.Fragment appFragment, int pickerType) {
        this.appFragment = appFragment;
        this.pickerType = pickerType;
        initProperties();
    }

    private void initProperties(){
        debugglable = new StoragePreferences(getContext()).isDebuggable();
    }



    /**
     * Since {@link CacheLocation#EXTERNAL_STORAGE_PUBLIC_DIR} is deprecated, you will have no
     * option to set the folder name now. If at all you need to copy the files into the public
     * sotrage for exposing them to other applications, you will have to implement the
     * copying/moving the files code yourself.
     * @param folderName
     */
    @Deprecated
    public void setFolderName(String folderName) {
        StoragePreferences preferences = new StoragePreferences(getContext());
        preferences.setFolderName(folderName);
    }

    /**
     * Triggers pick image
     *
     * @return
     */
    protected abstract String pick() throws PickerException;

    /**
     * This method should be called after {@link Activity#onActivityResult(int, int, Intent)} is  called.
     *
     * @param data
     */
    public abstract void submit(Intent data);



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected Context getContext() {
        if (activity != null) {
            return activity;
        } else if (fragment != null) {
            return fragment.getActivity();
        } else if (appFragment != null) {
            return appFragment.getActivity();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void pickInternal(Intent intent, int type) {
        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }
        if (activity != null) {
            activity.startActivityForResult(intent, type);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, type);
        } else if (appFragment != null) {
            appFragment.startActivityForResult(intent, type);
        }
    }

}
