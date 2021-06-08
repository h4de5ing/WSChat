package com.example.wschat

import android.app.Application
import android.content.Intent
import androidx.preference.PreferenceManager
import com.example.wschat.db.MessageDao
import com.example.wschat.db.MessageDatabase
import com.example.wschat.services.GuardService
import java.util.*

class App : Application() {
    companion object {
        var wsServer = "ws://172.16.1.45:8080/wschat-${UUID.randomUUID()}"
        var httpServer = "http://172.16.1.45:8080"
        lateinit var dao: MessageDao
    }

    override fun onCreate() {
        super.onCreate()
        dao = MessageDatabase.create(this).messageDao()
        loadConfig()
        startService(Intent(this, GuardService::class.java))
    }

    fun loadConfig() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val signature = sp!!.getString("signature", "")
        val http = sp!!.getString("http", "")
        signature?.apply {
            if (signature.isNotEmpty()) wsServer = signature
            else
                sp.edit().putString("signature", wsServer).commit()
        }
        http?.apply {
            if (http.isNotEmpty()) httpServer = http
            else
                sp.edit().putString("http", httpServer).commit()
        }
        println("ws配置信息$wsServer")
    }
}