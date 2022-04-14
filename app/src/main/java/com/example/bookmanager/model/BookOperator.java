package com.example.bookmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.BookSQLHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/14 20:03
 * @Version
 */
public class BookOperator implements IBookOperator {
    private Context context;
    private BookSQLHelper helper;

    public BookOperator() {}

    public BookOperator(Context context) {
        this.context = context;
        helper = new BookSQLHelper(context, "BookManager.db", null, 1);
    }

    @Override
    public void insert(Book book, BookOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Book> data = null;
        boolean resultFlag = false;
        // 插入前查询是否存在相同记录
        String queryExistsSQL = "select * from Book where name=? and author=?";
        Cursor cursor = db.rawQuery(queryExistsSQL, new String[]{book.getName(),book.getAuthor()});
        // 若存在相同记录则更新，否则插入
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("name",book.getName());
                values.put("author",book.getAuthor());
                values.put("addTime",book.getAddTime());
                values.put("progress",book.getProgress());
                values.put("page",book.getPage());
                int row = db.update("Book",values,"name=? and author=?",new String[]{
                        book.getName(),
                        book.getAuthor()
                });
                if (row != 0) {
                    resultFlag = true;
                }
            } else {
                ContentValues values = new ContentValues();
                values.put("name",book.getName());
                values.put("author",book.getAuthor());
                values.put("addTime",book.getAddTime());
                values.put("progress",book.getProgress());
                values.put("page",book.getPage());
                long insertId = db.insert("Book",null,values);
                if (insertId != -1) {
                    resultFlag = true;
                }
            }
            cursor.close();
            data = getDataAfterOperate();
            if (data != null) {
                resultFlag = true;
            }
        }
        if (resultFlag) {
            listener.onSuccess(data);
        } else {
            listener.onError(BookErrorType.INSERT_ERROR);
        }
    }

    @Override
    public void query(BookOperatorListener listener) {
        List<Book> data = getDataAfterOperate();
        if (data != null) {
            listener.onSuccess(data);
        } else {
            listener.onError(BookErrorType.QUERY_ERROR);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void delete(Book book) {

    }

    @Override
    public void swap() {

    }

    private List<Book> getDataAfterOperate() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String querySQL = "select * from Book";
        List<Book> data = new ArrayList<>();
        Cursor cursor = db.rawQuery(querySQL, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String author = cursor.getString(2);
                String addTime = cursor.getString(3);
                int progress = cursor.getInt(4);
                int page = cursor.getInt(5);
                data.add(new Book(name, author, addTime, progress, page));
            }
            cursor.close();
            return data;
        }
        return null;
    }
}
