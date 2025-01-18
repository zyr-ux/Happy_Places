package com.example.happyplaces.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.database.HappyPlacesApp
import com.example.happyplaces.database.HappyPlacesDao
import com.example.happyplaces.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity(), OnMapReadyCallback
{
    private var binding:ActivityMapBinding?=null
    private var entityID:Int?=null
    private var happyPlacesDao:HappyPlacesDao?=null
    var entity:HappyPlaceEntity?=null
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
        happyPlacesDao = (application as HappyPlacesApp).db.happyPlacesDao()
        actionBar()
        if (intent.hasExtra("Entity ID"))
            entityID=intent.getIntExtra("Entity ID",1)
        Log.e("Entity ID","$entityID")

        lifecycleScope.launch {
            happyPlacesDao?.fetchHappyPlacebyID(entityID!!)?.collect{
                setupMapsView(it)
                entity=it
            }
        }
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

    private fun setupMapsView(happyPlaceEntity: HappyPlaceEntity)
    {
        binding?.actionBarMap?.setTitle(happyPlaceEntity.title)
        val supportMapFragment:SupportMapFragment=supportFragmentManager.findFragmentById(R.id.happyPlace_mapview) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(maps: GoogleMap) {
        val position=LatLng(entity!!.latitude,entity!!.longitude)
        maps.addMarker(MarkerOptions().position(position))
        val latlngZoom= CameraUpdateFactory.newLatLngZoom(position,13f)
        maps.animateCamera(latlngZoom)
    }

}