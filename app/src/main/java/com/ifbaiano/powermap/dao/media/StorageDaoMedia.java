package com.ifbaiano.powermap.dao.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.firebase.storage.StorageReference;
import com.ifbaiano.powermap.dao.contracts.StorageDao;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class StorageDaoMedia implements StorageDao {
    private final Context ctx;

    public StorageDaoMedia(Context ctx) {
        this.ctx = ctx;
    }

    public String add(byte[] imageData, String child) {
        String title = UUID.randomUUID().toString();
        return putImage(imageData, title);
    }

    @Override
    public String putImage(byte[] imageData, String child) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        try {
            Uri savedImageUri = saveBitmap(this.ctx, bitmap, Bitmap.CompressFormat.PNG, "image/png", child);
            if (savedImageUri != null) {
                return savedImageUri.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public void transformInBitmap(String path, ImageView imageView, ProgressBar progressBar)  {
      try{
          Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.ctx.getContentResolver() , Uri.parse(path));
          if(bitmap != null){
              progressBar.setVisibility(View.GONE);
              imageView.setImageBitmap(bitmap);

          }
      }
      catch (IOException e){
          Log.e("StorageImg", "Error downloading image URI: " + e.getMessage());
      }
    }
    @Override
    public Boolean remove(String child) {
        return null;
    }

    public Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                          @NonNull final Bitmap.CompressFormat format,
                          @NonNull final String mimeType,
                          @NonNull final String displayName) throws IOException {

        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");

            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");

                if (!bitmap.compress(format, 95, stream))
                    throw new IOException("Failed to save bitmap.");
            }

            return uri;
        }
        catch (IOException e) {

            if (uri != null) {
                resolver.delete(uri, null, null);
            }

           return null;
        }
    }
}
