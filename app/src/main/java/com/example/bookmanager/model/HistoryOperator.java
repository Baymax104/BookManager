package com.example.bookmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.History;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/21 13:50
 * @Version
 */
public class HistoryOperator implements Operator {
    private Context context;
    private SQLHelper helper;
    private Book book;
    private String tableName;

    public HistoryOperator(Context context, Book book) {
        this.context = context;
        this.book = book;
        this.tableName = book.getHistory();
        helper = new SQLHelper(context,"BookManager.db",null,VERSION);
    }

    public void insert(History history, HistoryOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<History> data;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("updateTime", history.getUpdateTime());
            values.put("startPage", history.getStartPage());
            values.put("endPage", history.getEndPage());
            long insertId = db.insert(tableName, null, values);
            if (insertId == -1) {
                throw new BookException("添加失败", BookException.ErrorType.INSERT_ERROR);
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

    public void query(HistoryOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<History> data;
        db.beginTransaction();
        try {
            data = getDataAfterOperate();
            if (data == null) {
                throw new BookException("查询失败", BookException.ErrorType.QUERY_ERROR);
            }
            listener.onSuccess(data);
            db.setTransactionSuccessful();
        } catch (BookException e) {
            listener.onError(e);
        } finally {
            db.endTransaction();
        }
    }

    public void update(History history, HistoryOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String querySQL = "select * from "+tableName+" where startPage=? and endPage=?";
        List<History> data;
        db.beginTransaction();
        try (Cursor cursor = db.rawQuery(querySQL, new String[]{String.valueOf(history.getStartPage()),String.valueOf(history.getEndPage())})) {
            if (cursor == null || cursor.getCount() == 0) {
                throw new BookException("数据不存在", BookException.ErrorType.UPDATE_ERROR);
            }
            ContentValues values = new ContentValues();
            values.put("updateTime", history.getUpdateTime());
            int row = db.update(
                    tableName, values, "startPage=? and endPage=?",
                    new String[]{
                            String.valueOf(history.getStartPage()),
                            String.valueOf(history.getEndPage())
                    }
            );
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

    public void delete(History history, int position, HistoryOperatorListener listener) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<History> data;
        db.beginTransaction();
        try {
            int row = db.delete(
                    tableName, "updateTime=? and startPage=? and endPage=?",
                    new String[]{
                            history.getUpdateTime(),
                            String.valueOf(history.getStartPage()),
                            String.valueOf(history.getEndPage())
                    }
            );
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

    public List<History> getDataAfterOperate() {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<History> data = new ArrayList<>();
        String querySQL = "select * from "+tableName;
        Cursor cursor = db.rawQuery(querySQL, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String updateTime = cursor.getString(1);
                int startPage = cursor.getInt(2);
                int endPage = cursor.getInt(3);
                data.add(new History(
                        book.getPage(),updateTime,
                        startPage,endPage
                ));
            }
            cursor.close();
            return data;
        }
        return null;
    }
}
