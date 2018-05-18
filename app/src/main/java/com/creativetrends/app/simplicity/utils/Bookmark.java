package com.creativetrends.app.simplicity.adapters;

/**
 * Created by Creative Trends Apps.
 */

public class Bookmark {
    private String title;
    private String url;
    private String letter;
    private int color;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(int color) {
        this.color = color;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }


    public String getTitle() {
        return title;
    }

    public int getImage() {
        return color;
    }


    public String getUrl() {
        return url;
    }

    public String getLetter() {
        return letter;
    }
}
