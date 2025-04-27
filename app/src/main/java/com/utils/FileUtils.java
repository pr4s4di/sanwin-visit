package com.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtils { // Renamed class for clarity

    private static final String TAG = "TempFileUtil";

    /**
     * Creates a temporary file directly within the application's internal cache directory.
     * The cache directory path is typically /data/data/your.package.name/cache/
     *
     * @param context The application context (must not be null).
     * @param prefix  The prefix string for the file name (at least 3 characters).
     * @param suffix  The suffix string for the file name (e.g., ".tmp", ".jpg"). Can be null.
     * @return The newly created temporary File object.
     * @throws IOException              If the file cannot be created, the context is invalid,
     *                                  or the cache directory is not accessible/writable.
     * @throws IllegalArgumentException If the prefix is less than 3 characters long.
     */
    public static File createTemporaryFile(Context context, String prefix, String suffix) throws IOException {
        if (context == null) {
            Log.e(TAG, "Context provided is NULL. Cannot create temporary file.");
            throw new IOException("Context is null");
        }

        // 1. Get the app's specific cache directory
        File cacheDir = context.getCacheDir();
        if (cacheDir == null) {
            Log.e(TAG, "Failed to get cache directory. Check Context validity and app setup.");
            throw new IOException("context.getCacheDir() returned null");
        }
        Log.d(TAG, "Using cache directory: " + cacheDir.getAbsolutePath());

        // 2. Check if the directory exists and is writable
        //    (getCacheDir() should normally handle creation, but checking is good practice)
        if (!cacheDir.exists()) {
            Log.w(TAG, "Cache directory does not exist, attempting creation: " + cacheDir.getAbsolutePath());
            if (!cacheDir.mkdirs()) {
                Log.e(TAG, "Failed to create cache directory: " + cacheDir.getAbsolutePath());
                throw new IOException("Cache directory does not exist and could not be created.");
            }
        } else if (!cacheDir.isDirectory()) {
            Log.e(TAG, "Cache directory path exists but is not a directory: " + cacheDir.getAbsolutePath());
            throw new IOException("Cache path is not a directory.");
        } else if (!cacheDir.canWrite()) {
            Log.e(TAG, "Cache directory is not writable: " + cacheDir.getAbsolutePath());
            throw new IOException("Cache directory not writable.");
        }

        // 3. Create the temporary file directly in the cache directory
        File tempFile = File.createTempFile(prefix, suffix, cacheDir); // Pass cacheDir directly

        Log.d(TAG, "Successfully created temporary file: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * Gets the potential path for a temporary file within the app's cache directory.
     * Does NOT create the file.
     *
     * @param context The application context.
     * @param prefix  Prefix for the potential file name.
     * @param suffix  Suffix for the potential file name.
     * @return The potential absolute path string, or null if context or cache dir is invalid.
     */
    public static String getTempLocation(Context context, String prefix, String suffix) {
        if (context == null) {
            Log.e(TAG, "Context provided is NULL. Cannot get temp location.");
            return null;
        }
        File cacheDir = context.getCacheDir();
        if (cacheDir == null) {
            Log.e(TAG, "Failed to get cache directory for temp location.");
            return null;
        }
        // Construct path without creating file/dir
        File potentialFile = new File(cacheDir, prefix + suffix);
        Log.d(TAG, "Potential cache file location: " + potentialFile.getAbsolutePath());
        return potentialFile.getAbsolutePath();
    }
}