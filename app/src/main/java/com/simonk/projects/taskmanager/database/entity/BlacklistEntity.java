package com.simonk.projects.taskmanager.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "blacklist")
public class BlacklistEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String apppackage;
    public long lastOpenDate;
}
