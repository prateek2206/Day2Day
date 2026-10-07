package com.example.day2day;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class AddVehicleDocumentActivity extends AppCompatActivity {

    Spinner documentTypeSpinner;
    EditText documentNameEditText;
    EditText expiryDateEditText;
    TextView selectedFileText;

    DatabaseHelper databaseHelper;

    int vehicleId;

    Uri selectedFileUri;
    String selectedFileName = "";

    private static final int FILE_PICKER_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vehicle_document);

        documentTypeSpinner =
                findViewById(R.id.documentTypeSpinner);

        documentNameEditText =
                findViewById(R.id.documentNameEditText);

        expiryDateEditText =
                findViewById(R.id.expiryDateEditText);

        Button selectFileButton =
                findViewById(R.id.selectFileButton);

        Button saveButton =
                findViewById(R.id.saveVehicleDocumentButton);

        selectedFileText =
                findViewById(R.id.selectedFileText);

        databaseHelper =
                new DatabaseHelper(this);

        vehicleId =
                getIntent().getIntExtra("vehicle_id", -1);

        // Document types
        String[] documentTypes = {
                "RC",
                "Insurance",
                "PUC",
                "Other"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        documentTypes
                );

        documentTypeSpinner.setAdapter(adapter);

        // Expiry date picker
        expiryDateEditText.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            this,
                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                String date =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d/%02d/%04d",
                                                selectedDay,
                                                selectedMonth + 1,
                                                selectedYear
                                        );

                                expiryDateEditText.setText(date);
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
        });

        // Select Photo / PDF
        selectFileButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.addCategory(
                    Intent.CATEGORY_OPENABLE
            );

            intent.setType("*/*");

            String[] mimeTypes = {
                    "image/*",
                    "application/pdf"
            };

            intent.putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    mimeTypes
            );

            startActivityForResult(
                    intent,
                    FILE_PICKER_REQUEST
            );
        });

        // Save document
        saveButton.setOnClickListener(v -> {

            String documentType =
                    documentTypeSpinner
                            .getSelectedItem()
                            .toString();

            String documentName =
                    documentNameEditText
                            .getText()
                            .toString()
                            .trim();

            String expiryDate =
                    expiryDateEditText
                            .getText()
                            .toString()
                            .trim();

            // Check vehicle
            if (vehicleId == -1) {

                Toast.makeText(
                        this,
                        "Vehicle not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Check document name
            if (documentName.isEmpty()) {

                documentNameEditText.setError(
                        "Enter document name"
                );

                return;
            }

            // Expiry date is OPTIONAL
            // Empty expiry date is allowed.

            // Check file
            if (selectedFileUri == null) {

                Toast.makeText(
                        this,
                        "Please select a Photo or PDF",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Save actual file
            String filePath = "";

            try {

                File savedFile =
                        FileStorageHelper.saveFile(
                                this,
                                selectedFileUri,
                                "VehicleDocuments",
                                selectedFileName
                        );

                filePath =
                        savedFile.getAbsolutePath();

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Failed to save file",
                        Toast.LENGTH_SHORT
                ).show();

                e.printStackTrace();

                return;
            }

            // Save document information in database
            boolean success =
                    databaseHelper.addVehicleDocument(
                            vehicleId,
                            documentType,
                            documentName,
                            expiryDate,
                            filePath
                    );

            if (success) {

                Toast.makeText(
                        this,
                        "Document saved successfully",
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
        });
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

        if (requestCode == FILE_PICKER_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            selectedFileUri =
                    data.getData();

            selectedFileName =
                    getFileName(selectedFileUri);

            selectedFileText.setText(
                    "Selected: " + selectedFileName
            );
        }
    }

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
}