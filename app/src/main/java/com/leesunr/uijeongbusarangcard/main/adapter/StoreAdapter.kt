package com.leesunr.uijeongbusarangcard.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.uijeongbusarangcard.R
import com.leesunr.uijeongbusarangcard.data.entity.Store
import kotlinx.android.synthetic.main.list_item_store.view.*

class StoreAdapter() :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    val items = ArrayList<Store>()
    private var ItemClickListener:((Store)->Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_store, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(position: Int) {
            itemView.text_name.text = items[position].name
            itemView.text_category.text = items[position].category
            itemView.text_address.text = items[position].address
            itemView.text_distance.text = "10km"
            itemView.setOnClickListener { ItemClickListener?.invoke(items[position]) }
        }
    }

    override fun getItemCount() = items.size

    fun setItemClickListener(listener:((Store)->Unit)?){
        ItemClickListener = listener
    }
}