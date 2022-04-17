package com.example.bookmanager.controller.callbacks;

import com.example.bookmanager.domain.RequestBook;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/16 19:14
 * @Version
 */
public interface IRequestCallback {
    void getRequestBook(RequestBook requestBook);
    void getRequestError(Exception e);
}
