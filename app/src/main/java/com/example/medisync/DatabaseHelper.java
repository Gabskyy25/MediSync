package com.example.medisync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.medisync.Patient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "patients.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PATIENTS = "patients";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_BIRTHDATE = "birthdate";
    public static final String COLUMN_DISEASE = "disease";

    private static final String CREATE_TABLE_PATIENTS = "CREATE TABLE " + TABLE_PATIENTS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_AGE + " INTEGER, " +
            COLUMN_BIRTHDATE + " TEXT, " +
            COLUMN_DISEASE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PATIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
        onCreate(db);
    }

    // Insert a patient
    public long insertPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, patient.getName());
        values.put(COLUMN_AGE, patient.getAge());
        values.put(COLUMN_BIRTHDATE, patient.getBirthdate());
        values.put(COLUMN_DISEASE, patient.getDisease());
        long id = db.insert(TABLE_PATIENTS, null, values);
        db.close();
        return id;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PATIENTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int ageIndex = cursor.getColumnIndex(COLUMN_AGE);
                int birthdateIndex = cursor.getColumnIndex(COLUMN_BIRTHDATE);
                int diseaseIndex = cursor.getColumnIndex(COLUMN_DISEASE);

                String name = cursor.isNull(nameIndex) ? "" : cursor.getString(nameIndex);
                int age = cursor.isNull(ageIndex) ? 0 : cursor.getInt(ageIndex);
                String birthdate = cursor.isNull(birthdateIndex) ? "" : cursor.getString(birthdateIndex);
                String disease = cursor.isNull(diseaseIndex) ? "" : cursor.getString(diseaseIndex);

                Patient patient = new Patient(name, age, birthdate, disease);
                patient.setId(cursor.getInt(idIndex));
                patients.add(patient);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return patients;
    }

    public void deleteAllPatients() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PATIENTS);
        db.close();
    }
}
