package com.simonk.projects.taskmanager.database.providers;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.simonk.projects.taskmanager.database.entity.BlacklistEntity;
import com.simonk.projects.taskmanager.database.entity.TerminalSnapshotEntity;

import java.util.List;

@Dao
public interface BlacklistProvider {

    @Query("SELECT * FROM blacklist WHERE id=:id")
    LiveData<BlacklistEntity> loadBlacklistEntity(String id);

    @Query("SELECT * FROM blacklist WHERE id=:id")
    BlacklistEntity getBlacklistEntity(String id);

    @Query("SELECT * FROM blacklist")
    LiveData<List<BlacklistEntity>> loadBlacklistEntities();

    @Query("SELECT * FROM blacklist")
    List<BlacklistEntity> getBlacklistEntities();

    @Insert
    void insertBlacklistEntity(BlacklistEntity entity);

    @Update
    void updateBlacklistEntity(BlacklistEntity entity);

    @Delete
    void deleteBlacklistEntity(BlacklistEntity entity);

    @Query("DELETE FROM blacklist WHERE apppackage=:ppackage")
    void deleteBlacklistEntityByPackage(String ppackage);

    @Query("DELETE FROM blacklist")
    void clearBlacklistEntities();

}
