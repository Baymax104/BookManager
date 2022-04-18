package com.example.bookmanager.model;

import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.ProgressBook;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 18:10
 * @Version
 */
public interface BookOperator {
    void insert(Book book, OperatorListener listener);
    void query(OperatorListener listener);
    void update(Book book, OperatorListener listener);
    void delete(Book book, int position, OperatorListener listener);
    void swap(Book fromBook, Book toBook, int fromPosition, int toPosition, OperatorListener listener);
    List<Book> getDataAfterOperate();
}
