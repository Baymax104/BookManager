package com.example.bookmanager.domain;

import android.graphics.Bitmap;
import android.net.Uri;

import kotlin.jvm.internal.ShortSpreadBuilder;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/18 14:43
 * @Version
 */
public class FinishBook extends Book {
    private String addTime;
    private String endTime;

    public FinishBook() {
    }

    public FinishBook(String name, String author, int page, String addTime, String endTime, String history, String cover,String coverUrl) {
        super(name,author,page,history,cover,coverUrl);
        this.addTime = addTime;
        this.endTime = endTime;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
