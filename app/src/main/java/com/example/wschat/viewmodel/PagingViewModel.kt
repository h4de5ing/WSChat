package com.example.wschat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.wschat.App
import com.example.wschat.db.MessageItem

class PagingViewModel : ViewModel() {
    fun getAllDate(): LiveData<MutableList<MessageItem>> {
        return App.dao.selectAllDate()
    }

    fun delete(id: Long) {
        return App.dao.deleteMessage(id)
    }

    fun getLiveData(): LiveData<PagedList<MessageItem>> {
        val factory = App.dao.selectAll()
        val config = PagedList.Config.Builder()
            .setPageSize(10)              // 分页加载的数量
            .setInitialLoadSizeHint(10)   // 初次加载的数量
            .setPrefetchDistance(10)      // 预取数据的距离
            .setEnablePlaceholders(false) // 是否启用占位符（本地数据比较合适，因为远程数据是未知的）
            .build()
        return LivePagedListBuilder(factory, config).build()
    }
}