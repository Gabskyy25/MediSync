package com.example.medisync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notifications.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_NAME = "notifications";

    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_MESSAGE = "message";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_ENTITY_TYPE = "entityType";
    public static final String COL_ENTITY_ID = "entityId";

    public NotificationDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_TITLE + " TEXT, " +
                        COL_MESSAGE + " TEXT, " +
                        COL_TIMESTAMP + " TEXT, " +
                        COL_ENTITY_TYPE + " TEXT, " +
                        COL_ENTITY_ID + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ADD NOTIFICATION
    public void addNotification(String title, String message,
                                String entityType, int entityId) {

        SQLiteDatabase db = getWritableDatabase();

        String time = new SimpleDateFormat(
                "MMM dd, yyyy hh:mm a",
                Locale.getDefault()
        ).format(new Date());

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_MESSAGE, message);
        values.put(COL_TIMESTAMP, time);
        values.put(COL_ENTITY_TYPE, entityType);
        values.put(COL_ENTITY_ID, entityId);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // GET ALL NOTIFICATIONS
    public List<NotificationModel> getAllNotifications() {

        List<NotificationModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );

        if (c.moveToFirst()) {
            do {
                list.add(new NotificationModel(
                        c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                        c.getString(c.getColumnIndexOrThrow(COL_MESSAGE)),
                        c.getString(c.getColumnIndexOrThrow(COL_TIMESTAMP)),
                        c.getString(c.getColumnIndexOrThrow(COL_ENTITY_TYPE)),
                        c.getInt(c.getColumnIndexOrThrow(COL_ENTITY_ID))
                ));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // DELETE SINGLE NOTIFICATION
    public void deleteNotification(int notificationId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_NAME,
                COL_ID + "=?",
                new String[]{String.valueOf(notificationId)}
        );
        db.close();
    }

    // DELETE NOTIFICATION WHEN RELATED DATA IS DELETED
    public void deleteNotificationByEntity(String entityType, int entityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_NAME,
                COL_ENTITY_TYPE + "=? AND " + COL_ENTITY_ID + "=?",
                new String[]{entityType, String.valueOf(entityId)}
        );
        db.close();
    }

    public void deleteByEntity(String entityType, int entityId) {
        deleteNotificationByEntity(entityType, entityId);
    }

    // CLEAR ALL NOTIFICATIONS
    public void clearAllNotifications() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
