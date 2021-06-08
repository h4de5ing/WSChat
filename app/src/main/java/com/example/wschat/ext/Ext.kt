package com.example.wschat.ext

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

fun Long.date(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date(this))

fun AppCompatActivity.showConfirmDialog(change: ((Boolean) -> Unit)) {
    this.showConfirmDialog("您确定执行本次操作", change)
}


//确认对话框
fun AppCompatActivity.showConfirmDialog(message: String, change: ((Boolean) -> Unit)) {
    this.runOnUiThread {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            run {
                change(false)
            }
        }
        builder.setPositiveButton(
            android.R.string.ok
        ) { _, _ ->
            run {
                change(true)
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}