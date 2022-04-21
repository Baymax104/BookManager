package com.example.bookmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.ProgressBook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/14 20:03
 * @Version
 */
public class ProgressOperator implements BookOperator {
    private Context context;
    private SQLHelper helper;
    private final String tableName = "ProgressBook";

    public ProgressOperator() {
    }

    public ProgressOperator(Context context) {
        this.context = context;
        helper = new SQLHelper(context, "BookManager.db", null, VERSION);
    }

    @Override
    public void insert(Book book1, BookOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ProgressBook book = (ProgressBook) book1;
        List<Book> data;
        // 插入前查询是否存在相同记录
        String queryExistsSQL = "select * from ProgressBook where name=? and author=?";
        db.beginTransaction();
        try (Cursor cursor = db.rawQuery(queryExistsSQL, new String[]{book.getName(), book.getAuthor()})) {

            if (cursor == null) {
                throw new BookException("数据不存在", BookException.ErrorType.INSERT_ERROR);
            }
            if (!cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("name", book.getName());
                values.put("author", book.getAuthor());
                values.put("addTime", book.getAddTime());
                values.put("progress", book.getProgress());
                values.put("page", book.getPage());
                values.put("history", book.getHistory());
                values.put("cover", book.getCover());
                values.put("coverUrl", book.getCoverUrl());
                long insertId = db.insert(tableName,null,values);
                if (insertId == -1) {
                    throw new BookException("添加失败", BookException.ErrorType.INSERT_ERROR);
                }
                createItemTable(book);
            } else {
                throw new BookException("记录已存在", BookException.ErrorType.INSERT_ERROR);
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
        SQLiteDatabase db = helper.getWritableDatabase();
        ProgressBook book = (ProgressBook) book1;
        List<Book> data;
        String queryExistsSQL = "select * from ProgressBook where name=? and author=?";
        db.beginTransaction();
        try (Cursor cursor = db.rawQuery(queryExistsSQL, new String[]{book.getName(), book.getAuthor()})) {

            if (cursor == null || cursor.getCount() == 0) {
                throw new BookException("数据不存在", BookException.ErrorType.UPDATE_ERROR);
            }
            ContentValues values = new ContentValues();
            values.put("progress", book.getProgress());
            int row = db.update(tableName, values, "name=? and author=?", new String[]{
                    book.getName(), book.getAuthor()
            });
            if (row == 0) {
                throw new BookException("更新失败", BookException.ErrorType.UPDATE_ERROR);
            }
            data = getDataAfterOperate();
            if (data == null) {
                throw new BookException("返回数据失败", BookException.ErrorType.UPDATE_ERROR);
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
    public void delete(Book book1, int position, BookOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ProgressBook book = (ProgressBook) book1;
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
        ProgressBook fromBook = (ProgressBook) fromBook1;
        ProgressBook toBook = (ProgressBook) toBook1;
        List<Book> data;
        String fromId, toId;
        // 分别更新两个Book实现交换
        String queryExistsSQL = "select id from ProgressBook where name=? and author=?";
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
            values.put("addTime", fromBook.getAddTime());
            values.put("progress", fromBook.getProgress());
            values.put("page", fromBook.getPage());
            values.put("history", fromBook.getHistory());
            values.put("cover", fromBook.getCover());
            values.put("coverUrl",fromBook.getCoverUrl());
            int row = db.update(tableName,values,"id=?",new String[]{toId});
            if (row == 0) {
                throw new BookException("更新失败", BookException.ErrorType.SORT_ERROR);
            }
            // 更新fromId的数据
            values = new ContentValues();
            values.put("name", toBook.getName());
            values.put("author", toBook.getAuthor());
            values.put("addTime", toBook.getAddTime());
            values.put("progress", toBook.getProgress());
            values.put("page", toBook.getPage());
            values.put("history", toBook.getHistory());
            values.put("cover", toBook.getCover());
            values.put("coverUrl", toBook.getCoverUrl());
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

    public List<Book> getDataAfterOperate() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String querySQL = "select * from ProgressBook";
        List<ProgressBook> data = new ArrayList<>();
        Cursor cursor = db.rawQuery(querySQL, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String author = cursor.getString(2);
                String addTime = cursor.getString(3);
                int progress = cursor.getInt(4);
                int page = cursor.getInt(5);
                String history = cursor.getString(6);
                String cover = cursor.getString(7);
                String coverUrl = cursor.getString(8);
                data.add(new ProgressBook(
                        name, author, addTime,
                        progress, page, history,cover,coverUrl
                ));
            }
            cursor.close();
            return new ArrayList<>(data);
        }
        return null;
    }

    @Override
    public void deleteItemTable(Book book1) {
        ProgressBook book = (ProgressBook) book1;
        SQLiteDatabase db = helper.getWritableDatabase();
        final String deleteSQL = "drop table if exists "+book.getHistory();
        db.execSQL(deleteSQL);
    }

    public void createItemTable(ProgressBook book) throws BookException {
        final String createSQL = "create table if not exists "+book.getHistory()+"(" +
                "id integer primary key autoincrement," +
                "updateTime text not null," +
                "startPage integer not null," +
                "endPage integer not null)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(createSQL);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String updateTime = format.format(date);
        ContentValues values = new ContentValues();
        values.put("updateTime", updateTime);
        values.put("startPage", 1);
        values.put("endPage", 1);
        long insertId = db.insert(book.getHistory(),null,values);
        if (insertId == -1) {
            throw new BookException("添加失败2", BookException.ErrorType.INSERT_ERROR);
        }
    }
}
