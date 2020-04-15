package com.iit.finalproject;


public class CommentListViewItem {
    private int id;
    private String content;
    private String author;
    private int user_id;
    private String time;

    public CommentListViewItem(int id, String content, String author, int user_id, String time) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.user_id = user_id;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
