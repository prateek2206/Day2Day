package com.example.day2day;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.productCard).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.medicalCard).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicalActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.vehicleCard).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VehicleActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.documentsCard).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    DocumentsActivity.class
            );

            startActivity(intent);
        });
    }
}