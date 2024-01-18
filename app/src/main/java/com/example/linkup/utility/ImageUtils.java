package com.example.linkup.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.linkup.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    /**
     * Load an image asynchronously into an ImageView using Picasso.
     *
     * @param imageUrl The URL of the image to load.
     * @param imageView The ImageView to load the image into.
     */
    public static void loadImageAsync(String imageUrl, ImageView imageView) {
        Picasso.get()
                .load(imageUrl)
                .into(imageView);
    }

    /**
     * Resize an image.
     *
     * @param imageData The image data as a byte array.
     * @param width The desired width.
     * @param height The desired height.
     * @return The resized Bitmap.
     */
    public static Bitmap resizeImage(byte[] imageData, int width, int height) {
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }

    /**
     * Convert a Bitmap to a byte array.
     *
     * @param bitmap The Bitmap to convert.
     * @return The byte array.
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Compress an image.
     *
     * @param image The Bitmap to compress.
     * @param quality The compression quality, 0-100.
     * @return The compressed image as a byte array.
     */
    public static byte[] compressImage(Bitmap image, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, quality, stream);
        return stream.toByteArray();
    }

    // Additional utility methods as needed...
}

