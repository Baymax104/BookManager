package com.example.bookmanager.model;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/15 15:57
 * @Version
 */
public class BookException extends Exception {
    public enum ErrorType {
        INSERT_ERROR,
        UPDATE_ERROR,
        QUERY_ERROR,
        DELETE_ERROR,
        SORT_ERROR
    }
    public ErrorType errorType;

    public BookException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
}
