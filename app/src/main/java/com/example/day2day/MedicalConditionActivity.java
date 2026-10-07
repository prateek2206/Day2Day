package com.example.day2day;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class MedicalConditionActivity extends AppCompatActivity {

    LinearLayout medicalDocumentsContainer;
    DatabaseHelper databaseHelper;

    int conditionId;
    String conditionName;
    String personName;
    int personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medical_condition);

        conditionId =
                getIntent().getIntExtra("condition_id", -1);

        personId =
                getIntent().getIntExtra("person_id", -1);

        conditionName =
                getIntent().getStringExtra("condition_name");

        personName =
                getIntent().getStringExtra("person_name");

        TextView title =
                findViewById(R.id.medicalConditionTitle);

        TextView subtitle =
                findViewById(R.id.medicalConditionSubtitle);

        Button addDocumentButton =
                findViewById(R.id.addMedicalDocumentButton);

        Button appointmentButton =
                findViewById(R.id.updateAppointmentButton);

        medicalDocumentsContainer =
                findViewById(R.id.medicalDocumentsContainer);

        databaseHelper =
                new DatabaseHelper(this);

        title.setText(conditionName);

        subtitle.setText(
                personName + " • Medical Condition"
        );

        addDocumentButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MedicalConditionActivity.this,
                            AddMedicalDocumentActivity.class
                    );

            intent.putExtra(
                    "condition_id",
                    conditionId
            );

            startActivity(intent);
        });

        appointmentButton.setOnClickListener(v ->
                showAppointmentPicker()
        );

        showAppointment();
        showMedicalDocuments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showAppointment();
            showMedicalDocuments();
        }
    }

    private void showMedicalDocuments() {

        medicalDocumentsContainer.removeAllViews();

        Cursor cursor =
                databaseHelper.getMedicalDocuments(
                        conditionId
                );

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No medical documents added yet."
            );

            emptyText.setTextSize(16);

            medicalDocumentsContainer.addView(
                    emptyText
            );

        } else {

            while (cursor.moveToNext()) {

                int documentId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

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

                String filePath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_path"
                                )
                        );

                TextView document =
                        new TextView(this);

                document.setText(
                        "📄 " + documentType +
                                "\n" +
                                documentName +
                                "\n\nTap to open"
                );

                document.setTextSize(16);

                document.setPadding(
                        20,
                        20,
                        20,
                        20
                );

                // TAP → OPEN DOCUMENT
                document.setOnClickListener(v -> {

                    openMedicalDocument(
                            filePath
                    );
                });

                // LONG PRESS → MENU
                document.setOnLongClickListener(v -> {

                    showDocumentMenu(
                            documentId,
                            documentName,
                            filePath
                    );

                    return true;
                });

                medicalDocumentsContainer.addView(
                        document
                );
            }
        }

        cursor.close();
    }

    private void openMedicalDocument(
            String filePath) {

        if (filePath == null ||
                filePath.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "File not found",
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
                            getPackageName() +
                                    ".fileprovider",
                            file
                    );

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW
                    );

            intent.setDataAndType(
                    fileUri,
                    getMimeType(filePath)
            );

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    this,
                    "No app found to open this file",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open document",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String getMimeType(
            String filePath) {

        String lower =
                filePath.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }

        if (lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        if (lower.endsWith(".png")) {
            return "image/png";
        }

        if (lower.endsWith(".webp")) {
            return "image/webp";
        }

        if (lower.endsWith(".mp4")) {
            return "video/mp4";
        }

        if (lower.endsWith(".3gp")) {
            return "video/3gpp";
        }

        return "*/*";
    }

    private void showDocumentMenu(
            int documentId,
            String documentName,
            String filePath) {

        new AlertDialog.Builder(this)
                .setTitle(documentName)
                .setItems(
                        new String[]{
                                "Rename",
                                "Delete",
                                "Share"
                        },
                        (dialog, which) -> {

                            if (which == 0) {

                                showRenameDocumentDialog(
                                        documentId,
                                        documentName
                                );

                            } else if (which == 1) {

                                deleteMedicalDocument(
                                        documentId
                                );

                            } else if (which == 2) {

                                shareMedicalDocument(
                                        filePath
                                );
                            }
                        }
                )
                .show();
    }

    private void showRenameDocumentDialog(
            int documentId,
            String oldName) {

        final android.widget.EditText input =
                new android.widget.EditText(this);

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
                                return;
                            }

                            boolean updated =
                                    databaseHelper
                                            .renameMedicalDocument(
                                                    documentId,
                                                    newName
                                            );

                            if (updated) {

                                Toast.makeText(
                                        this,
                                        "Document renamed",
                                        Toast.LENGTH_SHORT
                                ).show();

                                showMedicalDocuments();
                            }
                        }
                )
                .show();
    }

    private void deleteMedicalDocument(
            int documentId) {

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

                            databaseHelper
                                    .deleteMedicalDocument(
                                            documentId
                                    );

                            showMedicalDocuments();
                        }
                )
                .show();
    }

    private void shareMedicalDocument(
            String filePath) {

        if (filePath == null ||
                filePath.trim().isEmpty()) {
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
                            getPackageName() +
                                    ".fileprovider",
                            file
                    );

            Intent shareIntent =
                    new Intent(
                            Intent.ACTION_SEND
                    );

            shareIntent.setType(
                    getMimeType(filePath)
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

    private void showAppointment() {

        Cursor cursor =
                databaseHelper.getMedicalConditions(
                        personId
                );

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "id"
                            )
                    );

            if (id == conditionId) {

                String appointment =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "next_appointment"
                                )
                        );

                if (appointment == null ||
                        appointment.trim().isEmpty()) {

                    Toast.makeText(
                            this,
                            "Next Appointment: Not scheduled",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Toast.makeText(
                            this,
                            "Next Appointment: " +
                                    appointment,
                            Toast.LENGTH_SHORT
                    ).show();
                }

                break;
            }
        }

        cursor.close();
    }

    private void showAppointmentPicker() {

        java.util.Calendar calendar =
                java.util.Calendar.getInstance();

        int year =
                calendar.get(java.util.Calendar.YEAR);

        int month =
                calendar.get(java.util.Calendar.MONTH);

        int day =
                calendar.get(java.util.Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog dialog =
                new android.app.DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            String appointmentDate =
                                    String.format(
                                            java.util.Locale.getDefault(),
                                            "%02d/%02d/%04d",
                                            selectedDay,
                                            selectedMonth + 1,
                                            selectedYear
                                    );

                            boolean updated =
                                    databaseHelper.updateMedicalAppointment(
                                            conditionId,
                                            appointmentDate
                                    );

                            if (updated) {

                                Toast.makeText(
                                        this,
                                        "Appointment updated",
                                        Toast.LENGTH_SHORT
                                ).show();

                                showAppointment();
                            }
                        },
                        year,
                        month,
                        day
                );

        dialog.show();
    }
}