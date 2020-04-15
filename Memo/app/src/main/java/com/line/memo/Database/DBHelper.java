package com.line.memo.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Tables.Memo.CREATE_TABLE);
        db.execSQL(Tables.Uri.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Memo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.Uri.TABLE_NAME);
        onCreate(db);
    }
}