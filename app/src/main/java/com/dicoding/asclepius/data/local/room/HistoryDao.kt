package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY id ASC")
    fun getAllHistory(): LiveData<List<HistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE id = :id")
    fun getOneHistory(id: Int): LiveData<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHistory(user: HistoryEntity)

    @Query("DELETE FROM scan_history")
    fun deleteAll()
}