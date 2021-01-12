package com.example.wschat

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wschat.adapter.MessagePagingAdapter
import com.example.wschat.db.MessageItem
import com.example.wschat.ui.SettingsActivity
import com.example.wschat.viewmodel.PagingViewModel
import com.example.wschat.ws.WSClient

class MainActivity : AppCompatActivity() {
    private val listAdapter = MessagePagingAdapter(this)
    var mRecyclerView: RecyclerView? = null
    var tip: TextView? = null

    private val pagingViewModel by viewModels<PagingViewModel>()
    var longPressId = -1L
    var longPressPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("MainActivity is running !!")
        mRecyclerView = findViewById<View>(R.id.recyclerview) as RecyclerView
        val edit = findViewById<EditText>(R.id.edit)
        val send = findViewById<Button>(R.id.send)
        tip = findViewById<Button>(R.id.tip)
        val managerControl = findViewById<LinearLayout>(R.id.managerControl)
        val inputControl = findViewById<LinearLayout>(R.id.inputControl)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.adapter = listAdapter
        send.setOnClickListener {
            val message = edit.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                WSClient.getClient().sendMessage(message)
                edit.setText("")
            }
        }
        pagingViewModel.getLiveData().observe(this) {
            listAdapter.submitList(it)
            runOnUiThread {
                listAdapter.notifyDataSetChanged()
                //mRecyclerView!!.smoothScrollToPosition(listAdapter.itemCount)
            }
        }
        listAdapter.addOnItemClickListener {
            println("点击了Item$it")
        }
        listAdapter.addOnItemLongClickListener { view, position, item ->
            println("长按弹出菜单多选删除")
            //inputControl.visibility = View.GONE
            //managerControl.visibility = View.VISIBLE
            longPressId = item.id
            longPressPosition = position
            registerForContextMenu(view)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
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
        menu.setHeaderTitle("设备操作")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                //App.dao.deleteMessage(longPressId)
                //listAdapter.notifyItemRemoved(longPressPosition)
            }
        }
        return super.onContextItemSelected(item)
    }
}