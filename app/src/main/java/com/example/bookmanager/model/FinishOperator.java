package com.example.bookmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.FinishBook;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/18 14:34
 * @Version
 */
public class FinishOperator implements BookOperator {

    private Context context;
    private SQLHelper helper;
    private final String tableName = "FinishBook";

    public FinishOperator() {
    }

    public FinishOperator(Context context) {
        this.context = context;
        helper = new SQLHelper(context,"BookManager.db",null, VERSION);
    }

    @Override
    public void insert(Book book1, BookOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        FinishBook book = (FinishBook) book1;
        List<Book> data;
        // 插入前查询是否存在相同记录
        String queryExistsSQL = "select * from FinishBook where name=? and author=?";
        db.beginTransaction();
        try (Cursor cursor = db.rawQuery(queryExistsSQL, new String[]{book.getName(), book.getAuthor()})) {

            // 若存在相同记录则更新，否则插入
            if (cursor == null) {
                throw new BookException("数据不存在", BookException.ErrorType.INSERT_ERROR);
            }
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("name", book.getName());
                values.put("author", book.getAuthor());
                values.put("page", book.getPage());
                values.put("addTime", book.getAddTime());
                values.put("endTime", book.getEndTime());
                values.put("history",book.getHistory());
                values.put("cover", book.getCover());
                values.put("coverUrl",book.getCoverUrl());
                int row = db.update(tableName,values,"name=? and author=?",new String[]{
                        book.getName(),
                        book.getAuthor()
                });
                if (row == 0) {
                    throw new BookException("更新失败", BookException.ErrorType.INSERT_ERROR);
                }
            } else {
                ContentValues values = new ContentValues();
                values.put("name", book.getName());
                values.put("author", book.getAuthor());
                values.put("page", book.getPage());
                values.put("addTime", book.getAddTime());
                values.put("endTime", book.getEndTime());
                values.put("history", book.getHistory());
                values.put("cover", book.getCover());
                values.put("coverUrl", book.getCoverUrl());
                long insertId = db.insert(tableName,null,values);
                if (insertId == -1) {
                    throw new BookException("添加失败", BookException.ErrorType.INSERT_ERROR);
                }
            }
            data = getDataAfterOperate();
            if (data == null) {
                throw new BookException("返回数据失败", BookException.ErrorType.INSERT_ERROR);
            }
            listener.onSuccess(data);
            db.setTransactionSuccessful();
        } catch (BookException e) {
            listener.onError(e);
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void query(BookOperatorListener listener) {
        try {
            List<Book> data = getDataAfterOperate();
            if (data == null) {
                throw new BookException("查询失败", BookException.ErrorType.QUERY_ERROR);
            }
            listener.onSuccess(data);
        } catch (BookException e) {
            listener.onError(e);
        }
    }

    @Override
    public void update(Book book1, BookOperatorListener listener) {
    }

    @Override
    public void delete(Book book1, int position, BookOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        FinishBook book = (FinishBook) book1;
        List<Book> data;
        db.beginTransaction();
        try {
            int row = db.delete(tableName, "name=? and author=?", new String[]{
                    book.getName(),book.getAuthor()
            });
            if (row == 0) {
                throw new BookException("删除失败", BookException.ErrorType.DELETE_ERROR);
            }
            deleteItemTable(book);
            data = getDataAfterOperate();
            if (data == null) {
                throw new BookException("返回数据失败", BookException.ErrorType.DELETE_ERROR);
            }
            listener.onSuccess(data, position);
            db.setTransactionSuccessful();
        } catch (BookException e) {
            listener.onError(e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void swap(Book fromBook1, Book toBook1, int fromPosition, int toPosition, BookOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        FinishBook fromBook = (FinishBook) fromBook1;
        FinishBook toBook = (FinishBook) toBook1;
        List<Book> data;
        String fromId, toId;
        // 分别更新两个Book实现交换
        String queryExistsSQL = "select id from FinishBook where name=? and author=?";
        db.beginTransaction();
        try (Cursor fromCursor = db.rawQuery(queryExistsSQL, new String[]{fromBook.getName(), fromBook.getAuthor()});
             Cursor toCursor = db.rawQuery(queryExistsSQL, new String[]{toBook.getName(), toBook.getAuthor()})) {
            // 查询fromBook的id
            if (fromCursor == null || toCursor == null || !fromCursor.moveToFirst() || !toCursor.moveToFirst()) {
                throw new BookException("查询失败", BookException.ErrorType.SORT_ERROR);
            }
            fromId = fromCursor.getString(0);
            toId = toCursor.getString(0);
            if (fromId == null || toId == null) {
                throw new BookException("获取id失败", BookException.ErrorType.SORT_ERROR);
            }
            // 更新toId的数据
            ContentValues values = new ContentValues();
            values.put("name", fromBook.getName());
            values.put("author", fromBook.getAuthor());
            values.put("page", fromBook.getPage());
            values.put("addTime", fromBook.getAddTime());
            values.put("endTime", fromBook.getEndTime());
            values.put("history", fromBook.getHistory());
            values.put("cover", fromBook.getCover());
            values.put("coverUrl", fromBook.getCoverUrl());
            int row = db.update(tableName,values,"id=?",new String[]{toId});
            if (row == 0) {
                throw new BookException("更新失败", BookException.ErrorType.SORT_ERROR);
            }
            // 更新fromId的数据
            values = new ContentValues();
            values.put("name", toBook.getName());
            values.put("author", toBook.getAuthor());
            values.put("page", toBook.getPage());
            values.put("addTime", toBook.getAddTime());
            values.put("endTime", toBook.getEndTime());
            values.put("history", toBook.getHistory());
            values.put("cover", toBook.getCover());
            values.put("coverUrl",toBook.getCoverUrl());
            row = db.update(tableName,values, "id=?",new String[]{fromId});
            if (row == 0) {
                throw new BookException("更新失败", BookException.ErrorType.SORT_ERROR);
            }
            data = getDataAfterOperate();
            if (data == null) {
                throw new BookException("返回数据失败", BookException.ErrorType.SORT_ERROR);
            }
            listener.onSuccess(data, fromPosition, toPosition);
            db.setTransactionSuccessful();
        } catch (BookException e) {
            listener.onError(e);
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public List<Book> getDataAfterOperate() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String querySQL = "select * from FinishBook";
        List<FinishBook> data = new ArrayList<>();
        Cursor cursor = db.rawQuery(querySQL, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String author = cursor.getString(2);
                int page = cursor.getInt(3);
                String addTime = cursor.getString(4);
                String endTime = cursor.getString(5);
                String history = cursor.getString(6);
                String cover = cursor.getString(7);
                String coverUrl = cursor.getString(8);
                data.add(new FinishBook(
                        name,author,page,
                        addTime,endTime,history,cover,coverUrl
                ));
            }
            cursor.close();
            return new ArrayList<>(data);
        }
        return null;
    }

    @Override
    public void deleteItemTable(Book book1) {
        SQLiteDatabase db = helper.getWritableDatabase();
        FinishBook book = (FinishBook) book1;
        final String deleteSQL = "drop table if exists "+book.getHistory();
        db.execSQL(deleteSQL);
    }
}
