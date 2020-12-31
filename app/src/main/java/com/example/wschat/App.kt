package com.example.wschat

import android.app.Application
import androidx.preference.PreferenceManager
import java.util.*

class App : Application() {
    companion object {
        var wsServer = "ws://172.16.1.45:8080/ws/wschat-${UUID.randomUUID()}"
    }

    override fun onCreate() {
        super.onCreate()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val signature = sp!!.getString("signature", "")
        signature?.apply {
            if (signature.isNotEmpty()) wsServer = signature
            else
                sp.edit().putString("signature", wsServer).commit()
        }
        println("ws配置信息$wsServer")
    }
}