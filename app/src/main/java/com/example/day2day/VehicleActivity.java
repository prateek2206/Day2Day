package com.example.day2day;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VehicleActivity extends AppCompatActivity {

    LinearLayout vehicleContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle);

        Button addVehicleButton = findViewById(R.id.addVehicleButton);

        vehicleContainer = findViewById(R.id.vehicleContainer);

        databaseHelper = new DatabaseHelper(this);

        addVehicleButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VehicleActivity.this,
                    AddVehicleActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showVehicles();
        }
    }

    private void showVehicles() {

        vehicleContainer.removeAllViews();

        Cursor cursor = databaseHelper.getAllVehicles();

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText(
                    "No vehicles added yet.\nTap + Add Vehicle to add one."
            );

            emptyText.setTextSize(16);

            vehicleContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("vehicle_name")
                        );

                String number =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("vehicle_number")
                        );

                String service =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("service_date")
                        );

                String insurance =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("insurance_expiry")
                        );

                String puc =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("puc_expiry")
                        );

                String fuel =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("fuel_expense")
                        );

                TextView vehicle = new TextView(this);

                vehicle.setText(
                        name + " (" + number + ")\n" +
                                "Last Service: " + service + "\n" +
                                "Insurance Expiry: " + insurance + "\n" +
                                "PUC Expiry: " + puc + "\n" +
                                "Fuel Expense: ₹" + fuel
                );

                vehicle.setTextSize(17);
                vehicle.setPadding(20, 20, 20, 20);

                vehicleContainer.addView(vehicle);
            }
        }

        cursor.close();
    }
}
