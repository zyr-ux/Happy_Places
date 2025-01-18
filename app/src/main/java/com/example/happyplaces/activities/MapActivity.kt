package com.example.happyplaces.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity()
{
    private var binding:ActivityMapBinding?=null
    private var entityID:Int?=null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.root?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
        }
        actionBar()

        if (intent.hasExtra("Entity ID"))
            entityID=intent.getIntExtra("Entity ID",1)
        Log.e("Entity ID","$entityID")
    }

    private fun actionBar()
    {
        val statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        binding?.actionBarMap?.setPadding(0, statusBarHeight, 0, 0)
        setSupportActionBar(binding?.actionBarMap)
        if(supportActionBar!=null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.actionBarMap?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

}