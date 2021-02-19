package com.example.wschat

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wschat.adapter.WSListAdapter
import com.example.wschat.db.MessageItem
import com.example.wschat.ext.showConfirmDialog
import com.example.wschat.ui.BaseSearchActivity
import com.example.wschat.ui.SettingsActivity
import com.example.wschat.utils.HttpRequest
import com.example.wschat.viewmodel.PagingViewModel
import com.example.wschat.ws.WSClient
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import kotlin.concurrent.thread

/**
 * 参考微信选择面板
 * 复制 转发 删除 多选 翻译 打开(网址) 文字里面如果包含网址 可以直接打开
 * 搜索
 */
class MainActivity : BaseSearchActivity() {
    private val listAdapter = WSListAdapter()
    var mRecyclerView: RecyclerView? = null
    var tip: TextView? = null

    private val pagingViewModel by viewModels<PagingViewModel>()
    var longPressId = -1L
    var longPressPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(!isTaskRoot)
        println("MainActivity is running !!")
        mRecyclerView = findViewById<View>(R.id.recyclerview) as RecyclerView
        val edit = findViewById<EditText>(R.id.edit)
        val send = findViewById<Button>(R.id.send)
        tip = findViewById<Button>(R.id.tip)
        val managerControl = findViewById<LinearLayout>(R.id.managerControl)
        val inputControl = findViewById<LinearLayout>(R.id.inputControl)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        mRecyclerView!!.layoutManager = linearLayoutManager
        mRecyclerView!!.adapter = listAdapter
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
                    mRecyclerView!!.smoothScrollToPosition(listAdapter.itemCount - 1)
            }
        }
        listAdapter.setOnItemLongClickListener { adapter, view, position ->
            longPressId = (adapter.getItem(position) as MessageItem).id
            longPressPosition = position
            println("长按弹出菜单多选删除$longPressId $longPressPosition")
            registerForContextMenu(view)
            false
        }
        loadWS()
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
                    if (it) {
                        this.visibility = View.GONE
                    } else {
                        this.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_load_server -> showConfirmDialog { if (it) load() }
            R.id.action_clear_server -> showConfirmDialog { if (it) clear() }
            R.id.action_backup_server -> showConfirmDialog { if (it) showToast("备份服务器信息") }
            R.id.action_clear_local -> showConfirmDialog { if (it) with(App) { dao.clear() } }
            R.id.action_sort_local -> showConfirmDialog { if (it) showToast("本地排序") }
            R.id.action_backup_local -> showConfirmDialog { if (it) showToast("备份本地信息") }
        }
        return super.onOptionsItemSelected(item)
    }

    //Item长按上下文菜单
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.pop, menu)
        menu.setHeaderIcon(R.mipmap.ic_launcher_round)
        menu.setHeaderTitle("选项操作")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                App.dao.deleteMessage(longPressId)
                listAdapter.notifyItemRemoved(longPressPosition)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun load() {
        thread {
            val response: String =
                HttpRequest.sendGet("${App.httpServer}/messages", null, null)
            val jsonArray = JSONArray(response)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                println(
                    "id:" + item.getString("id") + " message:" + item.getString("message") + " date:" + item.getLong(
                        "date"
                    )
                )
            }
        }
    }

    private fun clear() {
        thread {
            val response: String =
                HttpRequest.sendGet("${App.httpServer}/clear", null, null)
            runOnUiThread {
                showToast(response)
            }
        }
    }
}