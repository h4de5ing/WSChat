package com.example.wschat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wschat.R
import com.example.wschat.adapter.DeleteAdapter
import com.example.wschat.db.MessageItem
import com.example.wschat.ext.showConfirmDialog
import com.example.wschat.viewmodel.PagingViewModel
import com.github.h4de5ing.baseui.base.BaseReturnActivity
import kotlinx.android.synthetic.main.activity_delete.*

class DeleteActivity : BaseReturnActivity() {
    private var adapter: DeleteAdapter? = null
    private val list = mutableListOf<MessageItem>()
    private val pagingViewModel by viewModels<PagingViewModel>()
    private var allChoose = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)
        adapter = DeleteAdapter(this, list)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
        adapter?.setChangeListener {
            try {
                val size = adapter?.map?.filter { it.value }?.toList()?.size
                delete.isEnabled = size != null && size > 0
                result.text = "选中：${size}"
            } catch (e: Exception) {
            }
        }
        all.setOnClickListener {
            allChoose = !allChoose
            if (allChoose) {
                all.text = "取消全选"
                adapter!!.All(true)
            } else {
                all.text = "  全选"
                adapter!!.All(false)
            }
        }
        neverAll.setOnClickListener { adapter?.neverAll() }
        delete.setOnClickListener {
            showConfirmDialog("确认删除？") { isOk ->
                if (isOk) {
                    val list = adapter?.map?.filter { it.value }?.keys?.toList()
                    if (list != null && list.isNotEmpty()) list.forEach { pagingViewModel.delete(it) }
                    println("删除:${list}")
                }
            }
        }
        pagingViewModel.getAllDate().observe(this) {
            list.clear()
            list.addAll(it)
            adapter?.notifyDataSetChanged()
        }
    }
}