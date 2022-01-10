package com.urbanoexpress.iridio.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by mick on 21/07/16.
 */
public class FileUtils {

    // buffer size used for reading and writing
    private static final int BUFFER_SIZE = 8192;

    public static String getBaseDirectoryPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return context.getExternalFilesDir(null).getAbsolutePath() + File.separator;
        } else {
            return Environment.getExternalStorageDirectory() + "/Iridio/";
        }
    }

    public static boolean existFile(String path) {
        File f = new File(path);
        return f.exists();
    }

    public static void makeDirs(String dirs) {
        File f = new File(dirs);
        f.mkdirs();
    }

    public static String makeDirectory(Context context, String dirs) {
        String baseDir = getBaseDirectoryPath(context) + dirs;

        if (!existFile(baseDir)) {
            makeDirs(baseDir);
        }

        return baseDir;
    }

    public static File generateFile(Context context, String fileName, String dirs) {
        String baseDir = makeDirectory(context, dirs);
        return new File(baseDir + fileName);
    }

    public static File generateFile(String path) {
        File file = new File(path);
        if (file.exists()) { return file; }
        return null;
    }

    public static byte[] readBitmapToByteArray(Bitmap bitmap, int compressQuality) {
        if (bitmap != null) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bos);
                return bos.toByteArray();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] readAllBytes(String path) {
        return readAllBytes(FileUtils.generateFile(path));
    }

    public static byte[] readAllBytes(File file) {
        if (file != null) {
            try {
                return readBytes(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static byte[] readBytes(File file) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Files.readAllBytes(file.toPath());
        } else {
            /*try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bos);
                return bos.toByteArray();
            }*/

            try (FileInputStream inputStream = new FileInputStream(file)) {
                int count;
                byte[] buffer = new byte[BUFFER_SIZE];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while ((count = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);
                }
                return outputStream.toByteArray();
            }
        }
    }

    public static boolean deleteFile(String path) {
        File file = generateFile(path);
        if (file != null) { return file.delete(); }
        return false;
    }

    public static boolean copyFile(String fromPath, String toPath, boolean preserveFromFile) {
        try {
            FileInputStream inputStream = new FileInputStream(fromPath);
            FileOutputStream outputStream = new FileOutputStream(toPath);

            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while (-1 != (count = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, count);
            }
            inputStream.close();
            outputStream.close();

            if (!preserveFromFile) {
                File file = generateFile(fromPath);
                file.delete();
            }
            return true;
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void deleteRecursively(File fileOrDirectory) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (fileOrDirectory != null) {
                try {
                    Path path = fileOrDirectory.toPath();
                    if (Files.exists(path)) {
                        Files.walk(path)
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                //.peek(System.out::println)
                                .forEach(File::delete);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (fileOrDirectory != null && fileOrDirectory.exists()) {
                if (fileOrDirectory.isDirectory()) {
                    File[] files = fileOrDirectory.listFiles();
                    if (files != null) {
                        Arrays.stream(files).forEach(FileUtils::deleteRecursively);
                    }
                }
                fileOrDirectory.delete();
            }
        }
    }

}
