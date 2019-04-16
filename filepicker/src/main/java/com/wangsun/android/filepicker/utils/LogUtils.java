package com.wangsun.android.filepicker.utils;

import android.util.Log;

import com.wangsun.android.filepicker.core.PickerManager;


public class LogUtils {
    public static void d(String tag, String message){
        if(PickerManager.debugglable){
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if(PickerManager.debugglable){
            Log.e(tag, message);
        }
    }
}
