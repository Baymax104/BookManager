package com.example.bookmanager.domain;

import java.io.Serializable;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/18 14:39
 * @Version
 */
public abstract class Book implements Serializable {
    protected String name;
    protected String author;
    protected int page;
}
