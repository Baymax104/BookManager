package com.example.bookmanager.model;

import com.example.bookmanager.domain.Book;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 18:12
 * @Version
 */
public interface BookOperateListener {
    void onSuccess(List<Book> data, int... position);
    void onError(BookException e);
}
