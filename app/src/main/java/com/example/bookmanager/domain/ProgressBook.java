package com.example.bookmanager.domain;


/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 16:25
 * @Version
 */
public class ProgressBook extends Book {
    private String addTime;
    private int progress;

    public ProgressBook() {
    }

    public ProgressBook(String name, String author, String addTime, int progress, int page) {
        this.name = name;
        this.addTime = addTime;
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

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
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
