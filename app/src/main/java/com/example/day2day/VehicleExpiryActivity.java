package com.example.day2day;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VehicleExpiryActivity extends AppCompatActivity {

    LinearLayout vehicleExpiryContainer;
    DatabaseHelper databaseHelper;

    SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle_expiry);

        vehicleExpiryContainer =
                findViewById(R.id.vehicleExpiryContainer);

        databaseHelper =
                new DatabaseHelper(this);

        showExpiringDocuments();
    }

    private void showExpiringDocuments() {

        vehicleExpiryContainer.removeAllViews();

        Cursor cursor =
                databaseHelper.getExpiringVehicleDocuments();

        boolean found = false;

        Date today = new Date();

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

            String vehicleName =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "vehicle_name"
                            )
                    );

            try {

                Date expiry =
                        dateFormat.parse(expiryDate);

                if (expiry == null) {
                    continue;
                }

                long difference =
                        expiry.getTime() - today.getTime();

                long daysLeft =
                        TimeUnit.MILLISECONDS.toDays(
                                difference
                        );

                // Show expired documents
                // and documents expiring within 30 days
                if (daysLeft <= 30) {

                    found = true;

                    TextView document =
                            new TextView(this);

                    if (daysLeft < 0) {

                        document.setText(
                                "🔴 " + documentType + "\n" +
                                        documentName + "\n" +
                                        vehicleName + "\n" +
                                        "Expired: " + expiryDate
                        );

                    } else {

                        document.setText(
                                "🟠 " + documentType + "\n" +
                                        documentName + "\n" +
                                        vehicleName + "\n" +
                                        "Expires: " + expiryDate +
                                        "\n" +
                                        "Days left: " + daysLeft
                        );
                    }

                    document.setTextSize(17);

                    document.setPadding(
                            20,
                            20,
                            20,
                            20
                    );

                    vehicleExpiryContainer.addView(
                            document
                    );
                }

            } catch (ParseException e) {
                // Ignore invalid dates
            }
        }

        cursor.close();

        if (!found) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No vehicle documents are expiring soon."
            );

            emptyText.setTextSize(16);

            emptyText.setPadding(
                    10,
                    10,
                    10,
                    10
            );

            vehicleExpiryContainer.addView(
                    emptyText
            );
        }
    }
}