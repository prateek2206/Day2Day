package com.example.day2day;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddProductActivity extends AppCompatActivity {

    EditText productName, brand, model, purchaseDate, price, warranty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_product);

        productName = findViewById(R.id.productName);
        brand = findViewById(R.id.brand);
        model = findViewById(R.id.model);
        purchaseDate = findViewById(R.id.purchaseDate);
        price = findViewById(R.id.price);
        warranty = findViewById(R.id.warranty);

        Button saveButton = findViewById(R.id.saveProductButton);

        purchaseDate.setOnClickListener(v -> showDatePicker());

        saveButton.setOnClickListener(v -> saveProduct());
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

                    purchaseDate.setText(date);
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    private void saveProduct() {

        String name = productName.getText().toString().trim();
        String brandName = brand.getText().toString().trim();
        String modelName = model.getText().toString().trim();
        String date = purchaseDate.getText().toString().trim();
        String productPrice = price.getText().toString().trim();
        String warrantyPeriod = warranty.getText().toString().trim();

        if (name.isEmpty()) {
            productName.setError("Enter product name");
            productName.requestFocus();
            return;
        }

        if (brandName.isEmpty()) {
            brand.setError("Enter brand");
            brand.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            purchaseDate.setError("Select purchase date");
            return;
        }

        if (warrantyPeriod.isEmpty()) {
            warranty.setError("Enter warranty period");
            warranty.requestFocus();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        boolean inserted = databaseHelper.addProduct(
                name,
                brandName,
                modelName,
                date,
                productPrice,
                warrantyPeriod
        );

        if (inserted) {

            Toast.makeText(
                    this,
                    "Product saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to save product",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}