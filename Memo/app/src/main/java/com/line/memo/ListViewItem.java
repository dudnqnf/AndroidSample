package com.line.memo;

import android.graphics.Bitmap;

public class ListViewItem {
    private int id;
    private String title;
    private String content;
    private String thumbnail;
    private Bitmap online_thumbnail;

    public ListViewItem(int id, String title, String content, String thumbnail, Bitmap online_thumbnail) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.online_thumbnail = online_thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getOnline_thumbnail() {
        return online_thumbnail;
    }

    public void setOnline_thumbnail(Bitmap online_thumbnail) {
        this.online_thumbnail = online_thumbnail;
    }
}
