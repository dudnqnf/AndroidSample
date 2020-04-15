package com.line.memo.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLite {
    DBHelper dbHelper;
    SQLiteDatabase database;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Memo.db";

    public SQLite(Context context) {
        dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void onCreate() {
        dbHelper.onCreate(database);
    }

    public void onUpgrade() {
        dbHelper.onUpgrade(database, 0, 0);
    }
}