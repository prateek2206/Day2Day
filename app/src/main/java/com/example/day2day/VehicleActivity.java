package com.example.day2day;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VehicleActivity extends AppCompatActivity {

    LinearLayout vehicleContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle);

        Button addVehicleButton =
                findViewById(R.id.addVehicleButton);


        vehicleContainer =
                findViewById(R.id.vehicleContainer);

        databaseHelper =
                new DatabaseHelper(this);

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

        Cursor cursor =
                databaseHelper.getAllVehicles();

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No vehicles added yet.\n" +
                            "Tap + Add Vehicle to add one."
            );

            emptyText.setTextSize(16);

            vehicleContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                int vehicleId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "vehicle_name"
                                )
                        );

                String number =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "vehicle_number"
                                )
                        );

                TextView vehicle =
                        new TextView(this);

                vehicle.setText(
                        "🚗 " + name + "\n" +
                                "Vehicle No: " + number + "\n\n" +
                                "Tap to manage documents"
                );

                vehicle.setTextSize(17);

                vehicle.setPadding(
                        20,
                        20,
                        20,
                        20
                );

                vehicle.setOnLongClickListener(v -> {

                    new AlertDialog.Builder(
                            VehicleActivity.this
                    )
                            .setTitle(name)
                            .setItems(
                                    new String[]{
                                            "Rename",
                                            "Delete",
                                            "Share"
                                    },
                                    (dialog, which) -> {

                                        if (which == 0) {

                                            showRenameVehicleDialog(
                                                    vehicleId,
                                                    name
                                            );

                                        } else if (which == 1) {

                                            deleteVehicle(vehicleId);

                                        } else if (which == 2) {

                                            Toast.makeText(
                                                    VehicleActivity.this,
                                                    "Vehicle sharing will be available with its documents.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                            )
                            .show();

                    return true;
                });

                vehicle.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            VehicleActivity.this,
                            VehicleDocumentsActivity.class
                    );

                    intent.putExtra(
                            "vehicle_id",
                            vehicleId
                    );

                    intent.putExtra(
                            "vehicle_name",
                            name
                    );

                    startActivity(intent);
                });

                vehicleContainer.addView(vehicle);
            }
        }

        cursor.close();
    }

    private void showRenameVehicleDialog(
            int vehicleId,
            String oldName) {

        EditText input = new EditText(this);

        input.setText(oldName);
        input.setSelectAllOnFocus(true);

        new AlertDialog.Builder(this)
                .setTitle("Rename Vehicle")
                .setView(input)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Rename",
                        (dialog, which) -> {

                            String newName =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (newName.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Name cannot be empty",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            boolean updated =
                                    databaseHelper.renameVehicle(
                                            vehicleId,
                                            newName
                                    );

                            if (updated) {

                                Toast.makeText(
                                        this,
                                        "Vehicle renamed",
                                        Toast.LENGTH_SHORT
                                ).show();

                                showVehicles();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Rename failed",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    private void deleteVehicle(int vehicleId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Vehicle?")
                .setMessage(
                        "Are you sure you want to delete this vehicle?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            databaseHelper.deleteVehicle(
                                    vehicleId
                            );

                            showVehicles();

                            Toast.makeText(
                                    this,
                                    "Vehicle deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }
}