package com.example.wschat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wschat.R
import com.example.wschat.db.MessageItem
import com.example.wschat.utils.CopyUtils

class MessagePagingAdapter(val context: Context) :
    BasePagingAdapter<MessageItem, ArchiveViewHolder2>(POST_COMPARATOR) {
    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<MessageItem>() {
            override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder2 {
        return ArchiveViewHolder2(
            LayoutInflater.from(parent.context).inflate(R.layout.item_tv, parent, false)
        )
    }

    override fun bindItem(holder: ArchiveViewHolder2, position: Int) {
        val item = getItem(position)
        item?.apply {
            holder.tvName.text = item.content
        }
        holder.copy.setOnClickListener {
            CopyUtils.copy(context, holder.copy, item?.content)
        }
    }
}

class ArchiveViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tv)
    val copy: TextView = itemView.findViewById(R.id.copy)
}