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

    protected Book() {
    }

    protected Book(String name, String author, int page) {
        this.name = name;
        this.author = author;
        this.page = page;
    }

    protected Book(String name, String author, int page, String history) {
        this.name = name;
        this.author = author;
        this.page = page;
        this.history = history;
    }

    protected Book(String name, String author, int page, String history, String cover) {
        this.name = name;
        this.author = author;
        this.page = page;
        this.history = history;
        this.cover = cover;
    }

    protected Book(String name, String author, int page, String history, String cover, String coverUrl) {
        this.name = name;
        this.author = author;
        this.page = page;
        this.history = history;
        this.cover = cover;
        this.coverUrl = coverUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
