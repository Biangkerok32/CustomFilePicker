package com.wangsun.custompicker.core.threads;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import com.wangsun.custompicker.api.CacheLocation;
import com.wangsun.custompicker.api.callbacks.FilePickerCallback;
import com.wangsun.custompicker.api.entity.ChosenFile;
import com.wangsun.custompicker.api.exceptions.PickerException;
import com.wangsun.custompicker.utils.FileUtils;
import com.wangsun.custompicker.utils.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.wangsun.custompicker.utils.StreamHelper.*;


public class FileProcessorThread extends Thread {
    private final static String TAG = FileProcessorThread.class.getSimpleName();
    private final int cacheLocation;
    private final Context context;
    private final List<ChosenFile> files;  //List<? extends ChosenFile> files;
    private FilePickerCallback callback;

    private int requestId;

    public FileProcessorThread(Context context, List<ChosenFile> files, int cacheLocation) {
        this.context = context;
        this.files = files;
        this.files.size();
        this.cacheLocation = cacheLocation;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @Override
    public void run() {
        processFiles();
        if (callback != null) {
            onDone();
        }
    }

    private void onDone() {
        try {
            if (callback != null) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFilesChosen(files); //(List<ChosenFile>)
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void processFiles() {
        for (ChosenFile file : files) {
            try {
                file.setRequestId(requestId);
                LogUtils.d(TAG, "processFile: Before: " + file.toString());
                processFile(file);
                postProcess(file);
                file.setSuccess(true);
                LogUtils.d(TAG, "processFile: Final Path: " + file.toString());
            } catch (PickerException e) {
                e.printStackTrace();
                file.setSuccess(false);
            }
        }
    }


    private void postProcess(ChosenFile file) throws PickerException {
        file.setCreatedAt(Calendar.getInstance().getTime());
        File f = new File(file.getOriginalPath());
        file.setSize(f.length());
        //copyFileToFolder(file);
    }


    private void processFile(ChosenFile file) throws PickerException {
        String uri = file.getQueryUri();
        LogUtils.d(TAG, "processFile: uri" + uri);
        if (uri.startsWith("file://") || uri.startsWith("/")) {
            file = sanitizeUri(file);
            file.setDisplayName(Uri.parse(file.getOriginalPath()).getLastPathSegment());
        } else if (uri.startsWith("http")) {
            file = downloadAndSaveFile(file);
        } else if (uri.startsWith("content:")) {
            file = getAbsolutePathIfAvailable(file);
        }
        uri = file.getOriginalPath();
        // Still content:: Try ContentProvider stream import
        if (uri.startsWith("content:")) {
            file = getFromContentProvider(file);
        }
        uri = file.getOriginalPath();
        // Still content:: Try ContentProvider stream import alternate
        if (uri.startsWith("content:")) {
            file = getFromContentProviderAlternate(file);
        }

        // Check for URL Encoded file paths
        try {
            String decodedURL = Uri.parse(Uri.decode(file.getOriginalPath())).toString();
            if (!decodedURL.equals(file.getOriginalPath())) {
                file.setOriginalPath(decodedURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // If starts with file: (For some content providers, remove the file prefix)
    private ChosenFile sanitizeUri(ChosenFile file) {
        if (file.getQueryUri().startsWith("file://")) {
            file.setOriginalPath(file.getQueryUri().substring(7));
        }
        return file;
    }

    protected ChosenFile getFromContentProviderAlternate(ChosenFile file) throws PickerException {
        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;

        try {
            InputStream inputStream = context.getContentResolver()
                    .openInputStream(Uri.parse(file.getOriginalPath()));

            bStream = new BufferedInputStream(inputStream);

            verifyStream(file.getOriginalPath(), bStream);

            String localFilePath = generateFileName(file);

            outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            file.setOriginalPath(localFilePath);
        } catch (IOException e) {
            throw new PickerException(e);
        } finally {
            flush(outStream);
            close(bStream);
            close(outStream);
        }

        return file;
    }

    protected ChosenFile getFromContentProvider(ChosenFile file) throws PickerException {

        BufferedInputStream inputStream = null;
        BufferedOutputStream outStream = null;
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            String localFilePath = generateFileName(file);
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(Uri.parse(file.getOriginalPath()), "r");
            verifyStream(file.getOriginalPath(), parcelFileDescriptor);

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            inputStream = new BufferedInputStream(new FileInputStream(fileDescriptor));
            BufferedInputStream reader = new BufferedInputStream(inputStream);

            outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
            byte[] buf = new byte[2048];
            int len;
            while ((len = reader.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            flush(outStream);
            file.setOriginalPath(localFilePath);
        } catch (IOException e) {
            throw new PickerException(e);
        } catch (final Exception e) {
            throw new PickerException(e.getLocalizedMessage());
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                close(parcelFileDescriptor);
            }
            flush(outStream);
            close(outStream);
            close(inputStream);
        }
        return file;
    }


    // Try to get a local copy if available
    private ChosenFile getAbsolutePathIfAvailable(ChosenFile file) {
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE};

        // Workaround for various implementations for Google Photos/Picasa
        if (file.getQueryUri().startsWith(
                "content://com.android.gallery3d.provider")) {
            file.setOriginalPath(Uri.parse(file.getQueryUri().replace(
                    "com.android.gallery3d", "com.google.android.gallery3d")).toString());
        } else {
            file.setOriginalPath(file.getQueryUri());
        }

        // Try to see if there's a cached local copy that is available
        if (file.getOriginalPath().startsWith("content://")) {
            try {
                Cursor cursor = context.getContentResolver().query(Uri.parse(file.getOriginalPath()), projection,
                        null, null, null);
                cursor.moveToFirst();
                try {
                    // Samsung Bug
                    if (!file.getOriginalPath().contains("com.sec.android.gallery3d.provider")) {
                        String path = cursor.getString(cursor
                                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                        LogUtils.d(TAG, "processFile: Path: " + path);
                        if (path != null) {
                            file.setOriginalPath(path);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                    if (displayName != null) {
                        file.setDisplayName(displayName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Check if DownloadsDocument in which case, we can get the local copy by using the content provider
        if (file.getOriginalPath().startsWith("content:") && isDownloadsDocument(Uri.parse(file.getOriginalPath()))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] data = getPathAndMimeType(file);
                if (data[0] != null) {
                    file.setOriginalPath(data[0]);
                }
            }
        }

        return file;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String[] getPathAndMimeType(ChosenFile file) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Uri uri = Uri.parse(file.getOriginalPath());
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    String[] data = new String[2];
                    data[0] = id.replaceFirst("raw:", "");
                    data[1] = null;
                    return data;
                }
                Uri contentUri = uri;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                }
                return getDataAndMimeType(contentUri, null, null, file.getType());
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataAndMimeType(contentUri, selection, selectionArgs, file.getType());
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataAndMimeType(uri, null, null, file.getType());
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            String path = uri.getPath();
            String[] data = new String[2];
            data[0] = path;
            return data;
        }

        return null;
    }

    private String[] getDataAndMimeType(Uri uri, String selection,
                                        String[] selectionArgs, String type) {
        String[] data = new String[2];
        Cursor cursor = null;
        String[] projection = {MediaStore.MediaColumns.DATA};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                data[0] = path;
                return data;
            }
        } catch (Exception e) {
            data[0] = uri.toString();
            return data;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private ChosenFile downloadAndSaveFile(ChosenFile file) {
        String localFilePath;
        try {
            URL u = new URL(file.getQueryUri());
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedInputStream bStream = new BufferedInputStream(stream);

            localFilePath = generateFileName(file);

            File localFile = new File(localFilePath);

            FileOutputStream fileOutputStream = new FileOutputStream(localFile);

            byte[] buffer = new byte[2048];
            int len;
            while ((len = bStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            bStream.close();
            file.setOriginalPath(localFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    protected String getTargetDirectory(String type) throws PickerException {
        String directory = null;
        switch (cacheLocation) {
            case CacheLocation.EXTERNAL_CACHE_DIR:
                directory = FileUtils.getExternalCacheDir(context);
                break;
            case CacheLocation.INTERNAL_APP_DIR:
                directory = FileUtils.getInternalFileDirectory(context);
                break;
            default:
                directory = FileUtils.getExternalCacheDir(context);
                break;
        }
        return directory;
    }

    private String generateFileName(ChosenFile file) throws PickerException {
        String fileName = file.getDisplayName();
        if (fileName == null || fileName.isEmpty()) {
            fileName = UUID.randomUUID().toString();
        }
        // If File name already contains an extension, we don't need to guess the extension

        String probableFileName = fileName;
        File probableFile = new File(getTargetDirectory(file.getDirectoryType()) + File.separator
                + probableFileName);
        int counter = 0;
        while (probableFile.exists()) {
            counter++;
            if (fileName.contains(".")) {
                int indexOfDot = fileName.lastIndexOf(".");
                probableFileName = fileName.substring(0, indexOfDot - 1) + "-" + counter + "." + fileName.substring(indexOfDot + 1);
            } else {
                probableFileName = fileName + "(" + counter + ")";
            }
            probableFile = new File(getTargetDirectory(file.getDirectoryType()) + File.separator
                    + probableFileName);
        }
        fileName = probableFileName;

        file.setDisplayName(fileName);

        return getTargetDirectory(file.getDirectoryType()) + File.separator + fileName;
    }

    public void setFilePickerCallback(FilePickerCallback callback) {
        this.callback = callback;
    }
}
