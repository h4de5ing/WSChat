package com.example.wschat.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wschat.R
import com.example.wschat.db.MessageItem
import com.example.wschat.ext.date
import com.github.h4de5ing.baseui.CopyUtils


class WSListAdapter constructor(layoutRes: Int = R.layout.item_tv) :
    BaseQuickAdapter<MessageItem, BaseViewHolder>(layoutRes), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: MessageItem) {
        holder.setText(R.id.time, item.time.toLong().date())
        holder.setText(R.id.tv, item.content)
        val copy = holder.getView<TextView>(R.id.copy)
        copy.setOnClickListener { CopyUtils.copy(context, copy, item.content) }
    }
}