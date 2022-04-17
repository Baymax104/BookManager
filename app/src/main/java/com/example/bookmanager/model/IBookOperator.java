package com.example.bookmanager.model;

import com.example.bookmanager.domain.Book;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 18:10
 * @Version
 */
public interface IBookOperator {
    void insert(Book book, BookOperateListener listener);
    void query(BookOperateListener listener);
    void update(Book book, BookOperateListener listener);
    void delete(Book book, int position, BookOperateListener listener);
    void swap(Book fromBook, Book toBook, int fromPosition, int toPosition, BookOperateListener listener);
}
