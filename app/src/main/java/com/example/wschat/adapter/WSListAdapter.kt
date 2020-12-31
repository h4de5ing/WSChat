package com.example.wschat.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wschat.utils.CopyUtils
import com.example.wschat.R


class WSListAdapter(layoutRes: Int = R.layout.item_tv) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutRes) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv, item)
        val copy = holder.getView<TextView>(R.id.copy)
        copy.setOnClickListener {
            CopyUtils.copy(context, copy, item)
        }
    }
}