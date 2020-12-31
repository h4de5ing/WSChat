package com.example.wschat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wschat.adapter.WSListAdapter
import com.example.wschat.ui.SettingsActivity
import com.example.wschat.ws.WSClient

class MainActivity : AppCompatActivity() {
    private val listAdapter = WSListAdapter()
    private val list = mutableListOf<String>()
    var mRecyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("MainActivity is running !!")
        mRecyclerView = findViewById<View>(R.id.recyclerview) as RecyclerView
        val edit = findViewById<EditText>(R.id.edit)
        val send = findViewById<Button>(R.id.send)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.adapter = listAdapter
        listAdapter.setNewInstance(list)
        listAdapter.notifyDataSetChanged()
        send.setOnClickListener {
            val message = edit.text.toString()
            if (message.isNotEmpty()) {
                updateList(message)
                WSClient.getClient().sendMessage(message)
                edit.setText("")
            }
        }
        loadWS()
    }

    private fun updateList(message: String) {
        runOnUiThread {
            mRecyclerView!!.smoothScrollToPosition(listAdapter.itemCount);
            list.add(message)
            listAdapter.notifyItemInserted(list.size - 1)
        }
    }

    private fun loadWS() {
        WSClient.getClient().retry(App.wsServer)
        WSClient.getClient().setWSMessageListener { message ->
            println("拿到进度，更新UI $message")
            updateList(message)
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