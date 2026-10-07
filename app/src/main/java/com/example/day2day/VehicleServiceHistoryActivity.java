package com.example.day2day;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class VehicleServiceHistoryActivity extends AppCompatActivity {

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

        TextView addServiceButton =
                findViewById(R.id.addServiceButton);

        addServiceButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            VehicleServiceHistoryActivity.this,
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

                String filePath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_path"
                                )
                        );

                TextView service =
                        new TextView(this);

                String text =
                        "🛠 Service\n" +
                                "Date: " + serviceDate + "\n" +
                                "Center: " + serviceCenter + "\n" +
                                "Details: " +
                                (serviceDetails.isEmpty()
                                        ? "No details"
                                        : serviceDetails);

                if (filePath != null &&
                        !filePath.isEmpty()) {

                    File file =
                            new File(filePath);

                    if (file.exists()) {
                        text +=
                                "\n📎 " +
                                        file.getName();
                    }
                }

                service.setText(text);
                service.setTextSize(17);
                service.setPadding(
                        20,
                        20,
                        20,
                        20
                );

                service.setOnClickListener(v -> {

                    if (filePath == null ||
                            filePath.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No file attached",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    openFile(filePath);
                });

                service.setOnLongClickListener(v -> {

                    new AlertDialog.Builder(this)
                            .setTitle("Service")
                            .setItems(
                                    new String[]{
                                            "Delete",
                                            "Share"
                                    },
                                    (dialog, which) -> {

                                        if (which == 0) {
                                            deleteService(
                                                    serviceId,
                                                    filePath
                                            );
                                        } else {
                                            shareFile(
                                                    filePath
                                            );
                                        }
                                    }
                            )
                            .show();

                    return true;
                });

                serviceHistoryContainer.addView(
                        service
                );
            }
        }

        cursor.close();
    }

    private void openFile(String filePath) {

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
                new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(
                fileUri,
                getContentResolver()
                        .getType(fileUri)
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

    private void deleteService(
            int serviceId,
            String filePath) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Service?")
                .setMessage(
                        "Are you sure you want to delete this service record?"
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
                        "Share File"
                )
        );
    }
}