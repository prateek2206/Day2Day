package com.example.day2day;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class AddMedicalDocumentActivity extends AppCompatActivity {

    private static final int FILE_PICKER_REQUEST = 200;

    int conditionId;

    EditText documentType;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        conditionId =
                getIntent().getIntExtra(
                        "condition_id",
                        -1
                );

        databaseHelper =
                new DatabaseHelper(this);

        showDocumentTypeDialog();
    }

    private void showDocumentTypeDialog() {

        final String[] types = {
                "📄 Prescription",
                "🩻 X-Ray",
                "🧲 MRI",
                "🧪 Lab Report",
                "💉 Vaccination",
                "📋 Other"
        };

        new AlertDialog.Builder(this)
                .setTitle("Document Type")
                .setItems(
                        types,
                        (dialog, which) -> {

                            String selectedType =
                                    types[which];

                            selectedType =
                                    selectedType.substring(
                                            selectedType.indexOf(" ") + 1
                                    );

                            chooseFile(selectedType);
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> finish()
                )
                .show();
    }

    private void chooseFile(String type) {

        Intent intent =
                new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("*/*");

        intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                new String[]{
                        "image/*",
                        "video/*",
                        "application/pdf"
                }
        );

        documentType =
                new EditText(this);

        documentType.setText(type);

        startActivityForResult(
                intent,
                FILE_PICKER_REQUEST
        );
    }

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

        if (requestCode == FILE_PICKER_REQUEST &&
                resultCode == RESULT_OK &&
                data != null) {

            Uri selectedUri =
                    data.getData();

            if (selectedUri == null) {
                return;
            }

            String fileName =
                    getFileName(selectedUri);

            if (fileName == null) {
                fileName = "Medical Document";
            }

            String fileType =
                    getContentType(selectedUri);

            File savedFile =
                    FileStorageHelper.saveFile(
                            this,
                            selectedUri,
                            "MedicalDocuments",
                            fileName
                    );

            if (savedFile != null) {

                boolean inserted =
                        databaseHelper.addMedicalDocument(
                                conditionId,
                                documentType.getText()
                                        .toString(),
                                fileName,
                                savedFile.getAbsolutePath()
                        );

                if (inserted) {

                    Toast.makeText(
                            this,
                            "Medical document added",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            this,
                            "Failed to save document",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } else {

                Toast.makeText(
                        this,
                        "Failed to save file",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private String getFileName(Uri uri) {

        String result = null;

        if ("content".equals(uri.getScheme())) {

            Cursor cursor =
                    getContentResolver()
                            .query(
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

                if (nameIndex >= 0 &&
                        cursor.moveToFirst()) {

                    result =
                            cursor.getString(
                                    nameIndex
                            );
                }

                cursor.close();
            }
        }

        return result;
    }

    private String getContentType(Uri uri) {

        String type =
                getContentResolver()
                        .getType(uri);

        if (type == null) {
            return "Unknown";
        }

        if (type.startsWith("image/")) {
            return "Photo";
        }

        if (type.startsWith("video/")) {
            return "Video";
        }

        if (type.equals("application/pdf")) {
            return "PDF";
        }

        return "File";
    }
}