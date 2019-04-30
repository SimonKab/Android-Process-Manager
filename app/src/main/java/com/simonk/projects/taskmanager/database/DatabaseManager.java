package com.simonk.projects.taskmanager.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.simonk.projects.taskmanager.database.entity.BlacklistEntity;
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

    public static LiveData<List<BlacklistEntity>> loadBlacklistEntities(Context context) {
        return LocalDatabase.getInstance(context)
                .blacklistProvider()
                .loadBlacklistEntities();
    }

    public static void insertBlacklistEntity(Context context, BlacklistEntity entity) {
        Thread thread = new Thread(() -> {
            LocalDatabase.getInstance(context)
                    .blacklistProvider()
                    .insertBlacklistEntity(entity);
        });
        thread.setName("TaskManager-Database");
        thread.start();
    }

    public static void deleteBlacklistEntityByPackage(Context context, String ppackage) {
        Thread thread = new Thread(() -> {
            LocalDatabase.getInstance(context)
                    .blacklistProvider()
                    .deleteBlacklistEntityByPackage(ppackage);
        });
        thread.setName("TaskManager-Database");
        thread.start();
    }

    public static void updateBlacklistEntityOpenDate(Context context, String ppackage, long openDate) {
        Thread thread = new Thread(() -> {
            LocalDatabase.getInstance(context)
                    .blacklistProvider()
                    .updateBlacklistEntityOpenDate(ppackage, openDate);
        });
        thread.setName("TaskManager-Database");
        thread.start();
    }
}
