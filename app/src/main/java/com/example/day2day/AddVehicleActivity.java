package com.example.day2day;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddVehicleActivity extends AppCompatActivity {

    EditText vehicleName;
    EditText vehicleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vehicle);

        vehicleName = findViewById(R.id.vehicleName);
        vehicleNumber = findViewById(R.id.vehicleNumber);

        Button saveButton = findViewById(R.id.saveVehicleButton);

        saveButton.setOnClickListener(v ->
                saveVehicle());
    }

    private void saveVehicle() {

        String name = vehicleName.getText().toString().trim();
        String number = vehicleNumber.getText().toString().trim();

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

        DatabaseHelper databaseHelper =
                new DatabaseHelper(this);

        /*
         * We are no longer adding:
         * - Service date
         * - Insurance expiry
         * - PUC expiry
         * - Fuel expense
         *
         * These features are handled separately now.
         */

        boolean inserted = databaseHelper.addVehicle(
                name,
                number,
                "",
                "",
                "",
                ""
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