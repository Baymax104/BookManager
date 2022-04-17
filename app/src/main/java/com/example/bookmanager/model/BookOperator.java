package com.example.bookmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookmanager.domain.Book;

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

    public BookOperator() {
    }

    public BookOperator(Context context) {
        this.context = context;
        helper = new BookSQLHelper(context, "BookManager.db", null, 1);
    }

    @Override
    public void insert(Book book, BookOperateListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Book> data;
        // 插入前查询是否存在相同记录
        String queryExistsSQL = "select * from Book where name=? and author=?";
        db.beginTransaction();
        try (Cursor cursor = db.rawQuery(queryExistsSQL, new String[]{book.getName(),book.getAuthor()})) {

            // 若存在相同记录则更新，否则插入
            if (cursor == null) {
                throw new BookException("数据不存在", BookException.ErrorType.INSERT_ERROR);
            }
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
                if (row == 0) {
                    throw new BookException("更新失败", BookException.ErrorType.INSERT_ERROR);
                }
            } else {
                ContentValues values = new ContentValues();
                values.put("name",book.getName());
                values.put("author",book.getAuthor());
                values.put("addTime",book.getAddTime());
                values.put("progress",book.getProgress());
                values.put("page",book.getPage());
                long insertId = db.insert("Book",null,values);
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
    public void query(BookOperateListener listener) {
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
    public void update(Book book, BookOperateListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Book> data;
        String queryExistsSQL = "select * from Book where name=? and author=?";
        db.beginTransaction();
        try (Cursor cursor = db.rawQuery(queryExistsSQL, new String[]{book.getName(), book.getAuthor()})) {

            if (cursor == null || cursor.getCount() == 0) {
                throw new BookException("数据不存在", BookException.ErrorType.UPDATE_ERROR);
            }
            ContentValues values = new ContentValues();
            values.put("progress", book.getProgress());
            int row = db.update("Book", values, "name=? and author=?", new String[]{
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
    public void delete(Book book, int position, BookOperateListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Book> data;
        db.beginTransaction();
        try {
            int row = db.delete("Book", "name=? and author=?", new String[]{
                    book.getName(),
                    book.getAuthor()
            });
            if (row == 0) {
                throw new BookException("删除失败", BookException.ErrorType.DELETE_ERROR);
            }
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
    public void swap(Book fromBook, Book toBook, int fromPosition, int toPosition, BookOperateListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Book> data;
        String fromId, toId;
        // 分别更新两个Book实现交换
        String queryExistsSQL = "select id from Book where name=? and author=?";
        db.beginTransaction();
        try (Cursor fromCursor = db.rawQuery(queryExistsSQL, new String[]{fromBook.getName(), fromBook.getAuthor()});
            Cursor toCursor = db.rawQuery(queryExistsSQL, new String[]{toBook.getName(),toBook.getAuthor()})) {
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
            values.put("name",fromBook.getName());
            values.put("author",fromBook.getAuthor());
            values.put("addTime",fromBook.getAddTime());
            values.put("progress",fromBook.getProgress());
            values.put("page",fromBook.getPage());
            int row = db.update("Book",values,"id=?",new String[]{toId});
            if (row == 0) {
                throw new BookException("更新失败", BookException.ErrorType.SORT_ERROR);
            }
            // 更新fromId的数据
            values = new ContentValues();
            values.put("name",toBook.getName());
            values.put("author",toBook.getAuthor());
            values.put("addTime",toBook.getAddTime());
            values.put("progress",toBook.getProgress());
            values.put("page",toBook.getPage());
            row = db.update("Book",values, "id=?",new String[]{fromId});
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
