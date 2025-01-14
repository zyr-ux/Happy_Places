package com.example.happyplaces.activities

import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.database.HappyPlacesApp
import com.example.happyplaces.database.HappyPlacesDao
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailsBinding
import kotlinx.coroutines.launch

class HappyPlaceDetailsActivity : AppCompatActivity()
{
    private var binding:ActivityHappyPlaceDetailsBinding?=null
    private var id:Int?=null
    private lateinit var happyPlacesdao: HappyPlacesDao
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHappyPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.root?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
        }
        actionBar()
        happyPlacesdao = (application as HappyPlacesApp).db.happyPlacesDao()
        id=intent.getIntExtra("ID",1)
        Log.e("onClick ID","$id")
        lifecycleScope.launch {
            happyPlacesdao.fetchHappyPlacebyID(id!!).collect{
                happyPlacesDetails(it)
            }
        }

    }

    private fun actionBar()
    {
        val statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        binding?.actionBarPlaceDetails?.setPadding(0, statusBarHeight, 0, 0)
        setSupportActionBar(binding?.actionBarPlaceDetails)
        if(supportActionBar!=null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.actionBarPlaceDetails?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding=null
    }

    private fun happyPlacesDetails(happyPlaceEntity:HappyPlaceEntity)
    {
        binding?.actionBarPlaceDetails?.setTitle(happyPlaceEntity.title)
        val imgfile=happyPlaceEntity.img
        Glide.with(this@HappyPlaceDetailsActivity)
            .load(imgfile)
            .into(binding!!.happyPlaceIMG)
        //binding?.happyPlaceIMG?.setImageURI(Uri.parse(happyPlaceEntity.img))
        binding?.happyPlaceDesc?.setText(happyPlaceEntity.description)
        binding?.happyPlaceLocation?.setText(happyPlaceEntity.location)
    }

}