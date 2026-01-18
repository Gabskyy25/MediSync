package com.example.medisync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medisync.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ISSUES = "issues";
    private static final String COL_ID = "id";
    private static final String COL_ISSUE = "issue";
    private static final String COL_RESOLUTION = "resolution";
    private static final String COL_SAVED_AT = "saved_at";

    private static DBHelper sInstance;

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE = "CREATE TABLE " + TABLE_ISSUES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ISSUE + " TEXT, " +
                COL_RESOLUTION + " TEXT, " +
                COL_SAVED_AT + " INTEGER)";
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ISSUES);
        onCreate(db);
    }

    public long addIssue(Issue issue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ISSUE, issue.getIssue());
        values.put(COL_RESOLUTION, issue.getResolution());
        values.put(COL_SAVED_AT, System.currentTimeMillis());
        long id = db.insert(TABLE_ISSUES, null, values);
        db.close();
        return id;
    }

    public List<Issue> getAllIssues() {
        List<Issue> list = new ArrayList<>();
        String select = "SELECT * FROM " + TABLE_ISSUES + " ORDER BY " + COL_SAVED_AT + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(select, null);
        if (c.moveToFirst()) {
            do {
                long id = c.getLong(c.getColumnIndexOrThrow(COL_ID));
                String issue = c.getString(c.getColumnIndexOrThrow(COL_ISSUE));
                String resolution = c.getString(c.getColumnIndexOrThrow(COL_RESOLUTION));
                long savedAt = c.getLong(c.getColumnIndexOrThrow(COL_SAVED_AT));
                list.add(new Issue(id, issue, resolution, savedAt));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public void deleteIssue(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ISSUES, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateIssue(long id, String issue, String resolution) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ISSUE, issue);
        values.put(COL_RESOLUTION, resolution);
        db.update(TABLE_ISSUES, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
