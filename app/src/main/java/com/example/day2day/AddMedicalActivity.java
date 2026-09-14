package com.example.day2day;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddMedicalActivity extends AppCompatActivity {

    EditText personName, recordType, doctor, recordDate, notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_medical);

        personName = findViewById(R.id.personName);
        recordType = findViewById(R.id.recordType);
        doctor = findViewById(R.id.doctor);
        recordDate = findViewById(R.id.recordDate);
        notes = findViewById(R.id.notes);

        Button saveButton = findViewById(R.id.saveMedicalButton);

        recordDate.setOnClickListener(v -> showDatePicker());

        saveButton.setOnClickListener(v -> saveMedicalRecord());
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date = selectedDay + "/" +
                            (selectedMonth + 1) + "/" +
                            selectedYear;

                    recordDate.setText(date);
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    private void saveMedicalRecord() {

        String name = personName.getText().toString().trim();
        String type = recordType.getText().toString().trim();
        String doctorName = doctor.getText().toString().trim();
        String date = recordDate.getText().toString().trim();
        String note = notes.getText().toString().trim();

        if (name.isEmpty()) {
            personName.setError("Enter name");
            personName.requestFocus();
            return;
        }

        if (type.isEmpty()) {
            recordType.setError("Enter record type");
            recordType.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            recordDate.setError("Select date");
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        boolean inserted = databaseHelper.addMedicalRecord(
                name,
                type,
                doctorName,
                date,
                note
        );

        if (inserted) {

            Toast.makeText(
                    this,
                    "Medical record saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to save medical record",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}