package com.example.bookmanager.controller.callbacks;

import android.widget.ImageView;

import com.example.bookmanager.domain.Book;

/**
 * @Description 对话框监听回调接口
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/16 16:11
 * @Version
 */
public interface IDialogCallback {
    default void scanBarcode() {
    }
    default void insertBook(Book book) {
    }
    default void updateBook(Book book) {
    }
    default void deleteBook(Book book, int position, boolean... isRestart) {
    }
    default void startCamera(ImageView takePhoto, ImageView takeCover, IUriCallback callback) {
    }
}
