package com.example.day2day;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {

    EditText productName;
    EditText brand;
    EditText model;
    EditText purchaseDate;
    EditText price;
    EditText warranty;

    DatabaseHelper databaseHelper;

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

        Button saveProductButton =
                findViewById(R.id.saveProductButton);

        databaseHelper =
                new DatabaseHelper(this);


        // Purchase Date
        purchaseDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            this,
                            (view, year, month, dayOfMonth) -> {

                                String date =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d/%02d/%04d",
                                                dayOfMonth,
                                                month + 1,
                                                year
                                        );

                                purchaseDate.setText(date);
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );

            dialog.show();
        });


        // Warranty Expiry Date
        warranty.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            this,
                            (view, year, month, dayOfMonth) -> {

                                String date =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d/%02d/%04d",
                                                dayOfMonth,
                                                month + 1,
                                                year
                                        );

                                warranty.setText(date);
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );

            dialog.show();
        });


        // Save Product
        saveProductButton.setOnClickListener(v -> {

            String name =
                    productName.getText().toString().trim();

            String brandName =
                    brand.getText().toString().trim();

            String modelNumber =
                    model.getText().toString().trim();

            String purchase =
                    purchaseDate.getText().toString().trim();

            String productPrice =
                    price.getText().toString().trim();

            String warrantyDate =
                    warranty.getText().toString().trim();


            if (name.isEmpty() ||
                    brandName.isEmpty() ||
                    purchase.isEmpty() ||
                    warrantyDate.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please fill all required fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            boolean inserted =
                    databaseHelper.addProduct(
                            name,
                            brandName,
                            modelNumber,
                            purchase,
                            productPrice,
                            warrantyDate
                    );


            if (inserted) {

                Toast.makeText(
                        this,
                        "Product saved successfully",
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
        });
    }
}