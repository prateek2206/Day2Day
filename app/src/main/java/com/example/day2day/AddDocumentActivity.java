package com.example.day2day;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddDocumentActivity extends AppCompatActivity {

    EditText documentName;
    EditText documentType;
    EditText description;
    EditText expiryDate;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_document);

        documentName = findViewById(R.id.documentName);
        documentType = findViewById(R.id.documentType);
        description = findViewById(R.id.description);
        expiryDate = findViewById(R.id.expiryDate);

        Button saveButton = findViewById(R.id.saveDocumentButton);

        databaseHelper = new DatabaseHelper(this);

        expiryDate.setOnClickListener(v -> showDatePicker());

        saveButton.setOnClickListener(v -> saveDocument());
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

                    expiryDate.setText(date);
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    private void saveDocument() {

        String name = documentName.getText().toString().trim();
        String type = documentType.getText().toString().trim();
        String details = description.getText().toString().trim();
        String expiry = expiryDate.getText().toString().trim();

        if (name.isEmpty()) {
            documentName.setError("Enter document name");
            documentName.requestFocus();
            return;
        }

        if (type.isEmpty()) {
            documentType.setError("Enter document type");
            documentType.requestFocus();
            return;
        }

        boolean inserted = databaseHelper.addDocument(
                name,
                type,
                details,
                expiry
        );

        if (inserted) {

            Toast.makeText(
                    this,
                    "Document saved successfully!",
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
    }
}