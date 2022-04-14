package com.example.bookmanager.model;

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
        List<Book> data = new ArrayList<>();
        String insertSQL = "replace into Book values(?,?,?,?,?)";
        db.execSQL(insertSQL, new Object[]{
                book.getName(),
                book.getAuthor(),
                book.getAddTime(),
                book.getProgress(),
                book.getPage()
        });
        String querySQL = "select * from Book";
        Cursor cursor = db.rawQuery(querySQL, null);
        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String author = cursor.getString(2);
                String addTime = cursor.getString(3);
                int progress = cursor.getInt(4);
                int page = cursor.getInt(5);
                data.add(new Book(name, author, addTime, progress, page));
            }
            cursor.close();
            listener.onSuccess(data);
            return;
        }
        listener.onError(BookErrorType.INSERT_ERROR);
    }

    @Override
    public void query(BookOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Book> data = new ArrayList<>();
        String querySQL = "select * from Book";
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
            listener.onSuccess(data);
            return;
        }
        listener.onError(BookErrorType.QUERY_ERROR);
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
}
