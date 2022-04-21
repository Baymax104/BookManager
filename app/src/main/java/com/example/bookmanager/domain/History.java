package com.example.bookmanager.domain;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/21 13:48
 * @Version
 */
public class History {
    private int page;
    private String updateTime;
    private int startPage;
    private int endPage;

    public History() {
    }

    public History(int page, String updateTime, int startPage, int endPage) {
        this.page = page;
        this.updateTime = updateTime;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public History(String updateTime, int startPage, int endPage) {
        this.updateTime = updateTime;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
