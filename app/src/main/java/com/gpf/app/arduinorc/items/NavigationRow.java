package com.gpf.app.arduinorc.items;

public class NavigationRow {

    int iconId;
    String title;

    public NavigationRow(int iconId, String title) {
        this.iconId = iconId;
        this.title = title;
    }


    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
