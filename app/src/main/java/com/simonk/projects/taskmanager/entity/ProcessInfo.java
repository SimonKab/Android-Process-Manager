package com.simonk.projects.taskmanager.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Objects;

public class ProcessInfo implements Serializable {
    private String text;
    private Drawable image;
    private String ppackage;
    private int priority;
    private boolean status;
    private int minSdk;
    private int uid;
    private String description;
    private int pid;

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getMinSdk() {
        return minSdk;
    }

    public void setMinSdk(int minSdk) {
        this.minSdk = minSdk;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessInfo that = (ProcessInfo) o;
        return getPriority() == that.getPriority() &&
                isStatus() == that.isStatus() &&
                getMinSdk() == that.getMinSdk() &&
                getUid() == that.getUid() &&
                getPid() == that.getPid() &&
                Objects.equals(getText(), that.getText()) &&
                Objects.equals(getImage(), that.getImage()) &&
                Objects.equals(getPpackage(), that.getPpackage()) &&
                Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getImage(), getPpackage(), getPriority(), isStatus(),
                getMinSdk(), getUid(), getDescription(), getPid());
    }
}