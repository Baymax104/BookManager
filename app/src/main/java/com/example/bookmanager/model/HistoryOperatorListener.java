package com.example.bookmanager.model;

import com.example.bookmanager.domain.History;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/21 17:51
 * @Version
 */
public interface HistoryOperatorListener {
    void onSuccess(List<History> data, int... position);
    void onError(BookException e);
}
