package com.wangsun.android.filepicker.api;



/**
 * Cache Locations for the imported files, thumbnails of images, preview images of videos etc
 */
public interface CacheLocation {
    /**
     * Save files under your application's cache directory on the external storage
     * ex: /sdcard/Android/data/your_package/cache/
     * <p/>
     * These files will be ones that get deleted first when the device runs low on storage.
     * There is no guarantee when these files will be deleted.
     */
    int EXTERNAL_CACHE_DIR = 300;
    /**
     * Save files under your application's internal storage directory.
     */
    int INTERNAL_APP_DIR = 400;
}
