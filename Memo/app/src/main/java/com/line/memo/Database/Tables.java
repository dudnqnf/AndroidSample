package com.line.memo.Database;

import android.provider.BaseColumns;

public class Tables {
    public static class Memo {
        public static final String TABLE_NAME = "memo";
        public static String CREATE_TABLE =
                "create table if not exists " + TABLE_NAME + "(" +
                        "id integer primary key autoincrement, " +
                        "title text not null, " +
                        "content text not null" +
                        ");";
    }

    public static class Uri {
        public static final String TABLE_NAME = "uri";
        public static String CREATE_TABLE =
                "create table if not exists " + TABLE_NAME + "(" +
                        "id integer primary key autoincrement, " +
                        "memo_id integer not null, " +
                        "uri text not null, " +
                        "foreign key(memo_id) references memo(id)" +
                        ");";
    }
}