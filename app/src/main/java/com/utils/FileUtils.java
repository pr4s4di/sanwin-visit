package com.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        String imagePath = tempDir.getAbsolutePath() + "/.temp/";
        tempDir = new File(imagePath);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        Log.d("Location", imagePath);
        return File.createTempFile(part, ext, tempDir);
    }

    public static String getTempLocation(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        String path = tempDir.getAbsolutePath() + "/.temp/" + part + ext;
        tempDir = new File(path);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        Log.d("Location", path);
        return path;
    }



    public static String getRealPathFromURI(Context context, Uri uri) {
        String path = "";
        if (context.getContentResolver() != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
}
