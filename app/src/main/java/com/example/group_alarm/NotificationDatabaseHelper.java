package com.example.group_alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notifications.db";
    private static final int DATABASE_VERSION = 1;

    // 알림 설정 테이블
    public static final String TABLE_REMINDERS = "reminders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_REMINDERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DAY + " TEXT NOT NULL, " +
                    COLUMN_HOUR + " INTEGER NOT NULL, " +
                    COLUMN_MINUTE + " INTEGER NOT NULL" +
                    ");";

    public NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }
}
