package com.example.bookmanager.model;

import com.example.bookmanager.domain.Book;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 18:10
 * @Version
 */
public interface BookOperator extends Operator {
    void insert(Book book, BookOperatorListener listener);
    void query(BookOperatorListener listener);
    void update(Book book, BookOperatorListener listener);
    void delete(Book book, int position, boolean isEdit, BookOperatorListener listener);
    void swap(Book fromBook, Book toBook, int fromPosition, int toPosition, BookOperatorListener listener);
    List<Book> getDataAfterOperate();
    void deleteItemTable(Book book);
}
