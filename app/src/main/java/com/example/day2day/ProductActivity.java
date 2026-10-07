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

public class ProductActivity extends AppCompatActivity {

    LinearLayout productContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product);

        Button addProductButton =
                findViewById(R.id.addProductButton);

        Button addProductDocumentButton =
                findViewById(R.id.addProductDocumentButton);

        addProductDocumentButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProductActivity.this,
                    DocumentsActivity.class
            );

            intent.putExtra("section", "Product");

            startActivity(intent);
        });

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

                int productId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                LinearLayout productLayout = new LinearLayout(this);

                productLayout.setOrientation(LinearLayout.VERTICAL);
                productLayout.setPadding(20, 20, 20, 20);

                TextView product = new TextView(this);

                product.setText(
                        "📦 " + name + "\n" +
                                "Brand: " + brand + "\n" +
                                "Model: " + model + "\n" +
                                "Purchased: " + date + "\n" +
                                "Price: ₹" + price + "\n" +
                                "Warranty: " + warranty
                );

                product.setTextSize(17);

                product.setOnLongClickListener(v -> {

                    new AlertDialog.Builder(
                            ProductActivity.this
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

                                            showRenameProductDialog(
                                                    productId,
                                                    name
                                            );

                                        } else if (which == 1) {

                                            deleteProduct(productId);

                                        } else if (which == 2) {

                                            Toast.makeText(
                                                    ProductActivity.this,
                                                    "Product sharing will be available with product documents.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                            )
                            .show();

                    return true;
                });

                productLayout.addView(product);


                Button documentButton = new Button(this);

                documentButton.setText("+ Add Document");

                documentButton.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ProductActivity.this,
                            DocumentsActivity.class
                    );

                    intent.putExtra("section", "Product");
                    intent.putExtra("productId", productId);

                    startActivity(intent);
                });

                productLayout.addView(documentButton);


                product.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ProductActivity.this,
                            DocumentsActivity.class
                    );

                    intent.putExtra("section", "Product");
                    intent.putExtra("productId", productId);

                    startActivity(intent);
                });


                productContainer.addView(productLayout);
            }
        }

        cursor.close();
    }

    private void showRenameProductDialog(
            int productId,
            String oldName) {

        EditText input =
                new EditText(this);

        input.setText(oldName);
        input.setSelectAllOnFocus(true);

        new AlertDialog.Builder(this)
                .setTitle("Rename Product")
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
                                    databaseHelper.renameProduct(
                                            productId,
                                            newName
                                    );

                            if (updated) {

                                Toast.makeText(
                                        this,
                                        "Product renamed",
                                        Toast.LENGTH_SHORT
                                ).show();

                                showProducts();

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

    private void deleteProduct(int productId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Product?")
                .setMessage(
                        "Are you sure you want to delete this product?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            databaseHelper.deleteProduct(
                                    productId
                            );

                            showProducts();

                            Toast.makeText(
                                    this,
                                    "Product deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

}