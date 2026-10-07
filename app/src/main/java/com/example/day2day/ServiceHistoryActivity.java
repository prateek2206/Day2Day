package com.example.day2day;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class ServiceHistoryActivity extends AppCompatActivity {

    LinearLayout serviceHistoryContainer;
    DatabaseHelper databaseHelper;

    int vehicleId;
    String vehicleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_history);

        serviceHistoryContainer =
                findViewById(R.id.serviceHistoryContainer);

        databaseHelper =
                new DatabaseHelper(this);

        vehicleId =
                getIntent().getIntExtra(
                        "vehicle_id",
                        -1
                );

        vehicleName =
                getIntent().getStringExtra(
                        "vehicle_name"
                );

        TextView subtitle =
                findViewById(R.id.serviceHistorySubtitle);

        if (vehicleName != null) {
            subtitle.setText(vehicleName);
        }

        Button addServiceButton =
                findViewById(R.id.addServiceButton);

        addServiceButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ServiceHistoryActivity.this,
                            AddServiceActivity.class
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

        showServiceHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showServiceHistory();
        }
    }

    private void showServiceHistory() {

        serviceHistoryContainer.removeAllViews();

        if (vehicleId == -1) {
            return;
        }

        Cursor cursor =
                databaseHelper.getVehicleServices(
                        vehicleId
                );

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No service history yet.\n" +
                            "Tap + Add Service to add a service record."
            );

            emptyText.setTextSize(16);

            emptyText.setPadding(
                    10,
                    20,
                    10,
                    20
            );

            serviceHistoryContainer.addView(
                    emptyText
            );

        } else {

            while (cursor.moveToNext()) {

                int serviceId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

                String serviceDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "service_date"
                                )
                        );

                String serviceCenter =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "service_center"
                                )
                        );

                String serviceDetails =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "service_details"
                                )
                        );

                String oldFilePath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_path"
                                )
                        );

                // Service layout
                LinearLayout serviceLayout =
                        new LinearLayout(this);

                serviceLayout.setOrientation(
                        LinearLayout.VERTICAL
                );

                serviceLayout.setPadding(
                        20,
                        20,
                        20,
                        20
                );

                // Service information
                TextView service =
                        new TextView(this);

                String serviceText =
                        "🛠 Service\n" +
                                "Date: " + serviceDate + "\n" +
                                "Center: " + serviceCenter + "\n" +
                                "Details: " +
                                (serviceDetails == null ||
                                        serviceDetails.isEmpty()
                                        ? "No details"
                                        : serviceDetails);

                service.setText(
                        serviceText
                );

                service.setTextSize(17);

                serviceLayout.addView(
                        service
                );

                // ================= NEW DOCUMENTS =================

                Cursor documentCursor =
                        databaseHelper
                                .getVehicleServiceDocuments(
                                        serviceId
                                );

                while (documentCursor.moveToNext()) {

                    long documentId =
                            documentCursor.getLong(
                                    documentCursor.getColumnIndexOrThrow(
                                            "id"
                                    )
                            );

                    String documentName =
                            documentCursor.getString(
                                    documentCursor.getColumnIndexOrThrow(
                                            "document_name"
                                    )
                            );

                    String documentType =
                            documentCursor.getString(
                                    documentCursor.getColumnIndexOrThrow(
                                            "document_type"
                                    )
                            );

                    String documentPath =
                            documentCursor.getString(
                                    documentCursor.getColumnIndexOrThrow(
                                            "file_path"
                                    )
                            );

                    File file =
                            new File(documentPath);

                    if (!file.exists()) {
                        continue;
                    }

                    TextView document =
                            new TextView(this);

                    document.setText(
                            "📎 " +
                                    documentName +
                                    " (" +
                                    documentType +
                                    ")"
                    );

                    document.setTextSize(15);

                    document.setPadding(
                            40,
                            12,
                            20,
                            12
                    );

                    // Tap → Open
                    document.setOnClickListener(v -> {

                        openFile(
                                documentPath
                        );
                    });

                    // Long press → actions
                    document.setOnLongClickListener(v -> {

                        showDocumentOptions(
                                documentId,
                                documentName,
                                documentPath
                        );

                        return true;
                    });

                    serviceLayout.addView(
                            document
                    );
                }

                documentCursor.close();

                // ================= OLD FILE SUPPORT =================

                // This keeps old service records working.
                if (oldFilePath != null &&
                        !oldFilePath.isEmpty()) {

                    File oldFile =
                            new File(oldFilePath);

                    if (oldFile.exists()) {

                        TextView oldDocument =
                                new TextView(this);

                        oldDocument.setText(
                                "📎 " +
                                        oldFile.getName()
                        );

                        oldDocument.setTextSize(15);

                        oldDocument.setPadding(
                                40,
                                12,
                                20,
                                12
                        );

                        oldDocument.setOnClickListener(v -> {

                            openFile(
                                    oldFilePath
                            );
                        });

                        oldDocument.setOnLongClickListener(v -> {

                            String[] options = {
                                    "Open",
                                    "Share"
                            };

                            new AlertDialog.Builder(this)
                                    .setTitle(
                                            "Document"
                                    )
                                    .setItems(
                                            options,
                                            (dialog, which) -> {

                                                if (which == 0) {

                                                    openFile(
                                                            oldFilePath
                                                    );

                                                } else {

                                                    shareFile(
                                                            oldFilePath
                                                    );
                                                }
                                            }
                                    )
                                    .show();

                            return true;
                        });

                        serviceLayout.addView(
                                oldDocument
                        );
                    }
                }

                // Long press service itself
                service.setOnLongClickListener(v -> {

                    String[] options = {
                            "Delete Service"
                    };

                    new AlertDialog.Builder(this)
                            .setTitle("Service")
                            .setItems(
                                    options,
                                    (dialog, which) -> {

                                        if (which == 0) {

                                            deleteService(
                                                    serviceId,
                                                    oldFilePath
                                            );
                                        }
                                    }
                            )
                            .show();

                    return true;
                });

                serviceHistoryContainer.addView(
                        serviceLayout
                );
            }
        }

        cursor.close();
    }

    // ================= DOCUMENT OPTIONS =================

    private void showDocumentOptions(
            long documentId,
            String documentName,
            String filePath) {

        String[] options = {
                "Open",
                "Share",
                "Delete"
        };

        new AlertDialog.Builder(this)
                .setTitle(documentName)
                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {

                                openFile(
                                        filePath
                                );

                            } else if (which == 1) {

                                shareFile(
                                        filePath
                                );

                            } else {

                                deleteDocument(
                                        documentId,
                                        filePath
                                );
                            }
                        }
                )
                .show();
    }

    // ================= OPEN FILE =================

    private void openFile(String filePath) {

        if (filePath == null ||
                filePath.isEmpty()) {

            Toast.makeText(
                    this,
                    "No file attached",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

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

        Uri fileUri =
                FileProvider.getUriForFile(
                        this,
                        getPackageName()
                                + ".fileprovider",
                        file
                );

        Intent intent =
                new Intent(
                        Intent.ACTION_VIEW
                );

        String mimeType =
                getContentResolver()
                        .getType(fileUri);

        if (mimeType == null) {
            mimeType = "*/*";
        }

        intent.setDataAndType(
                fileUri,
                mimeType
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
    }

    // ================= DELETE DOCUMENT =================

    private void deleteDocument(
            long documentId,
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

                            if (filePath != null &&
                                    !filePath.isEmpty()) {

                                File file =
                                        new File(filePath);

                                if (file.exists()) {
                                    file.delete();
                                }
                            }

                            databaseHelper
                                    .deleteVehicleServiceDocument(
                                            documentId
                                    );

                            showServiceHistory();

                            Toast.makeText(
                                    this,
                                    "Document deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    // ================= DELETE SERVICE =================

    private void deleteService(
            int serviceId,
            String oldFilePath) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Service?")
                .setMessage(
                        "Are you sure you want to delete this service and all its documents?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            // Delete old single file
                            if (oldFilePath != null &&
                                    !oldFilePath.isEmpty()) {

                                File oldFile =
                                        new File(oldFilePath);

                                if (oldFile.exists()) {
                                    oldFile.delete();
                                }
                            }

                            // Delete all new service documents
                            Cursor cursor =
                                    databaseHelper
                                            .getVehicleServiceDocuments(
                                                    serviceId
                                            );

                            while (cursor.moveToNext()) {

                                String filePath =
                                        cursor.getString(
                                                cursor.getColumnIndexOrThrow(
                                                        "file_path"
                                                )
                                        );

                                if (filePath != null &&
                                        !filePath.isEmpty()) {

                                    File file =
                                            new File(filePath);

                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }
                            }

                            cursor.close();

                            // Delete document rows
                            databaseHelper
                                    .deleteVehicleServiceDocuments(
                                            serviceId
                                    );

                            // Delete service
                            databaseHelper
                                    .deleteVehicleService(
                                            serviceId
                                    );

                            showServiceHistory();

                            Toast.makeText(
                                    this,
                                    "Service deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    // ================= SHARE FILE =================

    private void shareFile(String filePath) {

        if (filePath == null ||
                filePath.isEmpty()) {

            Toast.makeText(
                    this,
                    "No file attached",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

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
                    new Intent(
                            Intent.ACTION_SEND
                    );

            String mimeType =
                    getContentResolver()
                            .getType(fileUri);

            if (mimeType == null) {
                mimeType = "*/*";
            }

            shareIntent.setType(
                    mimeType
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
}