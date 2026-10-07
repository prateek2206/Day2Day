package com.example.day2day;


import android.widget.EditText;
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
import android.widget.ImageView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class DocumentsActivity extends AppCompatActivity {

    LinearLayout documentsContainer;
    DatabaseHelper databaseHelper;

    String section;
    int productId = -1;

    private static final int FILE_PICKER_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_documents);

        documentsContainer = findViewById(R.id.documentsContainer);

        FloatingActionButton addDocumentButton =
                findViewById(R.id.addDocumentButton);

        databaseHelper = new DatabaseHelper(this);

        section = getIntent().getStringExtra("section");

        if (section == null) {
            section = "MyDocuments";
        }
        if (section.equals("Product")) {
            productId = getIntent().getIntExtra("productId", -1);
        }

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
                    } else if (which == 1) {
                        openFilePicker("video/*");
                    } else if (which == 2) {
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
                                section,
                                fileName
                        );

                if (savedFile != null) {

                    // Save file information in database
                    if (section.equals("Product") && productId != -1) {

                        databaseHelper.addProductFile(
                                fileName,
                                fileType,
                                productId,
                                savedFile.getAbsolutePath()
                        );

                    } else {

                        databaseHelper.addImportedFile(
                                fileName,
                                fileType,
                                section,
                                savedFile.getAbsolutePath()
                        );
                    }

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


    private String getMimeType(String fileType) {

        if (fileType.equals("Photo")) {
            return "image/*";
        }

        if (fileType.equals("Video")) {
            return "video/*";
        }

        if (fileType.equals("PDF")) {
            return "application/pdf";
        }

        return "*/*";
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
        Cursor cursor;

        if (section.equals("Product") && productId != -1) {

            cursor = databaseHelper.getProductFiles(productId);

        } else {

            cursor = databaseHelper.getImportedFiles(section);
        }

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


                // SINGLE TAP → OPEN
                fileLayout.setOnClickListener(v -> {
                    openFile(filePath, fileType);
                });


                // LONG PRESS → OPEN / RENAME / SHARE / DELETE
                fileLayout.setOnLongClickListener(v -> {

                    Toast.makeText(
                            this,
                            "LONG PRESS WORKS!",
                            Toast.LENGTH_SHORT
                    ).show();

                    String[] options = {
                            "📖 Open",
                            "✏️ Rename",
                            "📤 Share",
                            "🗑️ Delete"
                    };

                    new AlertDialog.Builder(this)
                            .setTitle(fileName)
                            .setItems(options, (dialog, which) -> {

                                // OPEN
                                if (which == 0) {
                                    openFile(filePath, fileType);
                                }

                                // RENAME
                                else if (which == 1) {

                                    EditText input = new EditText(this);
                                    input.setText(fileName);
                                    input.setSelectAllOnFocus(true);

                                    new AlertDialog.Builder(this)
                                            .setTitle("Rename File")
                                            .setView(input)
                                            .setPositiveButton("Rename",
                                                    (renameDialog, renameWhich) -> {

                                                        String newName =
                                                                input.getText().toString().trim();

                                                        if (newName.isEmpty()) {
                                                            Toast.makeText(
                                                                    this,
                                                                    "Enter a file name",
                                                                    Toast.LENGTH_SHORT
                                                            ).show();
                                                            return;
                                                        }

                                                        File oldFile = new File(filePath);

                                                        String extension = "";
                                                        int dot = fileName.lastIndexOf(".");

                                                        if (dot >= 0) {
                                                            extension = fileName.substring(dot);
                                                        }

                                                        if (!newName.contains(".")) {
                                                            newName = newName + extension;
                                                        }

                                                        File newFile = new File(
                                                                oldFile.getParent(),
                                                                newName
                                                        );

                                                        if (oldFile.renameTo(newFile)) {

                                                            if (section.equals("Product") && productId != -1) {

                                                                databaseHelper.renameProductFile(
                                                                        filePath,
                                                                        newName,
                                                                        newFile.getAbsolutePath()
                                                                );

                                                            } else {

                                                                databaseHelper.renameImportedFile(
                                                                        filePath,
                                                                        newName,
                                                                        newFile.getAbsolutePath()
                                                                );
                                                            }
                                                            showDocuments();

                                                            Toast.makeText(
                                                                    this,
                                                                    "File renamed",
                                                                    Toast.LENGTH_SHORT
                                                            ).show();

                                                        } else {
                                                            Toast.makeText(
                                                                    this,
                                                                    "Unable to rename file",
                                                                    Toast.LENGTH_SHORT
                                                            ).show();
                                                        }
                                                    })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }

                                // SHARE
                                // SHARE
                                else if (which == 2) {

                                    File file = new File(filePath);

                                    if (!file.exists()) {
                                        Toast.makeText(
                                                this,
                                                "File not found",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                        return;
                                    }

                                    try {

                                        Uri fileUri = FileProvider.getUriForFile(
                                                this,
                                                getPackageName() + ".fileprovider",
                                                file
                                        );

                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);

                                        shareIntent.setType(getMimeType(fileType));

                                        shareIntent.putExtra(
                                                Intent.EXTRA_STREAM,
                                                fileUri
                                        );

                                        shareIntent.addFlags(
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        );

                                        shareIntent.addFlags(
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                        );

                                        startActivity(
                                                Intent.createChooser(
                                                        shareIntent,
                                                        "Share File"
                                                )
                                        );

                                    } catch (Exception e) {

                                        Toast.makeText(
                                                this,
                                                "Unable to share file",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }

                                // DELETE
                                else if (which == 3) {

                                    new AlertDialog.Builder(this)
                                            .setTitle("Delete File")
                                            .setMessage(
                                                    "Are you sure you want to delete\n\"" +
                                                            fileName + "\"?"
                                            )
                                            .setPositiveButton(
                                                    "Delete",
                                                    (deleteDialog, deleteWhich) -> {

                                                        File file =
                                                                new File(filePath);

                                                        if (file.exists()) {
                                                            file.delete();
                                                        }

                                                        if (section.equals("Product") && productId != -1) {

                                                            databaseHelper.deleteProductFile(
                                                                    filePath
                                                            );

                                                        } else {

                                                            databaseHelper.deleteImportedFile(
                                                                    filePath
                                                            );
                                                        }

                                                        showDocuments();

                                                        Toast.makeText(
                                                                this,
                                                                "File deleted",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                            )
                                            .setNegativeButton(
                                                    "Cancel",
                                                    null
                                            )
                                            .show();
                                }
                            })
                            .show();

                    // VERY IMPORTANT
                    return true;
                });


                documentsContainer.addView(fileLayout);
            }
        }


        cursor.close();


    }

    private void openFile(String filePath, String fileType) {

        File file = new File(filePath);

        if (!file.exists()) {
            Toast.makeText(
                    this,
                    "File not found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // PHOTO
        if (fileType.equals("Photo")) {

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            if (bitmap != null) {

                ImageView imageView = new ImageView(this);

                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(20, 20, 20, 20);

                new AlertDialog.Builder(this)
                        .setView(imageView)
                        .setPositiveButton("Close", null)
                        .show();

            } else {

                Toast.makeText(
                        this,
                        "Unable to open photo",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }

        // VIDEO / PDF
        else {

            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setDataAndType(
                    fileUri,
                    getMimeType(fileType)
            );

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            try {

                startActivity(intent);

            } catch (ActivityNotFoundException e) {

                Toast.makeText(
                        this,
                        "No app found to open this file",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}