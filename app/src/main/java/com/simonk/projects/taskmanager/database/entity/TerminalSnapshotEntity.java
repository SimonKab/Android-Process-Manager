package com.simonk.projects.taskmanager.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class for database. Represents information about terminal snapshot, which will be saved in database
 * Terminal snapshot is piece of data which contains request and response as strings only.
 * Helpful for creating history of terminal
 */
@Entity(tableName = "terminal_snapshot")
public class TerminalSnapshotEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /**
     * Request to terminal
     */
    public String request;

    /**
     * Response of terminal
     */
    public String response;

    public TerminalSnapshotEntity() {

    }

    public TerminalSnapshotEntity(String request, String response) {
        this.request = request;
        this.response = response;
    }
}
