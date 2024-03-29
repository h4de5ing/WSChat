package com.example.wschat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wschat.R
import com.github.h4de5ing.zxing.view.ScanCodeView
import com.google.zxing.Result

class ZXingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zxing)
        findViewById<ScanCodeView>(R.id.scan_code).setOnScanCodeListener(object :
            ScanCodeView.OnScanCodeListener {
            override fun onScanCodeSucceed(result: Result) {
                showToast("编码格式:${result.barcodeFormat}")
                val code = result.text
                Log.i("gh0st", "扫描结果：$code")
                setResult(RESULT_OK, Intent().putExtra("data", code))
                finish()
            }

            override fun onScanCodeFailed(exception: Exception) {}
        })
    }

    fun showToast(tips: String) {
        runOnUiThread { Toast.makeText(this, tips, Toast.LENGTH_SHORT).show() }
    }
}