package com.example.bookmanager.domain;

/**
 * @Description Book类的子类，网络请求后返回的实体类，用于图书信息显示
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/16 15:17
 * @Version
 */
public class ProgressRequestBook extends ProgressBook {
    private String isbn;
    private String photoUrl;
    private String publishing;
    private String description;

    public ProgressRequestBook() {
    }

    public ProgressRequestBook(String name, String author, String addTime, int page, String isbn, String photoUrl, String publishing, String description) {
        super(name, author, addTime, 0, page);
        this.isbn = isbn;
        this.photoUrl = photoUrl;
        this.publishing = publishing;
        this.description = description;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPublishing() {
        return publishing;
    }

    public void setPublishing(String publishing) {
        this.publishing = publishing;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
