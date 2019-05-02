package com.simonk.projects.taskmanager.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class for database. Represents information about blocked app, which will be saved in database
 */
@Entity(tableName = "blacklist")
public class BlacklistEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /**
     * Package full name of blocked app
     */
    public String apppackage;

    /**
     * Last time when app was opened in milliseconds
     */
    public long lastOpenDate;
}
