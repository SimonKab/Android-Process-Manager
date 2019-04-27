package com.simonk.projects.taskmanager.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.simonk.projects.taskmanager.database.entity.TerminalSnapshotEntity;

import java.util.List;

public class DatabaseManager {

    public static LiveData<List<TerminalSnapshotEntity>> loadSnapshots(Context context) {
        return LocalDatabase.getInstance(context)
                        .terminalSnapshotProvider()
                        .loadSnapshots();
    }

    public static void insertSnapshot(Context context, TerminalSnapshotEntity snapshot) {
        Thread thread = new Thread(() -> {
            LocalDatabase.getInstance(context)
                    .terminalSnapshotProvider()
                    .insertSnapshot(snapshot);
        });
        thread.setName("TaskManager-Database");
        thread.start();
    }

    public static void deleteAllSnapshots(Context context) {
        Thread thread = new Thread(() -> {
            LocalDatabase.getInstance(context)
                    .terminalSnapshotProvider()
                    .clearSnapshots();
        });
        thread.setName("TaskManager-Database");
        thread.start();
    }
}
