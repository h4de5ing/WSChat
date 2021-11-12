package com.example.wschat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.zxing.view.ScanCodeView
import com.example.wschat.R
import com.example.wschat.utils.resultChange
import com.google.zxing.Result

class ZXingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zxing)
        findViewById<ScanCodeView>(R.id.scan_code).setOnScanCodeListener(object :
            ScanCodeView.OnScanCodeListener {
            override fun onScanCodeSucceed(result: Result) {
                val code = result.text
                Log.i("gh0st", "扫描结果：$code")
                resultChange = code
                setResult(RESULT_OK, Intent().putExtra("data", code))
                finish()
            }

            override fun onScanCodeFailed(exception: Exception) {}
        })
    }
}