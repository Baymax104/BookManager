package com.example.bookmanager.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/14 19:01
 * @Version
 */
public class SQLHelper extends SQLiteOpenHelper {
    private String createBook = "create table Book(" +
            "id integer primary key autoincrement," +
            "name text not null," +
            "author text not null," +
            "addTime text not null," +
            "progress integer not null," +
            "page integer default 0)";

    private String createFinishBook = "create table FinishBook(" +
            "id integer primary key autoincrement," +
            "name text not null," +
            "author text not null," +
            "page integer default 0," +
            "addTime text not null," +
            "endTime text not null)";
    private String renameProgressBook = "alter table Book rename to ProgressBook";
    public SQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createBook);
        sqLiteDatabase.execSQL(createFinishBook);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 若旧版本为1，则添加表
        switch (i) {
            case 1:
                sqLiteDatabase.execSQL(createFinishBook);
                sqLiteDatabase.execSQL(renameProgressBook);
                break;
        }
    }
}
