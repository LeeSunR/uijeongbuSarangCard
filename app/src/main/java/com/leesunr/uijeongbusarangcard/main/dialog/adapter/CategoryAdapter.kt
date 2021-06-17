package com.leesunr.uijeongbusarangcard.main.dialog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.uijeongbusarangcard.R
import kotlinx.android.synthetic.main.list_item_category.view.*

class CategoryAdapter(private val items: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var ItemClickListener:((Int)->Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_category, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(position: Int) {
            itemView.text_content.apply {
                text = items[position].name
                setOnClickListener {
                    allClear()
                    items[position].isSelected = !(items[position].isSelected)
                    notifyDataSetChanged()
                    ItemClickListener?.invoke(position)
                }
            }
            itemView.text_content.isSelected = items[position].isSelected
        }

        fun allClear(){
            items.forEach { it.isSelected = false }
        }
    }

    override fun getItemCount() = items.size

    fun itemClickListener(listener:((Int)->Unit)?){
        ItemClickListener = listener
    }

    fun getSelectedItem():String?{
        items.forEach {
            if(it.isSelected) return it.name
        }
        return null
    }
}