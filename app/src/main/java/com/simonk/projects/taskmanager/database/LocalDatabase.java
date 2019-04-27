package com.simonk.projects.taskmanager.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.simonk.projects.taskmanager.database.entity.TerminalSnapshotEntity;
import com.simonk.projects.taskmanager.database.providers.TerminalSnapshotProvider;

@androidx.room.Database(entities = {TerminalSnapshotEntity.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    private static LocalDatabase sInstance;

    private static final String DATABASE_NAME = "TaskManagerDatabase";

    public abstract TerminalSnapshotProvider terminalSnapshotProvider();

    public static LocalDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (LocalDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }

}
