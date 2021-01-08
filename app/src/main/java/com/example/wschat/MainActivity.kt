package com.example.wschat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("MainActivity is running !!")
        mRecyclerView = findViewById<View>(R.id.recyclerview) as RecyclerView
        val edit = findViewById<EditText>(R.id.edit)
        val send = findViewById<Button>(R.id.send)
        tip = findViewById<Button>(R.id.tip)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.adapter = listAdapter
        send.setOnClickListener {
            val message = edit.text.toString()
            if (message.isNotEmpty()) {
                updateList(message)
                WSClient.getClient().sendMessage(message)
                edit.setText("")
            }
        }
        pagingViewModel.getLiveData().observe(this) {
            listAdapter.submitList(it)
            runOnUiThread {
                listAdapter.notifyItemChanged(listAdapter.itemCount)
                mRecyclerView!!.smoothScrollToPosition(listAdapter.itemCount)
            }
        }
        loadWS()
    }

    private fun updateList(message: String) {
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

    private fun loadWS() {
        WSClient.getClient().retry(App.wsServer)
        WSClient.getClient().setWSMessageListener { message ->
            println("拿到进度，更新UI $message")
            updateList(message)
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
}