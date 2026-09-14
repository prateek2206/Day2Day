package com.example.day2day;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddVehicleActivity extends AppCompatActivity {

    EditText vehicleName;
    EditText vehicleNumber;
    EditText serviceDate;
    EditText insuranceExpiry;
    EditText pucExpiry;
    EditText fuelExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vehicle);

        vehicleName = findViewById(R.id.vehicleName);
        vehicleNumber = findViewById(R.id.vehicleNumber);
        serviceDate = findViewById(R.id.serviceDate);
        insuranceExpiry = findViewById(R.id.insuranceExpiry);
        pucExpiry = findViewById(R.id.pucExpiry);
        fuelExpense = findViewById(R.id.fuelExpense);

        Button saveButton = findViewById(R.id.saveVehicleButton);

        serviceDate.setOnClickListener(v ->
                showDatePicker(serviceDate));

        insuranceExpiry.setOnClickListener(v ->
                showDatePicker(insuranceExpiry));

        pucExpiry.setOnClickListener(v ->
                showDatePicker(pucExpiry));

        saveButton.setOnClickListener(v ->
                saveVehicle());
    }

    private void showDatePicker(EditText editText) {

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

                    editText.setText(date);
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    private void saveVehicle() {

        String name = vehicleName.getText().toString().trim();
        String number = vehicleNumber.getText().toString().trim();
        String service = serviceDate.getText().toString().trim();
        String insurance = insuranceExpiry.getText().toString().trim();
        String puc = pucExpiry.getText().toString().trim();
        String fuel = fuelExpense.getText().toString().trim();

        if (name.isEmpty()) {
            vehicleName.setError("Enter vehicle name");
            vehicleName.requestFocus();
            return;
        }

        if (number.isEmpty()) {
            vehicleNumber.setError("Enter vehicle number");
            vehicleNumber.requestFocus();
            return;
        }

        if (service.isEmpty()) {
            serviceDate.setError("Select service date");
            serviceDate.requestFocus();
            return;
        }

        if (insurance.isEmpty()) {
            insuranceExpiry.setError("Select insurance expiry");
            insuranceExpiry.requestFocus();
            return;
        }

        if (puc.isEmpty()) {
            pucExpiry.setError("Select PUC expiry");
            pucExpiry.requestFocus();
            return;
        }

        DatabaseHelper databaseHelper =
                new DatabaseHelper(this);

        boolean inserted = databaseHelper.addVehicle(
                name,
                number,
                service,
                insurance,
                puc,
                fuel
        );

        if (inserted) {

            Toast.makeText(
                    this,
                    "Vehicle saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to save vehicle",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
