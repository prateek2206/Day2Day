package com.example.day2day;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddServiceActivity extends AppCompatActivity {

    EditText serviceDateEditText;
    EditText serviceCenterEditText;
    EditText serviceDetailsEditText;

    TextView selectedServiceFileText;

    Button selectServiceFileButton;
    Button saveServiceButton;

    DatabaseHelper databaseHelper;

    int vehicleId;
    String vehicleName;

    // Multiple selected documents
    ArrayList<Uri> selectedFileUris = new ArrayList<>();
    ArrayList<String> selectedFileNames = new ArrayList<>();

    private static final int FILE_PICKER_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_service);

        serviceDateEditText =
                findViewById(R.id.serviceDateEditText);

        serviceCenterEditText =
                findViewById(R.id.serviceCenterEditText);

        serviceDetailsEditText =
                findViewById(R.id.serviceDetailsEditText);

        selectedServiceFileText =
                findViewById(R.id.selectedServiceFileText);

        selectServiceFileButton =
                findViewById(R.id.selectServiceFileButton);

        saveServiceButton =
                findViewById(R.id.saveServiceButton);

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

        // ================= DATE PICKER =================

        serviceDateEditText.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            this,
                            (view, year, month, dayOfMonth) -> {

                                String date =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d/%02d/%04d",
                                                dayOfMonth,
                                                month + 1,
                                                year
                                        );

                                serviceDateEditText.setText(date);
                            },
                            calendar.get(
                                    Calendar.YEAR
                            ),
                            calendar.get(
                                    Calendar.MONTH
                            ),
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            )
                    );

            datePickerDialog.show();
        });

        // ================= SELECT FILE =================

        selectServiceFileButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Intent.ACTION_OPEN_DOCUMENT
                    );

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

        // ================= SAVE =================

        saveServiceButton.setOnClickListener(v -> {

            String serviceDate =
                    serviceDateEditText
                            .getText()
                            .toString()
                            .trim();

            String serviceCenter =
                    serviceCenterEditText
                            .getText()
                            .toString()
                            .trim();

            String serviceDetails =
                    serviceDetailsEditText
                            .getText()
                            .toString()
                            .trim();

            if (vehicleId == -1) {

                Toast.makeText(
                        this,
                        "Vehicle not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (serviceDate.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select service date",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (serviceCenter.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please enter service center",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // ================= SAVE SERVICE =================

            /*
             * We create the service first.
             *
             * The returned service ID will be used
             * to connect all documents to this service.
             */

            long serviceId =
                    databaseHelper.addVehicleService(
                            vehicleId,
                            serviceDate,
                            serviceCenter,
                            serviceDetails,
                            ""
                    );

            if (serviceId == -1) {

                Toast.makeText(
                        this,
                        "Failed to save service",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // ================= SAVE DOCUMENTS =================

            for (int i = 0;
                 i < selectedFileUris.size();
                 i++) {

                Uri fileUri =
                        selectedFileUris.get(i);

                String fileName =
                        selectedFileNames.get(i);

                try {

                    File savedFile =
                            FileStorageHelper.saveFile(
                                    this,
                                    fileUri,
                                    "VehicleServices",
                                    fileName
                            );

                    String filePath =
                            savedFile.getAbsolutePath();

                    databaseHelper.addVehicleServiceDocument(
                            serviceId,
                            fileName,
                            getDocumentType(fileName),
                            filePath
                    );

                } catch (Exception e) {

                    Toast.makeText(
                            this,
                            "Failed to save one of the documents",
                            Toast.LENGTH_SHORT
                    ).show();

                    e.printStackTrace();
                }
            }

            Toast.makeText(
                    this,
                    "Service saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });
    }

    // ================= FILE RESULT =================

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

            if (selectedUri != null) {

                String fileName =
                        getFileName(selectedUri);

                selectedFileUris.add(
                        selectedUri
                );

                selectedFileNames.add(
                        fileName
                );

                updateSelectedFilesText();
            }
        }
    }

    // ================= SHOW SELECTED FILES =================

    private void updateSelectedFilesText() {

        if (selectedFileNames.isEmpty()) {

            selectedServiceFileText.setText(
                    "No documents selected"
            );

            return;
        }

        StringBuilder text =
                new StringBuilder();

        text.append("Selected documents:\n");

        for (int i = 0;
             i < selectedFileNames.size();
             i++) {

            text.append("📎 ")
                    .append(selectedFileNames.get(i))
                    .append("\n");
        }

        selectedServiceFileText.setText(
                text.toString()
        );
    }

    // ================= DOCUMENT TYPE =================

    private String getDocumentType(String fileName) {

        if (fileName == null) {
            return "Other";
        }

        String lowerName =
                fileName.toLowerCase(Locale.getDefault());

        if (lowerName.endsWith(".pdf")) {
            return "PDF";
        }

        if (lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png")) {

            return "Image";
        }

        return "Other";
    }

    // ================= GET FILE NAME =================

    private String getFileName(Uri uri) {

        String result = null;

        if ("content".equals(uri.getScheme())) {

            Cursor cursor =
                    getContentResolver().query(
                            uri,
                            null,
                            null,
                            null,
                            null
                    );

            if (cursor != null) {

                try {

                    if (cursor.moveToFirst()) {

                        int index =
                                cursor.getColumnIndex(
                                        OpenableColumns.DISPLAY_NAME
                                );

                        if (index >= 0) {

                            result =
                                    cursor.getString(
                                            index
                                    );
                        }
                    }

                } finally {

                    cursor.close();
                }
            }
        }

        if (result == null) {

            result =
                    uri.getPath();

            if (result != null) {

                int cut =
                        result.lastIndexOf('/');

                if (cut != -1) {

                    result =
                            result.substring(
                                    cut + 1
                            );
                }
            }
        }

        return result;
    }
}