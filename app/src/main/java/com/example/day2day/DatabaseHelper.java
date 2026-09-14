package com.example.day2day;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Day2Day.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Products table
        db.execSQL(
                "CREATE TABLE products (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "brand TEXT," +
                        "model TEXT," +
                        "purchase_date TEXT," +
                        "price TEXT," +
                        "warranty TEXT)"
        );

        // Medical records table
        db.execSQL(
                "CREATE TABLE medical_records (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "person_name TEXT," +
                        "record_type TEXT," +
                        "doctor TEXT," +
                        "record_date TEXT," +
                        "notes TEXT)"
        );

        // Vehicles table
        db.execSQL(
                "CREATE TABLE vehicles (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "vehicle_name TEXT," +
                        "vehicle_number TEXT," +
                        "service_date TEXT," +
                        "insurance_expiry TEXT," +
                        "puc_expiry TEXT," +
                        "fuel_expense TEXT)"
        );

        // Documents table
        db.execSQL(
                "CREATE TABLE documents (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "document_name TEXT," +
                        "document_type TEXT," +
                        "description TEXT," +
                        "expiry_date TEXT)"
        );

        // Imported files table
        db.execSQL(
                "CREATE TABLE imported_files (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "file_name TEXT," +
                        "file_type TEXT," +
                        "section TEXT," +
                        "file_path TEXT)"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS medical_records");
        db.execSQL("DROP TABLE IF EXISTS vehicles");
        db.execSQL("DROP TABLE IF EXISTS documents");
        db.execSQL("DROP TABLE IF EXISTS imported_files");

        onCreate(db);
    }

    // ================= PRODUCTS =================

    public boolean addProduct(
            String name,
            String brand,
            String model,
            String purchaseDate,
            String price,
            String warranty) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("brand", brand);
        values.put("model", model);
        values.put("purchase_date", purchaseDate);
        values.put("price", price);
        values.put("warranty", warranty);

        long result = db.insert("products", null, values);

        db.close();

        return result != -1;
    }

    public Cursor getAllProducts() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM products ORDER BY id DESC",
                null
        );
    }

    // ================= MEDICAL =================

    public boolean addMedicalRecord(
            String personName,
            String recordType,
            String doctor,
            String recordDate,
            String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("person_name", personName);
        values.put("record_type", recordType);
        values.put("doctor", doctor);
        values.put("record_date", recordDate);
        values.put("notes", notes);

        long result = db.insert(
                "medical_records",
                null,
                values
        );

        db.close();

        return result != -1;
    }

    public Cursor getAllMedicalRecords() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM medical_records ORDER BY id DESC",
                null
        );
    }

    // ================= VEHICLES =================

    public boolean addVehicle(
            String vehicleName,
            String vehicleNumber,
            String serviceDate,
            String insuranceExpiry,
            String pucExpiry,
            String fuelExpense) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("vehicle_name", vehicleName);
        values.put("vehicle_number", vehicleNumber);
        values.put("service_date", serviceDate);
        values.put("insurance_expiry", insuranceExpiry);
        values.put("puc_expiry", pucExpiry);
        values.put("fuel_expense", fuelExpense);

        long result = db.insert(
                "vehicles",
                null,
                values
        );

        db.close();

        return result != -1;
    }

    public Cursor getAllVehicles() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM vehicles ORDER BY id DESC",
                null
        );
    }

    // ================= DOCUMENTS =================

    public boolean addDocument(
            String documentName,
            String documentType,
            String description,
            String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("document_name", documentName);
        values.put("document_type", documentType);
        values.put("description", description);
        values.put("expiry_date", expiryDate);

        long result = db.insert(
                "documents",
                null,
                values
        );

        db.close();

        return result != -1;
    }

    public Cursor getAllDocuments() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM documents ORDER BY id DESC",
                null
        );
    }

    public boolean addImportedFile(
            String fileName,
            String fileType,
            String section,
            String filePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("file_name", fileName);
        values.put("file_type", fileType);
        values.put("section", section);
        values.put("file_path", filePath);

        long result = db.insert(
                "imported_files",
                null,
                values
        );

        db.close();

        return result != -1;
    }

    public Cursor getImportedFiles(String section) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM imported_files " +
                        "WHERE section = ? " +
                        "ORDER BY id DESC",
                new String[]{section}
        );
    }
}
