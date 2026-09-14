package com.example.day2day;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class DocumentsActivity extends AppCompatActivity {

    LinearLayout documentsContainer;
    DatabaseHelper databaseHelper;

    private static final int FILE_PICKER_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_documents);

        documentsContainer = findViewById(R.id.documentsContainer);

        FloatingActionButton addDocumentButton =
                findViewById(R.id.addDocumentButton);

        databaseHelper = new DatabaseHelper(this);

        addDocumentButton.setOnClickListener(v -> showImportOptions());
    }

    // ================= IMPORT OPTIONS =================

    private void showImportOptions() {

        String[] options = {
                "📷  Photo",
                "🎥  Video",
                "📄  PDF"
        };

        new AlertDialog.Builder(this)
                .setTitle("Import")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        openFilePicker("image/*");
                    }
                    else if (which == 1) {
                        openFilePicker("video/*");
                    }
                    else if (which == 2) {
                        openFilePicker("application/pdf");
                    }

                })
                .show();
    }

    // ================= FILE PICKER =================

    private void openFilePicker(String type) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType(type);

        startActivityForResult(
                intent,
                FILE_PICKER_REQUEST
        );
    }

    // ================= FILE SELECTED =================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == FILE_PICKER_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            Uri selectedFileUri = data.getData();

            if (selectedFileUri != null) {

                String fileName =
                        getFileName(selectedFileUri);

                String fileType =
                        getFileType(selectedFileUri);

                // Save file inside MyDocuments
                File savedFile =
                        FileStorageHelper.saveFile(
                                this,
                                selectedFileUri,
                                "MyDocuments",
                                fileName
                        );

                if (savedFile != null) {

                    // Save file information in database
                    databaseHelper.addImportedFile(
                            fileName,
                            fileType,
                            "MyDocuments",
                            savedFile.getAbsolutePath()
                    );

                    // Refresh screen
                    showDocuments();
                }
            }
        }
    }

    // ================= GET FILE NAME =================

    private String getFileName(Uri uri) {

        String fileName = "document";

        Cursor cursor =
                getContentResolver().query(
                        uri,
                        null,
                        null,
                        null,
                        null
                );

        if (cursor != null) {

            int nameIndex =
                    cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                    );

            if (nameIndex >= 0
                    && cursor.moveToFirst()) {

                fileName =
                        cursor.getString(nameIndex);
            }

            cursor.close();
        }

        return fileName;
    }

    // ================= GET FILE TYPE =================

    private String getFileType(Uri uri) {

        String mimeType =
                getContentResolver().getType(uri);

        if (mimeType != null) {

            if (mimeType.startsWith("image/")) {
                return "Photo";
            }

            if (mimeType.startsWith("video/")) {
                return "Video";
            }

            if (mimeType.equals("application/pdf")) {
                return "PDF";
            }
        }

        return "File";
    }

    // ================= SHOW DOCUMENTS =================

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            showDocuments();
        }
    }

    private void showDocuments() {

        documentsContainer.removeAllViews();

        // IMPORTANT:
        // Only show files belonging to MyDocuments
        Cursor cursor =
                databaseHelper.getImportedFiles(
                        "MyDocuments"
                );

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No documents added yet.\n\n" +
                            "Tap + to import a Photo, Video or PDF."
            );

            emptyText.setTextSize(16);

            emptyText.setPadding(
                    20,
                    30,
                    20,
                    30
            );

            documentsContainer.addView(
                    emptyText
            );

        } else {

            while (cursor.moveToNext()) {

                String fileName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_name"
                                )
                        );

                String fileType =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_type"
                                )
                        );

                String filePath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_path"
                                )
                        );

                LinearLayout fileLayout =
                        new LinearLayout(this);

                fileLayout.setOrientation(
                        LinearLayout.VERTICAL
                );

                fileLayout.setPadding(
                        20,
                        20,
                        20,
                        20
                );

                TextView document =
                        new TextView(this);

                document.setText(
                        "📄 " + fileName + "\n" +
                                "Type: " + fileType
                );

                document.setTextSize(17);

                fileLayout.addView(document);

                // Open file when tapped
                fileLayout.setOnClickListener(v -> {

                    try {

                        if (fileType.equals("Photo")) {

                            Bitmap bitmap =
                                    BitmapFactory.decodeFile(filePath);

                            if (bitmap != null) {

                                ImageView imageView =
                                        new ImageView(this);

                                imageView.setImageBitmap(bitmap);

                                imageView.setAdjustViewBounds(true);

                                imageView.setPadding(
                                        10,
                                        10,
                                        10,
                                        10
                                );

                                new AlertDialog.Builder(this)
                                        .setTitle(fileName)
                                        .setView(imageView)
                                        .setPositiveButton(
                                                "Close",
                                                null
                                        )
                                        .show();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Unable to load this photo",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Unable to open file",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

                documentsContainer.addView(fileLayout);
            }
        }

        cursor.close();
    }
}