package com.example.happyplaces.database

import android.app.Application

class HappyPlacesApp:Application() {
    val db by lazy {
        HappyPlacesDatabase.getInstance(this)
    }
}