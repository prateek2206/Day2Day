package com.example.day2day;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.EditText;

import androidx.core.content.FileProvider;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class VehicleDocumentsActivity extends AppCompatActivity {

    LinearLayout vehicleDocumentsContainer;
    DatabaseHelper databaseHelper;

    int vehicleId;
    String vehicleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle_documents);



        vehicleDocumentsContainer =
                findViewById(R.id.vehicleDocumentsContainer);

        databaseHelper = new DatabaseHelper(this);

        // Get selected vehicle information
        vehicleId = getIntent().getIntExtra("vehicle_id", -1);
        vehicleName = getIntent().getStringExtra("vehicle_name");

        TextView subtitle =
                findViewById(R.id.vehicleDocumentSubtitle);

        if (vehicleName != null) {
            subtitle.setText(vehicleName);
        }

        showDocuments();

        Button addVehicleDocumentButton =
                findViewById(R.id.addVehicleDocumentButton);

        addVehicleDocumentButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    VehicleDocumentsActivity.this,
                    AddVehicleDocumentActivity.class
            );

            intent.putExtra("vehicle_id", vehicleId);

            startActivity(intent);
        });

        Button serviceHistoryButton =
                findViewById(R.id.serviceHistoryButton);

        serviceHistoryButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    VehicleDocumentsActivity.this,
                    ServiceHistoryActivity.class
            );

            intent.putExtra(
                    "vehicle_id",
                    vehicleId
            );

            intent.putExtra(
                    "vehicle_name",
                    vehicleName
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showDocuments();
        }
    }

    private void showDocuments() {

        vehicleDocumentsContainer.removeAllViews();

        if (vehicleId == -1) {
            return;
        }

        Cursor cursor =
                databaseHelper.getVehicleDocuments(vehicleId);

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText(
                    "No documents added yet.\n" +
                            "Tap + Add Document to add RC, Insurance, PUC or other documents."
            );

            emptyText.setTextSize(16);
            emptyText.setPadding(10, 10, 10, 10);

            vehicleDocumentsContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                String documentType =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "document_type"
                                )
                        );

                String documentName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "document_name"
                                )
                        );

                String expiryDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "expiry_date"
                                )
                        );

                String filePath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_path"
                                )
                        );

                TextView document = new TextView(this);

                StringBuilder documentText = new StringBuilder();

                documentText.append("📄 ")
                        .append(documentType)
                        .append("\n")
                        .append(documentName);

                if (expiryDate != null && !expiryDate.trim().isEmpty()) {
                    documentText.append("\nExpiry: ")
                            .append(expiryDate);
                }

                document.setText(documentText.toString());

                document.setTextSize(17);
                document.setPadding(20, 20, 20, 20);

                document.setOnClickListener(v -> {

                    File file = new File(filePath);

                    if (!file.exists()) {

                        Toast.makeText(
                                this,
                                "File not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Uri fileUri = FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".fileprovider",
                            file
                    );

                    Intent intent = new Intent(
                            Intent.ACTION_VIEW
                    );

                    intent.setDataAndType(
                            fileUri,
                            getContentResolver().getType(fileUri)
                    );

                    intent.addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

                    try {

                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {

                        Toast.makeText(
                                this,
                                "No app available to open this file",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

                document.setOnLongClickListener(v -> {

                    String[] options = {
                            "Rename",
                            "Delete",
                            "Share"
                    };

                    new AlertDialog.Builder(
                            VehicleDocumentsActivity.this
                    )
                            .setTitle(documentName)
                            .setItems(
                                    options,
                                    (dialog, which) -> {

                                        if (which == 0) {

                                            showRenameDocumentDialog(
                                                    documentName,
                                                    filePath
                                            );

                                        } else if (which == 1) {

                                            deleteDocument(filePath);

                                        } else if (which == 2) {

                                            shareDocument(
                                                    filePath
                                            );
                                        }
                                    }
                            )
                            .show();

                    return true;
                });

                vehicleDocumentsContainer.addView(document);
            }
        }

        cursor.close();
    }

    private void showRenameDocumentDialog(
            String oldName,
            String filePath) {

        EditText input =
                new EditText(this);

        input.setText(oldName);
        input.setSelectAllOnFocus(true);

        new AlertDialog.Builder(this)
                .setTitle("Rename Document")
                .setView(input)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Rename",
                        (dialog, which) -> {

                            String newName =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (newName.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        "Name cannot be empty",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            File oldFile =
                                    new File(filePath);

                            if (!oldFile.exists()) {
                                Toast.makeText(
                                        this,
                                        "File not found",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            String extension = "";

                            int dot =
                                    oldName.lastIndexOf(".");

                            if (dot >= 0) {
                                extension =
                                        oldName.substring(dot);
                            }

                            if (!newName.contains(".")) {
                                newName += extension;
                            }

                            File newFile =
                                    new File(
                                            oldFile.getParent(),
                                            newName
                                    );

                            if (oldFile.renameTo(newFile)) {

                                databaseHelper.renameVehicleDocument(
                                        filePath,
                                        newName,
                                        newFile.getAbsolutePath()
                                );

                                showDocuments();

                                Toast.makeText(
                                        this,
                                        "Document renamed",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Unable to rename document",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }


    private void deleteDocument(
            String filePath) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Document?")
                .setMessage(
                        "Are you sure you want to delete this document?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            File file =
                                    new File(filePath);

                            if (file.exists()) {
                                file.delete();
                            }

                            databaseHelper.deleteVehicleDocument(
                                    filePath
                            );

                            showDocuments();

                            Toast.makeText(
                                    this,
                                    "Document deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    private void shareDocument(
            String filePath) {

        File file =
                new File(filePath);

        if (!file.exists()) {

            Toast.makeText(
                    this,
                    "File not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Uri fileUri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            file
                    );

            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);

            shareIntent.setType(
                    getContentResolver()
                            .getType(fileUri)
            );

            shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    fileUri
            );

            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share Document"
                    )
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to share document",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}