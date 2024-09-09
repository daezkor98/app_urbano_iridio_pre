package com.urbanoexpress.iridio3.pe.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.urbanoexpress.iridio3.pe.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.reactivex.rxjava3.core.Single;

public class MultimediaManager {

    private final Context context;
    private File filePicture;
    private LocalDateTime creationDateTimePicture;

    public MultimediaManager(Context context) {
        this.context = context;
    }

    public void generateFileForTakePicture(String directoryNameParent, String imageNamePrefix) {
        creationDateTimePicture = LocalDateTime.now();
        filePicture = FileUtils.generateFile(context,
                generateImageName(imageNamePrefix, creationDateTimePicture),
                generateImagePath(directoryNameParent));
    }

    public Single<Picture> onSuccessResultTakingPicture() {
        return Single.create(emitter -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(filePicture.getAbsolutePath());
                bitmap = ImageRotator.rotateImageIfRequired(bitmap, filePicture.getAbsolutePath());
                try (FileOutputStream out = new FileOutputStream(filePicture)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
                }
                emitter.onSuccess(new Picture(filePicture, creationDateTimePicture));
            } catch (IOException | IllegalStateException ex) {
                ex.printStackTrace();
                emitter.onError(ex);
            }
        });
    }

    public Intent getIntentImageCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", filePicture);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(filePicture);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    private String generateImageName(String imageNamePrefix, LocalDateTime creationDateTimePicture) {
        String timeStamp = creationDateTimePicture.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String imageName = "Imagen_" + timeStamp + ".jpg";
        if (!imageNamePrefix.isEmpty()) {
            imageName = imageNamePrefix + "_" + timeStamp + ".jpg";
        }
        return imageName;
    }

    private String generateImagePath(String directoryNameParent) {
        return "Imagenes/" + directoryNameParent + "/";
    }

    public static class Picture {

        private File file;
        private LocalDateTime creationDateTime;

        public Picture(File file, LocalDateTime creationDateTime) {
            this.file = file;
            this.creationDateTime = creationDateTime;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public LocalDateTime getCreationDateTime() {
            return creationDateTime;
        }

        public void setCreationDateTime(LocalDateTime creationDateTime) {
            this.creationDateTime = creationDateTime;
        }
    }
}
