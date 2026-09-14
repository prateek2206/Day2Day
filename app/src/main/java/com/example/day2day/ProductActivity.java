package com.example.day2day;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProductActivity extends AppCompatActivity {

    LinearLayout productContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product);

        Button addProductButton =
                findViewById(R.id.addProductButton);

        productContainer =
                findViewById(R.id.productContainer);

        databaseHelper = new DatabaseHelper(this);

        addProductButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProductActivity.this,
                    AddProductActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            showProducts();
        }
    }

    private void showProducts() {

        productContainer.removeAllViews();

        Cursor cursor = databaseHelper.getAllProducts();

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText(
                    "No products added yet.\nTap + Add Product to add one."
            );

            emptyText.setTextSize(16);

            productContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                String name =
                        cursor.getString(cursor.getColumnIndexOrThrow("name"));

                String brand =
                        cursor.getString(cursor.getColumnIndexOrThrow("brand"));

                String model =
                        cursor.getString(cursor.getColumnIndexOrThrow("model"));

                String date =
                        cursor.getString(cursor.getColumnIndexOrThrow("purchase_date"));

                String price =
                        cursor.getString(cursor.getColumnIndexOrThrow("price"));

                String warranty =
                        cursor.getString(cursor.getColumnIndexOrThrow("warranty"));

                TextView product = new TextView(this);

                product.setText(
                        name + "\n" +
                                "Brand: " + brand + "\n" +
                                "Model: " + model + "\n" +
                                "Purchased: " + date + "\n" +
                                "Price: ₹" + price + "\n" +
                                "Warranty: " + warranty
                );

                product.setTextSize(17);
                product.setPadding(20, 20, 20, 20);

                productContainer.addView(product);
            }
        }

        cursor.close();
    }
}