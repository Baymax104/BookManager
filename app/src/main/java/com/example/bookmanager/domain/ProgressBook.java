package com.example.bookmanager.domain;


import android.graphics.Bitmap;
import android.net.Uri;

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
        super(name,author,page);
        this.addTime = addTime;
        this.progress = progress;
    }
    public ProgressBook(String name, String author, String addTime, int progress, int page, String cover) {
        super(name,author,page);
        this.addTime = addTime;
        this.progress = progress;
        this.cover = cover;
    }
    public ProgressBook(String name, String author, String addTime, int progress, int page, String history, String cover, String coverUrl) {
        super(name,author,page,history,cover,coverUrl);
        this.addTime = addTime;
        this.progress = progress;
    }

    public ProgressBook(String name, String author, String addTime, int progress, int page, String history, String cover) {
        super(name,author,page,history,cover);
        this.addTime = addTime;
        this.progress = progress;
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
