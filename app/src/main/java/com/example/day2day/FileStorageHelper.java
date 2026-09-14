package com.example.day2day;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileStorageHelper {

    public static File saveFile(
            Context context,
            Uri uri,
            String section,
            String fileName
    ) {

        try {
            // Main Day2Day folder
            File mainFolder = new File(
                    context.getFilesDir(),
                    "Day2Day"
            );

            // Section folder
            File sectionFolder = new File(
                    mainFolder,
                    section
            );

            // Create folders if they don't exist
            if (!sectionFolder.exists()) {
                sectionFolder.mkdirs();
            }

            // Create the destination file
            File destinationFile = new File(
                    sectionFolder,
                    fileName
            );

            // Read selected file
            InputStream inputStream =
                    context.getContentResolver().openInputStream(uri);

            // Write file into internal storage
            FileOutputStream outputStream =
                    new FileOutputStream(destinationFile);

            byte[] buffer = new byte[4096];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return destinationFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}