package com.example.bookmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.bookmanager.R;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/14 19:01
 * @Version
 */
public class SQLHelper extends SQLiteOpenHelper {
    private final String createBook = "create table Book(" +
            "id integer primary key autoincrement," +
            "name text not null," +
            "author text not null," +
            "addTime text not null," +
            "progress integer not null," +
            "page integer default 0)";

    private final String createFinishBook = "create table FinishBook(" +
            "id integer primary key autoincrement," +
            "name text not null," +
            "author text not null," +
            "page integer default 0," +
            "addTime text not null," +
            "endTime text not null)";

    private final String renameProgressBook = "alter table Book rename to ProgressBook";
    private final String addHistoryInProgress = "alter table ProgressBook add column history text";
    private final String addHistoryInFinish = "alter table FinishBook add column history text";
    private final String addCoverInProgress = "alter table ProgressBook add column cover text";
    private final String addCoverInFinish = "alter table FinishBook add column cover text";
    private final String addUrlInProgress = "alter table ProgressBook add column coverUrl text";
    private final String addUrlInFinish = "alter table FinishBook add column coverUrl text";
    public SQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createBook);
        sqLiteDatabase.execSQL(createFinishBook);
        sqLiteDatabase.execSQL(renameProgressBook);
        sqLiteDatabase.execSQL(addHistoryInProgress);
        sqLiteDatabase.execSQL(addHistoryInFinish);
        sqLiteDatabase.execSQL(addCoverInProgress);
        sqLiteDatabase.execSQL(addCoverInFinish);
        sqLiteDatabase.execSQL(addUrlInProgress);
        sqLiteDatabase.execSQL(addUrlInFinish);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        switch (i) {
            case 1:
                sqLiteDatabase.execSQL(createFinishBook);
            case 2:
                sqLiteDatabase.execSQL(renameProgressBook);
            case 3:
                sqLiteDatabase.execSQL(addHistoryInProgress);
                sqLiteDatabase.execSQL(addHistoryInFinish);
            case 4:
                sqLiteDatabase.execSQL(addCoverInProgress);
                sqLiteDatabase.execSQL(addCoverInFinish);
            case 5:
                sqLiteDatabase.execSQL(addUrlInProgress);
                sqLiteDatabase.execSQL(addUrlInFinish);
                break;
        }
    }
}
