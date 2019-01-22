package com.wangsun.custompicker.api;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import com.wangsun.custompicker.api.callbacks.FilePickerCallback;
import com.wangsun.custompicker.api.entity.ChosenFile;
import com.wangsun.custompicker.api.exceptions.PickerException;
import com.wangsun.custompicker.core.PickerManager;
import com.wangsun.custompicker.core.threads.FileProcessorThread;
import com.wangsun.custompicker.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Choose a file from your device. Gallery, Downloads, Dropbox etc.
 */
public class FilePicker extends PickerManager {
    private final static String TAG = FilePicker.class.getSimpleName();
    private FilePickerCallback callback;


    private String mimeTypes[] = {"*/*"};
    private String fileType = "*/*";

    /**
     * Constructor for choosing a file from an {@link Activity}
     * @param activity
     */
    public FilePicker(Activity activity) {
        super(activity, Picker.PICK_FILE);
    }

    /**
     * Constructor for choosing a file from a {@link Fragment}
     * @param fragment
     */
    public FilePicker(Fragment fragment) {
        super(fragment, Picker.PICK_FILE);
    }

    /**
     * Constructor for choosing a file from a {@link android.app.Fragment}
     * @param appFragment
     */
    public FilePicker(android.app.Fragment appFragment) {
        super(appFragment, Picker.PICK_FILE);
    }

    /**
     * Allow multiple files to be chosen. Default is false. This will only work for applications that support multiple file selection. Else, you will get only one result.
     */
    public FilePicker allowMultiple() {
        this.allowMultiple = true;
        return this;
    }

    /**
     * Listener which gets callbacks when your file is processed and ready to be used.
     * @param callback
     */
    public FilePicker setFilePickerCallback(FilePickerCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Default: All types of files. Set this value to a specific mimetype to pick.
     *
     * ex: application/pdf, application/xls
     * @param mimeType
     */
    public FilePicker setMimeTypes(String mimeType[]){
        this.mimeTypes = mimeType;
        return this;
    }

    public FilePicker setFileType(String fileType){
        this.fileType = fileType;
        return this;
    }


    /**
     * Triggers file selection
     */
    public void pickFile() {
        try {
            pick();
        } catch (PickerException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    @Override
    protected String pick() throws PickerException {
        if (callback == null) {
            throw new PickerException("FilePickerCallback is null!!! Please set one");
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(fileType);
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickInternal(intent, pickerType);
        return null;
    }

    /**
     * Call this method from
     * {@link Activity#onActivityResult(int, int, Intent)}
     * OR
     * {@link Fragment#onActivityResult(int, int, Intent)}
     * OR
     * {@link android.app.Fragment#onActivityResult(int, int, Intent)}
     * @param data
     */
    @Override
    public void submit(Intent data) {
        handleFileData(data);
    }

    private void handleFileData(Intent intent) {
        List<String> uris = new ArrayList<>();
        if (intent != null) {
            if (intent.getDataString() != null) {
                String uri = intent.getDataString();
                LogUtils.d(TAG, "handleFileData: " + uri);
                uris.add(uri);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    LogUtils.d(TAG, "handleFileData: Multiple files with ClipData");
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        LogUtils.d(TAG, "Item [" + i + "]: " + item.getUri().toString());
                        uris.add(item.getUri().toString());
                    }
                }
            }
            if (intent.hasExtra("uris")) {
                ArrayList<Uri> paths = intent.getParcelableArrayListExtra("uris");
                for (int i = 0; i < paths.size(); i++) {
                    uris.add(paths.get(i).toString());
                }
            }

            processFiles(uris);
        }
    }

    private void processFiles(List<String> uris) {
        FileProcessorThread thread = new FileProcessorThread(getContext(), getFileObjects(uris), cacheLocation);
        thread.setFilePickerCallback(callback);
        thread.setRequestId(requestId);
        thread.start();
    }

    private void onError(final String errorMessage) {
        try {
            if (callback != null) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(errorMessage);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private List<ChosenFile> getFileObjects(List<String> uris) {
        List<ChosenFile> files = new ArrayList<>();
        for (String uri : uris) {
            ChosenFile file = new ChosenFile();
            file.setQueryUri(uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                file.setDirectoryType(Environment.DIRECTORY_DOCUMENTS);
            } else {
                file.setDirectoryType(Environment.DIRECTORY_DOWNLOADS);
            }
            file.setType("file");
            files.add(file);
        }
        return files;
    }
}
