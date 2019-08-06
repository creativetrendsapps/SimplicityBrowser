package com.creativetrends.app.simplicity.popup;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 5/11/2018.
 */
public class Item {
    private String title;
    private int imageRes;

    public Item(String title, int imageRes) {
        this.title = title;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    int getImageRes() {
        return imageRes;
    }

    @SuppressWarnings (value="unused")
    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}