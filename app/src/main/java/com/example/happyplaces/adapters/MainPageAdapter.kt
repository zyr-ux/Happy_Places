package com.example.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.happyplaces.activities.AddHappyPlacesActivity
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.database.HappyPlacesDao
import com.example.happyplaces.databinding.RvItemViewBinding
import java.io.File

class MainPageAdapter(private val context:Context,
                           private val items:ArrayList<HappyPlaceEntity>,
                           private val cardClickListener:(id:Int)->Unit)
    :RecyclerView.Adapter<MainPageAdapter.ViewHolder>() {
    class ViewHolder(binding:RvItemViewBinding):RecyclerView.ViewHolder(binding.root)
    {
        val ivPlaces=binding.ivPlacesImg
        val title=binding.tvTitle
        val desc=binding.tvDesc
        val happyPlaceCard=binding.happyPlaceCard

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(RvItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int
    {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item=items[position]
        holder.title.text=item.title
        holder.desc.text=item.description
        val file=File(item.img)
        Glide.with(holder.itemView.context)
            .load(file)
            .thumbnail(0.4f)
            .apply(RequestOptions().override(600, 600)) // Resize the image to reduce memory usage
            .into(holder.ivPlaces)
        holder.happyPlaceCard.setOnClickListener{
            cardClickListener.invoke(item.id)
        }
    }

    fun notifyEditItem(activity:Activity,position: Int,requextCode:Int)
    {
        val intent=Intent(context,AddHappyPlacesActivity::class.java)
        intent.putExtra("ID",position)
        Log.e("Position","$position")
        activity.startActivityForResult(intent,requextCode)
        notifyItemChanged(position)
    }

    suspend fun deleteItem(happyPlacesDao: HappyPlacesDao, position: Int)
    {
        happyPlacesDao.delete(items[position])
    }
}