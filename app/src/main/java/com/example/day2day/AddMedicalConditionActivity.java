package com.example.day2day;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddMedicalConditionActivity extends AppCompatActivity {

    EditText conditionName;
    EditText conditionNotes;
    EditText nextAppointment;

    DatabaseHelper databaseHelper;

    int personId;
    String personName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_medical_condition
        );

        personId =
                getIntent().getIntExtra(
                        "person_id",
                        -1
                );

        personName =
                getIntent().getStringExtra(
                        "person_name"
                );

        TextView personNameText =
                findViewById(R.id.personNameText);

        conditionName =
                findViewById(R.id.conditionName);

        conditionNotes =
                findViewById(R.id.conditionNotes);

        nextAppointment =
                findViewById(R.id.nextAppointment);

        Button saveConditionButton =
                findViewById(R.id.saveConditionButton);

        databaseHelper =
                new DatabaseHelper(this);

        personNameText.setText(
                "Person: " + personName
        );

        nextAppointment.setOnClickListener(v ->
                showAppointmentPicker()
        );

        saveConditionButton.setOnClickListener(v ->
                saveCondition()
        );
    }

    private void showAppointmentPicker() {

        Calendar calendar =
                Calendar.getInstance();

        int year =
                calendar.get(Calendar.YEAR);

        int month =
                calendar.get(Calendar.MONTH);

        int day =
                calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            String date =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d/%02d/%04d",
                                            selectedDay,
                                            selectedMonth + 1,
                                            selectedYear
                                    );

                            nextAppointment.setText(date);
                        },
                        year,
                        month,
                        day
                );

        dialog.show();
    }

    private void saveCondition() {

        String name =
                conditionName
                        .getText()
                        .toString()
                        .trim();

        String notes =
                conditionNotes
                        .getText()
                        .toString()
                        .trim();

        String appointment =
                nextAppointment
                        .getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            conditionName.setError(
                    "Enter medical condition"
            );

            conditionName.requestFocus();

            return;
        }

        long result =
                databaseHelper.addMedicalCondition(
                        personId,
                        name,
                        notes,
                        appointment
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Medical condition saved",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to save condition",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}