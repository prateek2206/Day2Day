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

public class MedicalActivity extends AppCompatActivity {

    LinearLayout medicalContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medical);

        Button addMedicalButton =
                findViewById(R.id.addMedicalButton);

        medicalContainer =
                findViewById(R.id.medicalContainer);

        databaseHelper =
                new DatabaseHelper(this);

        addMedicalButton.setOnClickListener(v ->
                showAddPersonDialog()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showPeople();
        }
    }

    private void showAddPersonDialog() {

        EditText input =
                new EditText(this);

        input.setHint("Person Name");
        input.setPadding(30, 20, 30, 20);

        new AlertDialog.Builder(this)
                .setTitle("Add Person")
                .setView(input)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            String name =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (name.isEmpty()) {
                                return;
                            }

                            long result =
                                    databaseHelper.addMedicalPerson(name);

                            if (result != -1) {
                                showPeople();
                            }
                        }
                )
                .show();
    }

    private void showPeople() {

        medicalContainer.removeAllViews();

        Cursor cursor =
                databaseHelper.getAllMedicalPersons();

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No people added yet.\n" +
                            "Tap + Add Medical Record to add a person."
            );

            emptyText.setTextSize(16);

            medicalContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                int personId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String personName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "person_name"
                                )
                        );

                TextView person =
                        new TextView(this);

                person.setText(
                        "👤 " + personName + "\n\n" +
                                "Tap to manage medical conditions"
                );

                person.setTextSize(17);

                person.setPadding(
                        20,
                        20,
                        20,
                        20
                );

                person.setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    MedicalActivity.this,
                                    MedicalPersonActivity.class
                            );

                    intent.putExtra(
                            "person_id",
                            personId
                    );

                    intent.putExtra(
                            "person_name",
                            personName
                    );

                    startActivity(intent);
                });

                person.setOnLongClickListener(v -> {

                    new AlertDialog.Builder(
                            MedicalActivity.this
                    )
                            .setTitle("Person")
                            .setItems(
                                    new String[]{
                                            "Rename",
                                            "Delete",
                                            "Share"
                                    },
                                    (dialog, which) -> {

                                        if (which == 0) {

                                            showRenameDialog(
                                                    personId,
                                                    personName
                                            );

                                        } else if (which == 1) {

                                            deletePerson(personId);

                                        } else if (which == 2) {

                                            Toast.makeText(
                                                    MedicalActivity.this,
                                                    "Share feature coming next",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }

                                    }
                            )
                            .show();

                    return true;
                });

                medicalContainer.addView(person);
            }
        }

        cursor.close();
    }


    private void showRenameDialog(int personId, String oldName) {

        EditText input = new EditText(this);

        input.setText(oldName);
        input.setSelectAllOnFocus(true);

        new AlertDialog.Builder(this)
                .setTitle("Rename Person")
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
                                    databaseHelper.renameMedicalPerson(
                                            personId,
                                            newName
                                    );

                            if (updated) {

                                Toast.makeText(
                                        this,
                                        "Person renamed",
                                        Toast.LENGTH_SHORT
                                ).show();

                                showPeople();

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



    private void deletePerson(int personId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Person?")
                .setMessage(
                        "Are you sure you want to delete this person?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            databaseHelper.deleteMedicalPerson(
                                    personId
                            );

                            showPeople();
                        }
                )
                .show();
    }
}