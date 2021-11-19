package com.example.wschat.ui

import android.os.Bundle
import android.view.KeyEvent
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.wschat.R
import com.github.h4de5ing.baseui.base.BaseReturnActivity
import kotlinx.android.synthetic.main.activity_webview.*
import me.jingbin.web.ByWebView

//https://github.com/youlookwhat/ByWebView
class WebViewActivity : BaseReturnActivity() {
    private lateinit var byWebView: ByWebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        var url = intent.getStringExtra("url")
        byWebView = ByWebView
            .with(this)
            .setWebParent(container, LinearLayout.LayoutParams(-1, -1))
            .useWebProgress(ContextCompat.getColor(this, R.color.colorAccent))
            .loadUrl(url)
    }

    override fun onPause() {
        super.onPause()
        byWebView.onPause()
    }

    override fun onResume() {
        super.onResume()
        byWebView.onResume()
    }

    override fun onDestroy() {
        byWebView.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return byWebView.handleKeyEvent(keyCode, event)
    }
}