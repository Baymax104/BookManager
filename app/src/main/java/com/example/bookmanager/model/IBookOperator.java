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
    void insert(Book book, BookOperatorListener listener);
    void query(BookOperatorListener listener);
    void update();
    void delete(Book book);
    void swap();
}
