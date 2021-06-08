package com.example.wschat

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wschat.adapter.WSListAdapter
import com.example.wschat.db.MessageItem
import com.example.wschat.ext.showConfirmDialog
import com.example.wschat.ui.BaseSearchActivity
import com.example.wschat.ui.SettingsActivity
import com.example.wschat.utils.CopyUtils
import com.example.wschat.viewmodel.PagingViewModel
import com.example.wschat.ws.WSClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.widget.SwipeLayout


/**
 * 参考微信选择面板
 * 转发 删除 多选 翻译 打开(网址) 文字里面如果包含网址 可以直接打开
 */
class MainActivity : BaseSearchActivity() {
    private val listAdapter = WSListAdapter()

    private val pagingViewModel by viewModels<PagingViewModel>()
    var longPressId = -1L
    var longPressPosition = -1
    var longPressItem: MessageItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(!isTaskRoot)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerview!!.layoutManager = linearLayoutManager
        recyclerview!!.adapter = listAdapter
        send.setOnClickListener {
            val message = edit.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                WSClient.getClient().sendMessage(message)
                edit.setText("")
            }
        }
        pagingViewModel.getAllDate().observe(this) {
            allDataList.clear()
            it.forEach { item ->
                allDataList.add(Triple(item.content, item.content, "${item.id}"))
            }
            listAdapter.setNewInstance(it)
            runOnUiThread {
                listAdapter.notifyDataSetChanged()
                if (listAdapter.itemCount > 1)
                    recyclerview!!.smoothScrollToPosition(listAdapter.itemCount)
            }
        }
        listAdapter.setOnItemLongClickListener { adapter, view, position ->
            longPressItem = (adapter.getItem(position)) as MessageItem
            longPressId = longPressItem!!.id
            longPressPosition = position
            initPop(view)
            false
        }
        onClickItem.observe(this) { id ->
            println("搜索信息的id:$id")
        }
        loadWS()
        loadShare()
    }

    private fun loadShare() {
        try {
            if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
                edit.setText("${intent.getStringExtra(Intent.EXTRA_TEXT)}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPop(v: View) {
        AnyLayer.dialog(this@MainActivity)
            .contentView(R.layout.pop)
            //.backgroundDimDefault()
            .backgroundBlurPercent(0.05f)
            .swipeDismiss(SwipeLayout.Direction.BOTTOM)
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //复制
                    CopyUtils.copy(this@MainActivity, v, "${longPressItem?.content}")
                }, R.id.button1
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    val intent = Intent()
                    intent.putExtra(Intent.EXTRA_TEXT, "${longPressItem?.content}")
                    intent.type = "text/plain"
                    startActivity(
                        Intent.createChooser(
                            intent,
                            resources.getText(R.string.app_name)
                        )
                    )
                    //转发
                }, R.id.button2
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //删除
                    showConfirmDialog("确实删除 -> ${longPressItem?.content}") {
                        if (it) {
                            pagingViewModel.delete(longPressItem?.id!!)
                        }
                    }
                }, R.id.button3
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //多选
                    managerControl.visibility = View.VISIBLE
                    inputControl.visibility = View.GONE
                }, R.id.button4
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //翻译
                }, R.id.button5
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //打开
                }, R.id.button6
            )
            .show()
    }

    private fun receivedMessage(message: String) {
        runOnUiThread {
            App.dao.insertMessage(
                MessageItem(
                    0,
                    "${System.currentTimeMillis()}",
                    "server",
                    message
                )
            )
        }
    }

    private fun sendMessage(message: String) {
        runOnUiThread {
            App.dao.insertMessage(
                MessageItem(
                    0,
                    "${System.currentTimeMillis()}",
                    "client",
                    message
                )
            )
        }
    }

    private fun loadWS() {
        WSClient.getClient().retry(App.wsServer)
        WSClient.getClient().setWSMessageListener { message ->
            println("拿到进度，更新UI $message")
            receivedMessage(message)
        }
        WSClient.getClient().setWsStatusUpdateListener {
            runOnUiThread {
                tip?.apply {
                    this.visibility = if (it) View.GONE else View.VISIBLE
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_clear_local -> showConfirmDialog { if (it) with(App) { dao.clear() } }
            R.id.action_sort_local -> showConfirmDialog { if (it) showToast("本地排序") }
            R.id.action_backup_local -> showConfirmDialog { if (it) showToast("备份本地信息") }
        }
        return super.onOptionsItemSelected(item)
    }
}