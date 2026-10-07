package com.example.day2day;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        // Product
        findViewById(R.id.productCard).setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    ProductActivity.class
            ));
        });

        // Medical
        findViewById(R.id.medicalCard).setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    MedicalActivity.class
            ));
        });

        // Vehicle
        findViewById(R.id.vehicleCard).setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    VehicleActivity.class
            ));
        });

        // My Documents
        findViewById(R.id.documentsCard).setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    DocumentsActivity.class
            ));
        });

        showProductReminder();
        showMedicalReminder();
        showVehicleReminder();
        showDocumentReminder();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showProductReminder();
            showMedicalReminder();
            showVehicleReminder();
            showDocumentReminder();
        }
    }


    // =========================================================
    // PRODUCT REMINDER
    // =========================================================

    private void showProductReminder() {

        TextView reminderText =
                findViewById(R.id.productReminderText);

        Cursor cursor =
                databaseHelper.getAllProducts();

        StringBuilder reminder = new StringBuilder();

        while (cursor.moveToNext()) {

            String productName =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    );

            String warranty =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("warranty")
                    );

            String message =
                    getReminderMessage(
                            warranty,
                            productName + " warranty"
                    );

            if (!message.isEmpty()) {
                reminder.append(message).append("\n");
            }
        }

        cursor.close();

        if (reminder.length() == 0) {
            reminderText.setText(
                    "✅ No important reminders"
            );
        } else {
            reminderText.setText(
                    reminder.toString().trim()
            );
        }
    }


    // =========================================================
    // MEDICAL REMINDER
    // =========================================================

    private void showMedicalReminder() {

        TextView reminderText =
                findViewById(R.id.medicalReminderText);

        Cursor people =
                databaseHelper.getAllMedicalPersons();

        StringBuilder reminder =
                new StringBuilder();

        while (people.moveToNext()) {

            int personId =
                    people.getInt(
                            people.getColumnIndexOrThrow("id")
                    );

            String personName =
                    people.getString(
                            people.getColumnIndexOrThrow(
                                    "person_name"
                            )
                    );

            Cursor conditions =
                    databaseHelper.getMedicalConditions(personId);

            while (conditions.moveToNext()) {

                String conditionName =
                        conditions.getString(
                                conditions.getColumnIndexOrThrow(
                                        "condition_name"
                                )
                        );

                String appointment =
                        conditions.getString(
                                conditions.getColumnIndexOrThrow(
                                        "next_appointment"
                                )
                        );

                if (appointment == null ||
                        appointment.trim().isEmpty()) {
                    continue;
                }

                String message =
                        getReminderMessage(
                                appointment,
                                personName +
                                        " → " +
                                        conditionName +
                                        " appointment"
                        );

                if (!message.isEmpty()) {
                    reminder.append(message).append("\n");
                }
            }

            conditions.close();
        }

        people.close();

        if (reminder.length() == 0) {
            reminderText.setText(
                    "✅ No important reminders"
            );
        } else {
            reminderText.setText(
                    reminder.toString().trim()
            );
        }
    }


    // =========================================================
    // VEHICLE REMINDER
    // =========================================================

    private void showVehicleReminder() {

        TextView reminderText =
                findViewById(R.id.vehicleReminderText);

        Cursor cursor =
                databaseHelper.getExpiringVehicleDocuments();

        StringBuilder reminder =
                new StringBuilder();

        while (cursor.moveToNext()) {

            String documentType =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "document_type"
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

            String message =
                    getReminderMessage(
                            expiryDate,
                            vehicleName +
                                    " " +
                                    documentType
                    );

            if (!message.isEmpty()) {
                reminder.append(message).append("\n");
            }
        }

        cursor.close();

        if (reminder.length() == 0) {
            reminderText.setText(
                    "✅ No important reminders"
            );
        } else {
            reminderText.setText(
                    reminder.toString().trim()
            );
        }
    }


    // =========================================================
    // MY DOCUMENTS REMINDER
    // =========================================================

    private void showDocumentReminder() {

        TextView reminderText =
                findViewById(R.id.documentsReminderText);

        Cursor cursor =
                databaseHelper.getAllDocuments();

        StringBuilder reminder =
                new StringBuilder();

        while (cursor.moveToNext()) {

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

            String message =
                    getReminderMessage(
                            expiryDate,
                            documentName
                    );

            if (!message.isEmpty()) {
                reminder.append(message).append("\n");
            }
        }

        cursor.close();

        if (reminder.length() == 0) {
            reminderText.setText(
                    "✅ No important reminders"
            );
        } else {
            reminderText.setText(
                    reminder.toString().trim()
            );
        }
    }


    // =========================================================
    // COMMON REMINDER LOGIC
    // =========================================================

    private String getReminderMessage(
            String dateString,
            String itemName) {

        if (dateString == null ||
                dateString.trim().isEmpty()) {
            return "";
        }

        try {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            dateFormat.setLenient(false);

            Date expiry =
                    dateFormat.parse(dateString);

            if (expiry == null) {
                return "";
            }

            Date today = new Date();

            long difference =
                    expiry.getTime() -
                            today.getTime();

            long daysLeft =
                    TimeUnit.MILLISECONDS.toDays(
                            difference
                    );

            // Already expired
            if (daysLeft < 0) {

                return "🔴 " +
                        itemName +
                        " has expired.";
            }

            // Today
            if (daysLeft == 0) {

                return "🔴 " +
                        itemName +
                        " is due today.";
            }

            // Within 30 days
            if (daysLeft <= 30) {

                return "⚠️ " +
                        itemName +
                        " is due in " +
                        daysLeft +
                        " days.";
            }

        } catch (Exception e) {
            // Ignore invalid dates
        }

        return "";
    }
}