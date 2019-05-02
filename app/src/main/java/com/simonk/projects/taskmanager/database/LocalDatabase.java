package com.simonk.projects.taskmanager.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.simonk.projects.taskmanager.database.entity.BlacklistEntity;
import com.simonk.projects.taskmanager.database.entity.TerminalSnapshotEntity;
import com.simonk.projects.taskmanager.database.providers.BlacklistProvider;
import com.simonk.projects.taskmanager.database.providers.TerminalSnapshotProvider;

@androidx.room.Database(entities = {TerminalSnapshotEntity.class, BlacklistEntity.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    private static LocalDatabase sInstance;

    private static final String DATABASE_NAME = "TaskManagerDatabase";

    /**
     * Provider for working with snapshots of terminal.
     * See {@link TerminalSnapshotEntity} for more details
     * @return
     */
    public abstract TerminalSnapshotProvider terminalSnapshotProvider();
    /**
     * Provider for working with blacklist entries.
     * See {@link BlacklistEntity} for more details
     * @return
     */
    public abstract BlacklistProvider blacklistProvider();

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
