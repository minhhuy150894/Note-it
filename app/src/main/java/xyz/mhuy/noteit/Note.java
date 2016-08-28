package xyz.mhuy.noteit;


import java.io.Serializable;

public class Note implements Serializable{
    private long id;
    private long date;
    private String title;
    private String content;

    public Note(long id, long date, String title, String content) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
    }


    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}