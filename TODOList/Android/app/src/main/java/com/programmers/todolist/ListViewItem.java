package com.programmers.todolist;


public class ListViewItem {
    private int id;
    private String title;
    private int priority;
    private int status;
    private String deadline;

    public ListViewItem(int id, String title, int priority, int status, String deadline) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
