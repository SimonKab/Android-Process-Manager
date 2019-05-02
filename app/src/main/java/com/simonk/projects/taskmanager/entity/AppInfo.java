package com.simonk.projects.taskmanager.entity;

import android.graphics.drawable.Drawable;

import java.util.Objects;

/**
 * POJO represents usefull for this app information about other apps.
 */
public class AppInfo {

    private String text;
    private Drawable image;
    private String ppackage;
    private boolean isEnabled;
    private boolean isSystem;
    private boolean isInBlacklist;
    private long lastOpenDate;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getPpackage() {
        return ppackage;
    }

    public void setPpackage(String ppackage) {
        this.ppackage = ppackage;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public boolean isInBlacklist() {
        return isInBlacklist;
    }

    public void setInBlacklist(boolean inBlacklist) {
        isInBlacklist = inBlacklist;
    }

    public long getLastOpenDate() {
        return lastOpenDate;
    }

    public void setLastOpenDate(long lastOpenDate) {
        this.lastOpenDate = lastOpenDate;
    }
}
