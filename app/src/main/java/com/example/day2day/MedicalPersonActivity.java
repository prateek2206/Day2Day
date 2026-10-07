package com.example.day2day;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MedicalPersonActivity extends AppCompatActivity {

    LinearLayout conditionContainer;
    DatabaseHelper databaseHelper;

    int personId;
    String personName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medical_person);

        personId = getIntent().getIntExtra("person_id", -1);
        personName = getIntent().getStringExtra("person_name");

        TextView title =
                findViewById(R.id.medicalPersonTitle);

        TextView subtitle =
                findViewById(R.id.medicalPersonSubtitle);

        Button addConditionButton =
                findViewById(R.id.addConditionButton);

        conditionContainer =
                findViewById(R.id.conditionContainer);

        databaseHelper =
                new DatabaseHelper(this);

        title.setText(personName);
        subtitle.setText("Manage medical conditions");

        addConditionButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MedicalPersonActivity.this,
                    AddMedicalConditionActivity.class
            );

            intent.putExtra("person_id", personId);
            intent.putExtra("person_name", personName);


            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showConditions();
        }
    }

    private void showConditions() {

        conditionContainer.removeAllViews();

        Cursor cursor =
                databaseHelper.getMedicalConditions(personId);

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No medical conditions added yet.\n" +
                            "Tap + Add Medical Condition to add one."
            );

            emptyText.setTextSize(16);

            conditionContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                int conditionId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String conditionName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "condition_name"
                                )
                        );

                String notes =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "notes"
                                )
                        );

                String appointment =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "next_appointment"
                                )
                        );

                TextView condition =
                        new TextView(this);

                String appointmentText;

                if (appointment == null ||
                        appointment.trim().isEmpty()) {

                    appointmentText =
                            "Next Appointment: Not scheduled";

                } else {

                    appointmentText =
                            "Next Appointment: " + appointment;
                }

                condition.setText(
                        "🩺 " + conditionName + "\n\n" +
                                "Notes: " + notes + "\n" +
                                appointmentText
                );

                condition.setTextSize(17);

                condition.setPadding(
                        20,
                        20,
                        20,
                        20
                );

                condition.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            MedicalPersonActivity.this,
                            MedicalConditionActivity.class
                    );

                    intent.putExtra(
                            "condition_id",
                            conditionId
                    );

                    intent.putExtra(
                            "condition_name",
                            conditionName
                    );

                    intent.putExtra(
                            "person_name",
                            personName
                    );

                    intent.putExtra(
                            "person_id",
                            personId
                    );

                    startActivity(intent);
                });

                condition.setOnLongClickListener(v -> {

                    new AlertDialog.Builder(
                            MedicalPersonActivity.this
                    )
                            .setTitle("Medical Condition")
                            .setItems(
                                    new String[]{
                                            "Rename",
                                            "Delete",
                                            "Share"
                                    },
                                    (dialog, which) -> {

                                        if (which == 0) {
                                            showRenameDialog(
                                                    conditionId,
                                                    conditionName
                                            );
                                        }

                                        else if (which == 1) {
                                            deleteCondition(
                                                    conditionId
                                            );
                                        }

                                        else if (which == 2) {

                                            Toast.makeText(
                                                    MedicalPersonActivity.this,
                                                    "Share feature is available for documents.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                            )
                            .show();

                    return true;
                });

                conditionContainer.addView(condition);
            }
        }

        cursor.close();
    }

    private void showRenameDialog(
            int conditionId,
            String oldName) {

        EditText input =
                new EditText(this);

        input.setText(oldName);

        new AlertDialog.Builder(this)
                .setTitle("Rename Medical Condition")
                .setView(input)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            String newName =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (!newName.isEmpty()) {

                                boolean updated =
                                        databaseHelper.renameMedicalCondition(
                                                conditionId,
                                                newName
                                        );

                                if (updated) {

                                    Toast.makeText(
                                            this,
                                            "Medical condition renamed",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    showConditions();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "Rename failed",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        }
                )
                .show();
    }

    private void deleteCondition(int conditionId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Medical Condition?")
                .setMessage(
                        "Are you sure you want to delete this condition?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            databaseHelper
                                    .deleteMedicalCondition(
                                            conditionId
                                    );

                            showConditions();
                        }
                )
                .show();
    }




}