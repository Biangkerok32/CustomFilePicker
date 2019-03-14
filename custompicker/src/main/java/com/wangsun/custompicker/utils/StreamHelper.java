package com.wangsun.custompicker.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.wangsun.custompicker.api.exceptions.PickerException;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class StreamHelper {

    static final String TAG = StreamHelper.class.getSimpleName();


    public static void close(Closeable stream) throws PickerException {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                throw new PickerException(e);
            }
        }
    }

    public static void flush(OutputStream stream) throws PickerException {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                throw new PickerException(e);
            }
        }
    }


    public static void verifyStream(String path, ParcelFileDescriptor descriptor) throws PickerException {
        if (descriptor == null) {
            throw new PickerException("Could not read file descriptor from file at path = " + path);
        }
    }

    public static void verifyStream(String path, InputStream is) throws PickerException {
        if (is == null) {
            throw new PickerException("Could not open stream to read path = " + path);
        }
    }
}
