package com.simonk.projects.taskmanager.database.providers;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.simonk.projects.taskmanager.database.entity.TerminalSnapshotEntity;

import java.util.List;

@Dao
public interface TerminalSnapshotProvider {

    @Query("SELECT * FROM terminal_snapshot WHERE id=:id")
    LiveData<TerminalSnapshotEntity> loadSnapshot(String id);

    @Query("SELECT * FROM terminal_snapshot WHERE id=:id")
    TerminalSnapshotEntity getSnapshot(String id);

    @Query("SELECT * FROM terminal_snapshot")
    LiveData<List<TerminalSnapshotEntity>> loadSnapshots();

    @Query("SELECT * FROM terminal_snapshot")
    List<TerminalSnapshotEntity> getSnapshots();

    @Insert
    void insertSnapshot(TerminalSnapshotEntity entity);

    @Update
    void updateSnapshot(TerminalSnapshotEntity entity);

    @Delete
    void deleteSnapshot(TerminalSnapshotEntity entity);

    @Query("DELETE FROM terminal_snapshot")
    void clearSnapshots();
}
