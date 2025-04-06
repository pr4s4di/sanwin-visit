package com.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class BitmapUtils {
    public static Bitmap resizeImage(int maxWidth, int maxHeight, Bitmap bitmap, Matrix matrix)
    {
        while (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix , true);
        return bitmap;
    }
}
