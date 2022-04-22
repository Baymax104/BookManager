package com.example.bookmanager.controller.callbacks;

import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.History;

/**
 * @Description 对话框监听回调接口
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/16 16:11
 * @Version
 */
public interface IDialogCallback {
    default void insertBook(Book book) {
    }
    default void updateBook(Book book) {
    }
    default void deleteBook(Book book, int position, boolean... isRestart) {
    }
    default void deleteHistory(int position) {
    }
    default void insertHistory(History history) {
    }
    default void startSelectTime(int position) {
    }
    default void updateHistory(String updateTime, int position) {
    }
}
