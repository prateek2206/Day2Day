package com.example.day2day;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MedicalActivity extends AppCompatActivity {

    LinearLayout medicalContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medical);

        Button addMedicalButton = findViewById(R.id.addMedicalButton);

        medicalContainer = findViewById(R.id.medicalContainer);

        databaseHelper = new DatabaseHelper(this);

        addMedicalButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MedicalActivity.this,
                    AddMedicalActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showMedicalRecords();
        }
    }

    private void showMedicalRecords() {

        medicalContainer.removeAllViews();

        Cursor cursor = databaseHelper.getAllMedicalRecords();

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText(
                    "No medical records added yet.\nTap + Add Medical Record to add one."
            );

            emptyText.setTextSize(16);

            medicalContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                String personName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("person_name")
                        );

                String recordType =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("record_type")
                        );

                String doctor =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("doctor")
                        );

                String date =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("record_date")
                        );

                String notes =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("notes")
                        );

                TextView record = new TextView(this);

                record.setText(
                        personName + "\n" +
                                "Record: " + recordType + "\n" +
                                "Doctor: " + doctor + "\n" +
                                "Date: " + date + "\n" +
                                "Notes: " + notes
                );

                record.setTextSize(17);

                record.setPadding(20, 20, 20, 20);

                medicalContainer.addView(record);
            }
        }

        cursor.close();
    }
}