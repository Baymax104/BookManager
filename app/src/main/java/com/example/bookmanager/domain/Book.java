package com.example.bookmanager.domain;

import android.graphics.Bitmap;
import android.net.Uri;

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
    protected String history;
    protected String cover;
    protected String coverUrl;

    public Book() {
    }

    public Book(String name, String author, int page) {
        this.name = name;
        this.author = author;
        this.page = page;
    }

    public Book(String name, String author, int page, String history) {
        this.name = name;
        this.author = author;
        this.page = page;
        this.history = history;
    }

    public Book(String name, String author, int page, String history, String cover) {
        this.name = name;
        this.author = author;
        this.page = page;
        this.history = history;
        this.cover = cover;
    }

    public Book(String name, String author, int page, String history, String cover, String coverUrl) {
        this.name = name;
        this.author = author;
        this.page = page;
        this.history = history;
        this.cover = cover;
        this.coverUrl = coverUrl;
    }
}
