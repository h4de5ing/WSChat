package com.example.wschat.adapter

import android.view.View
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingAdapter<T, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, VH>(diffCallback) {
    private lateinit var itemClickListener: (item: T) -> Unit
    private lateinit var itemLongClickListener: (view: View, item: T) -> Unit
    fun addOnItemClickListener(listener: (item: T) -> Unit) {
        itemClickListener = listener
    }

    fun addOnItemLongClickListener(listener: (view: View, item: T) -> Unit) {
        itemLongClickListener = listener
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        bindItem(holder, position)
        registerClickListener(holder.itemView, position)
    }

    abstract fun bindItem(holder: VH, position: Int)
    private fun registerClickListener(view: View, position: Int) {
        if (::itemClickListener.isInitialized) {
            view.setOnClickListener(fun(_: View) {
                getItem(position)?.let {
                    itemClickListener.invoke(it)
                }
            })
            view.setOnLongClickListener(fun(_: View): Boolean {
                getItem(position)?.let { item ->
                    itemLongClickListener.invoke(view, item)
                }
                return false
            })
        }
    }
}