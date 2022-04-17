package com.example.bookmanager.controller.callbacks;

import com.example.bookmanager.domain.RequestBook;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 17:21
 * @Version
 */
public interface InfoChangeCallback {
    void refreshInfo(RequestBook book);
}
