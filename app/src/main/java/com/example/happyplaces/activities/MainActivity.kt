package com.example.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.adapters.MainPageAdapter
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.database.HappyPlacesApp
import com.example.happyplaces.database.HappyPlacesDao
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.utils.SwipeToDeleteCallback
import com.example.happyplaces.utils.SwipeToEditCallback
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    private var binding:ActivityMainBinding?= null
    private lateinit var happyPlacesdao: HappyPlacesDao

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
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
        lifecycleScope.launch {
            happyPlacesdao.fetchAllHappyPlaces().collect{ list ->
                val list=ArrayList(list)

                setupDataintoRV(list)
            }
        }
        binding?.fabBtn?.setOnClickListener {
            val intent= Intent(this, AddHappyPlacesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun actionBar()
    {
        val statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        binding?.actionBarMain?.setPadding(0, statusBarHeight, 0, 0)
        setSupportActionBar(binding?.actionBarMain)
    }

    private fun setupDataintoRV(happyPlacesList: ArrayList<HappyPlaceEntity>)
    {
        if(happyPlacesList.isNotEmpty())
        {
            binding?.RVMain?.visibility=View.VISIBLE // chatting with ssr fixed this
            binding?.defaultTv?.visibility= View.INVISIBLE
            Log.e("List State","${happyPlacesList.isNotEmpty()}")
            val mainPageAdapter=MainPageAdapter(this@MainActivity,happyPlacesList,
                {
                        selectedID->
                    cardClickListener(selectedID)
                })
            binding?.RVMain?.layoutManager=LinearLayoutManager(this)
            binding?.RVMain?.adapter=mainPageAdapter
        }
        else
        {
            binding?.RVMain?.visibility=View.GONE
            binding?.defaultTv?.visibility=View.VISIBLE
        }

        val editSwipeHandler=object :SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=binding?.RVMain?.adapter as MainPageAdapter
                adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.RVMain)

        val deleteSwipeHandler= object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=binding?.RVMain?.adapter as MainPageAdapter
                lifecycleScope.launch {
                    adapter.deleteItem(happyPlacesdao, viewHolder.adapterPosition)
                }
            }
        }
        val deleteItemTouchHelper=ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding?.RVMain)
    }

    private fun cardClickListener(id:Int)
    {
        val intent=Intent(this,HappyPlaceDetailsActivity::class.java)
        intent.putExtra("ID",id)
        startActivity(intent)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding=null
    }

    companion object
    {
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
}