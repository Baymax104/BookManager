package com.example.bookmanager.domain;

import java.io.Serializable;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 16:25
 * @Version
 */
public class Book implements Serializable {
    private String name;
    private String time;
    private String author;
    private int progress;
    private int page;

    public Book() {}

    public Book(String name, String time, String author, int progress, int page) {
        this.name = name;
        this.time = time;
        this.author = author;
        this.progress = progress;
        this.page = page;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
