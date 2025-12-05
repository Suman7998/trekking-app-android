package com.example.trekking.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places WHERE category = :category ORDER BY name")
    suspend fun getByCategory(category: String): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PlaceEntity>)

    @Query("SELECT COUNT(*) FROM places")
    suspend fun count(): Int

    @Query("DELETE FROM places WHERE category = :category")
    suspend fun deleteByCategory(category: String)

    @Delete
    suspend fun delete(place: PlaceEntity)

    @Query("SELECT * FROM places")
    suspend fun getAll(): List<PlaceEntity>

    @Query("DELETE FROM places")
    suspend fun clearAll()

    @Transaction
    suspend fun clearAndInsert(places: List<PlaceEntity>) {
        clearAll()
        insertAll(places)
    }
}