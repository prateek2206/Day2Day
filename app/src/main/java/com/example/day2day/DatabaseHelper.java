
package com.example.day2day;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Day2Day.db";
    private static final int DATABASE_VERSION = 12;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ================= PRODUCTS =================

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


        // ================= MEDICAL =================

        db.execSQL(
                "CREATE TABLE medical_records (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "person_name TEXT," +
                        "record_type TEXT," +
                        "doctor TEXT," +
                        "record_date TEXT," +
                        "reminder_date TEXT," +
                        "notes TEXT)"
        );


        // ================= VEHICLES =================

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


        // ================= VEHICLE DOCUMENTS =================

        db.execSQL(
                "CREATE TABLE vehicle_documents (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "vehicle_id INTEGER," +
                        "document_type TEXT," +
                        "document_name TEXT," +
                        "expiry_date TEXT," +
                        "file_path TEXT)"
        );


        // ================= DOCUMENTS =================

        db.execSQL(
                "CREATE TABLE documents (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "document_name TEXT," +
                        "document_type TEXT," +
                        "description TEXT," +
                        "expiry_date TEXT)"
        );


        // ================= IMPORTED FILES =================

        db.execSQL(
                "CREATE TABLE imported_files (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "file_name TEXT," +
                        "file_type TEXT," +
                        "section TEXT," +
                        "product_id INTEGER," +
                        "file_path TEXT)"
        );


        // Vehicle Service History table
        db.execSQL(
                "CREATE TABLE vehicle_services (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "vehicle_id INTEGER," +
                        "service_date TEXT," +
                        "service_center TEXT," +
                        "service_details TEXT," +
                        "file_path TEXT)"
        );

        db.execSQL(
                "CREATE TABLE vehicle_service_documents (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "service_id INTEGER," +
                        "document_name TEXT," +
                        "document_type TEXT," +
                        "file_path TEXT)"
        );

        db.execSQL(
                "CREATE TABLE medical_persons (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "person_name TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE medical_conditions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "person_id INTEGER," +
                        "condition_name TEXT NOT NULL," +
                        "notes TEXT," +
                        "next_appointment TEXT)"
        );

        db.execSQL(
                "CREATE TABLE medical_documents (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "condition_id INTEGER," +
                        "document_type TEXT," +
                        "document_name TEXT," +
                        "file_path TEXT)"
        );

    }


    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Version 7:
        // Added vehicle_documents table.
        if (oldVersion < 7) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS vehicle_documents (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "vehicle_id INTEGER," +
                            "document_type TEXT," +
                            "document_name TEXT," +
                            "expiry_date TEXT," +
                            "file_path TEXT)"
            );
        }

        // Version 8:
        // Added vehicle service history table.
        if (oldVersion < 8) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS vehicle_services (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "vehicle_id INTEGER," +
                            "service_date TEXT," +
                            "service_center TEXT," +
                            "service_details TEXT," +
                            "file_path TEXT)"
            );
        }

        if (oldVersion < 9) {

            db.execSQL(
                    "ALTER TABLE medical_records " +
                            "ADD COLUMN reminder_date TEXT"
            );
        }

        if (oldVersion < 10) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS medical_persons (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "person_name TEXT NOT NULL)"
            );

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS medical_conditions (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "person_id INTEGER," +
                            "condition_name TEXT NOT NULL," +
                            "notes TEXT," +
                            "next_appointment TEXT)"
            );
        }

        if (oldVersion < 11) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS medical_documents (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "condition_id INTEGER," +
                            "document_type TEXT," +
                            "document_name TEXT," +
                            "file_path TEXT)"
            );
        }

        if (oldVersion < 12) {

            db.execSQL(
                    "CREATE TABLE vehicle_service_documents (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "service_id INTEGER," +
                            "document_name TEXT," +
                            "document_type TEXT," +
                            "file_path TEXT)"
            );
        }
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

        long result = db.insert(
                "products",
                null,
                values
        );

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

    public boolean renameProduct(
            int productId,
            String newName) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("name", newName);

        int result =
                db.update(
                        "products",
                        values,
                        "id = ?",
                        new String[]{
                                String.valueOf(productId)
                        }
                );

        db.close();

        return result > 0;
    }

    public boolean deleteProduct(int productId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "products",
                "id = ?",
                new String[]{
                        String.valueOf(productId)
                }
        );

        db.close();

        return result > 0;
    }


    // ================= MEDICAL =================

    public boolean addMedicalRecord(
            String personName,
            String recordType,
            String doctor,
            String recordDate,
            String reminderDate,
            String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("person_name", personName);
        values.put("record_type", recordType);
        values.put("doctor", doctor);
        values.put("record_date", recordDate);
        values.put("reminder_date", reminderDate);
        values.put("notes", notes);

        long result = db.insert(
                "medical_records",
                null,
                values
        );

        db.close();

        return result != -1;
    }

    public boolean deleteMedicalRecord(int recordId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "medical_records",
                "id = ?",
                new String[]{
                        String.valueOf(recordId)
                }
        );

        db.close();

        return result > 0;
    }


    public Cursor getAllMedicalRecords() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM medical_records ORDER BY id DESC",
                null
        );
    }

    public long addMedicalPerson(String personName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("person_name", personName);

        long result = db.insert(
                "medical_persons",
                null,
                values
        );

        db.close();

        return result;
    }

    public Cursor getAllMedicalPersons() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM medical_persons ORDER BY id DESC",
                null
        );
    }

    public boolean deleteMedicalPerson(int personId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "medical_persons",
                "id = ?",
                new String[]{
                        String.valueOf(personId)
                }
        );

        db.close();

        return result > 0;
    }

    public long addMedicalCondition(
            int personId,
            String conditionName,
            String notes,
            String nextAppointment) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("person_id", personId);
        values.put("condition_name", conditionName);
        values.put("notes", notes);
        values.put("next_appointment", nextAppointment);

        long result = db.insert(
                "medical_conditions",
                null,
                values
        );

        db.close();

        return result;
    }

    public Cursor getMedicalConditions(int personId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM medical_conditions " +
                        "WHERE person_id = ? " +
                        "ORDER BY id DESC",
                new String[]{
                        String.valueOf(personId)
                }
        );
    }

    public boolean deleteMedicalCondition(int conditionId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "medical_conditions",
                "id = ?",
                new String[]{
                        String.valueOf(conditionId)
                }
        );

        db.close();

        return result > 0;
    }

    public boolean updateMedicalAppointment(
            int conditionId,
            String appointmentDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                "next_appointment",
                appointmentDate
        );

        int result = db.update(
                "medical_conditions",
                values,
                "id = ?",
                new String[]{
                        String.valueOf(conditionId)
                }
        );

        db.close();

        return result > 0;
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


    // ================= VEHICLE DOCUMENTS =================

    public boolean addVehicleDocument(
            int vehicleId,
            String documentType,
            String documentName,
            String expiryDate,
            String filePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        // RC and PUC can have only one document per vehicle
        if (documentType.equals("RC")
                || documentType.equals("PUC")) {

            // Find the existing RC/PUC document
            Cursor cursor = db.query(
                    "vehicle_documents",
                    new String[]{"file_path"},
                    "vehicle_id = ? AND document_type = ?",
                    new String[]{
                            String.valueOf(vehicleId),
                            documentType
                    },
                    null,
                    null,
                    null
            );

            // Delete old database record
            if (cursor.moveToFirst()) {

                String oldFilePath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "file_path"
                                )
                        );

                db.delete(
                        "vehicle_documents",
                        "vehicle_id = ? AND document_type = ?",
                        new String[]{
                                String.valueOf(vehicleId),
                                documentType
                        }
                );

                // Delete old physical file
                if (oldFilePath != null
                        && !oldFilePath.isEmpty()) {

                    java.io.File oldFile =
                            new java.io.File(oldFilePath);

                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
            }

            cursor.close();
        }

        // Add the new document
        ContentValues values = new ContentValues();

        values.put("vehicle_id", vehicleId);
        values.put("document_type", documentType);
        values.put("document_name", documentName);
        values.put("expiry_date", expiryDate);
        values.put("file_path", filePath);

        long result = db.insert(
                "vehicle_documents",
                null,
                values
        );

        db.close();

        return result != -1;
    }


    public Cursor getVehicleDocuments(int vehicleId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                "vehicle_documents",
                null,
                "vehicle_id = ?",
                new String[]{
                        String.valueOf(vehicleId)
                },
                null,
                null,
                "id DESC"
        );
    }

    public Cursor getExpiringVehicleDocuments() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        "vehicle_documents.id, " +
                        "vehicles.vehicle_name, " +
                        "vehicles.vehicle_number, " +
                        "vehicle_documents.document_type, " +
                        "vehicle_documents.expiry_date, " +
                        "vehicle_documents.file_path " +
                        "FROM vehicle_documents " +
                        "INNER JOIN vehicles " +
                        "ON vehicle_documents.vehicle_id = vehicles.id " +
                        "WHERE vehicle_documents.expiry_date IS NOT NULL " +
                        "AND vehicle_documents.expiry_date != '' " +
                        "ORDER BY vehicle_documents.expiry_date ASC",
                null
        );
    }


    // ================= VEHICLE SERVICE HISTORY =================

    public long addVehicleService(
            int vehicleId,
            String serviceDate,
            String serviceCenter,
            String serviceDetails,
            String filePath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("vehicle_id", vehicleId);
        values.put("service_date", serviceDate);
        values.put("service_center", serviceCenter);
        values.put("service_details", serviceDetails);
        values.put("file_path", filePath);

        long result =
                db.insert(
                        "vehicle_services",
                        null,
                        values
                );

        db.close();

        return result;
    }

    public boolean addVehicleServiceDocument(
            long serviceId,
            String documentName,
            String documentType,
            String filePath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "service_id",
                serviceId
        );

        values.put(
                "document_name",
                documentName
        );

        values.put(
                "document_type",
                documentType
        );

        values.put(
                "file_path",
                filePath
        );

        long result =
                db.insert(
                        "vehicle_service_documents",
                        null,
                        values
                );

        db.close();

        return result != -1;
    }



    public Cursor getVehicleServices(int vehicleId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                "vehicle_services",
                null,
                "vehicle_id = ?",
                new String[]{
                        String.valueOf(vehicleId)
                },
                null,
                null,
                "id DESC"
        );
    }

    public Cursor getVehicleServiceDocuments(long serviceId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM vehicle_service_documents " +
                        "WHERE service_id = ? " +
                        "ORDER BY id DESC",
                new String[]{
                        String.valueOf(serviceId)
                }
        );
    }

    public boolean deleteVehicleServiceDocument(long documentId) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        "vehicle_service_documents",
                        "id = ?",
                        new String[]{
                                String.valueOf(documentId)
                        }
                );

        db.close();

        return result > 0;
    }

    public boolean deleteVehicleServiceDocuments(long serviceId) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        "vehicle_service_documents",
                        "service_id = ?",
                        new String[]{
                                String.valueOf(serviceId)
                        }
                );

        db.close();

        return result > 0;
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


    // ================= IMPORTED FILES =================

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


    public boolean addProductFile(
            String fileName,
            String fileType,
            int productId,
            String filePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("file_name", fileName);
        values.put("file_type", fileType);
        values.put("section", "Product");
        values.put("product_id", productId);
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
                new String[]{
                        section
                }
        );
    }


    public Cursor getProductFiles(int productId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM imported_files " +
                        "WHERE section = ? AND product_id = ? " +
                        "ORDER BY id DESC",
                new String[]{
                        "Product",
                        String.valueOf(productId)
                }
        );
    }


    public void deleteImportedFile(String filePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                "imported_files",
                "file_path = ?",
                new String[]{
                        filePath
                }
        );

        db.close();
    }


    public void renameImportedFile(
            String oldPath,
            String newName,
            String newPath) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("file_name", newName);
        values.put("file_path", newPath);

        db.update(
                "imported_files",
                values,
                "file_path = ?",
                new String[]{
                        oldPath
                }
        );

        db.close();
    }

    public boolean addMedicalDocument(
            int conditionId,
            String documentType,
            String documentName,
            String filePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("condition_id", conditionId);
        values.put("document_type", documentType);
        values.put("document_name", documentName);
        values.put("file_path", filePath);

        long result = db.insert(
                "medical_documents",
                null,
                values
        );

        db.close();

        return result != -1;
    }

    public Cursor getMedicalDocuments(int conditionId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM medical_documents " +
                        "WHERE condition_id = ? " +
                        "ORDER BY id DESC",
                new String[]{
                        String.valueOf(conditionId)
                }
        );
    }

    public boolean deleteMedicalDocument(int documentId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "medical_documents",
                "id = ?",
                new String[]{
                        String.valueOf(documentId)
                }
        );

        db.close();

        return result > 0;
    }

    public boolean renameMedicalPerson(
            int personId,
            String newName) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "person_name",
                newName
        );

        int result =
                db.update(
                        "medical_persons",
                        values,
                        "id = ?",
                        new String[]{
                                String.valueOf(personId)
                        }
                );

        db.close();

        return result > 0;
    }


    public boolean renameMedicalCondition(
            int conditionId,
            String newName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("condition_name", newName);

        int result = db.update(
                "medical_conditions",
                values,
                "id = ?",
                new String[]{String.valueOf(conditionId)}
        );

        db.close();

        return result > 0;
    }


    public boolean renameProductFile(
            String oldPath,
            String newName,
            String newPath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("file_name", newName);
        values.put("file_path", newPath);

        int result =
                db.update(
                        "product_files",
                        values,
                        "file_path = ?",
                        new String[]{oldPath}
                );

        db.close();

        return result > 0;
    }

    public boolean deleteProductFile(
            String filePath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        "product_files",
                        "file_path = ?",
                        new String[]{filePath}
                );

        db.close();

        return result > 0;
    }

    public boolean renameVehicle(
            int vehicleId,
            String newName) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("vehicle_name", newName);

        int result =
                db.update(
                        "vehicles",
                        values,
                        "id = ?",
                        new String[]{
                                String.valueOf(vehicleId)
                        }
                );

        db.close();

        return result > 0;
    }

    public boolean deleteVehicle(
            int vehicleId) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        "vehicles",
                        "id = ?",
                        new String[]{
                                String.valueOf(vehicleId)
                        }
                );

        db.close();

        return result > 0;
    }

    public boolean renameVehicleDocument(
            String oldPath,
            String newName,
            String newPath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("document_name", newName);
        values.put("file_path", newPath);

        int result =
                db.update(
                        "vehicle_documents",
                        values,
                        "file_path = ?",
                        new String[]{oldPath}
                );

        db.close();

        return result > 0;
    }

    public boolean deleteVehicleDocument(
            String filePath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        "vehicle_documents",
                        "file_path = ?",
                        new String[]{filePath}
                );

        db.close();

        return result > 0;
    }

    public boolean renameMedicalDocument(
            int documentId,
            String newName) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "document_name",
                newName
        );

        int result =
                db.update(
                        "medical_documents",
                        values,
                        "id = ?",
                        new String[]{
                                String.valueOf(documentId)
                        }
                );

        db.close();

        return result > 0;
    }

    public boolean renameVehicleService(
            int serviceId,
            String newName) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "service_details",
                newName
        );

        int result =
                db.update(
                        "vehicle_services",
                        values,
                        "id = ?",
                        new String[]{
                                String.valueOf(serviceId)
                        }
                );

        db.close();

        return result > 0;
    }

    public boolean deleteVehicleService(
            int serviceId) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        "vehicle_services",
                        "id = ?",
                        new String[]{
                                String.valueOf(serviceId)
                        }
                );

        db.close();

        return result > 0;
    }

}
