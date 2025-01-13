package com.example.happyplaces.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlacesDao {

    @Insert
    suspend fun insert(happyPlaceEntity: HappyPlaceEntity)

    @Update
    suspend fun update(happyPlaceEntity: HappyPlaceEntity)

    @Delete
    suspend fun delete(happyPlaceEntity: HappyPlaceEntity)

    @Query("SELECT * FROM `happy places`")
    fun fetchAllHappyPlaces():Flow<List<HappyPlaceEntity>>

    @Query("SELECT * FROM `happy places` WHERE id=:id")
    fun fetchHappyPlacebyID(id:Int):Flow<HappyPlaceEntity>
}