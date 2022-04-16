package com.example.bookmanager.view;

import com.example.bookmanager.domain.Book;

/**
 * @Description 对话框监听回调接口
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/16 16:11
 * @Version
 */
public interface DialogCallback {
    void scanBarcode();
    void insertBook(Book book);
    void updateBook(Book book);

}
