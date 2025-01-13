package com.example.happyplaces.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Happy Places")
data class HappyPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val title:String,
    val img:String,
    val description:String,
    val date:String,
    val location:String,
    val latitude:Double,
    val longitude:Double
 ) :Serializable