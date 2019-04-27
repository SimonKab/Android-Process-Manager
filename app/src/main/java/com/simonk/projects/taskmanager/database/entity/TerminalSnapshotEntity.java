package com.simonk.projects.taskmanager.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "terminal_snapshot")
public class TerminalSnapshotEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String request;
    public String response;

    public TerminalSnapshotEntity() {

    }

    public TerminalSnapshotEntity(String request, String response) {
        this.request = request;
        this.response = response;
    }
}
