package com.example.bookmanager.controller.callbacks;

import com.example.bookmanager.domain.ProgressBook;

/**
 * @Description 对话框监听回调接口
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/16 16:11
 * @Version
 */
public interface IDialogCallback {
    void scanBarcode();
    void insertBook(ProgressBook progressBook);
    void updateBook(ProgressBook progressBook);

}
