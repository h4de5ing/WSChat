package com.example.wschat

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wschat.adapter.WSListAdapter
import com.example.wschat.db.MessageItem
import com.example.wschat.ext.showConfirmDialog
import com.example.wschat.ui.*
import com.example.wschat.utils.*
import com.example.wschat.viewmodel.PagingViewModel
import com.github.h4de5ing.baseui.CopyUtils
import com.github.h4de5ing.netlib.send2WS
import com.github.h4de5ing.netlib.setOnChangeBoolean
import com.github.h4de5ing.netlib.ws
import com.github.h4de5ing.zxing.coder.ZXWriter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.widget.SwipeLayout

/**
 * 参考微信选择面板
 * 转发 删除 多选 翻译 打开(网址) 文字里面如果包含网址 可以直接打开
 */
class MainActivity : BaseSearchActivity() {
    private val listAdapter = WSListAdapter()
    private val pagingViewModel by viewModels<PagingViewModel>()
    var longPressId = -1L
    var longPressPosition = -1
    var longPressItem: MessageItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(!isTaskRoot)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerview!!.layoutManager = linearLayoutManager
        recyclerview!!.adapter = listAdapter
        send.setOnClickListener {
            val message = edit.text.toString()
            if (message.isNotEmpty()) {
                send2WS(message)
                sendMessage(message)
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
                    recyclerview!!.smoothScrollToPosition(listAdapter.itemCount)
            }
        }
        listAdapter.setOnItemLongClickListener { adapter, view, position ->
            longPressItem = (adapter.getItem(position)) as MessageItem
            longPressId = longPressItem!!.id
            longPressPosition = position
            initPop(view)
            false
        }
        onClickItem.observe(this) { id ->
            println("搜索信息的id:$id")
        }
        loadWS()
        loadShare()
        checkPermission()
    }

    private fun loadShare() {
        try {
            if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
                edit.setText("${intent.getStringExtra(Intent.EXTRA_TEXT)}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPop(v: View) {
        AnyLayer.dialog(this@MainActivity)
            .contentView(R.layout.pop)
            .backgroundBlurPercent(0.05f)
            .swipeDismiss(SwipeLayout.Direction.BOTTOM)
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //复制
                    CopyUtils.copy(this@MainActivity, v, "${longPressItem?.content}")
                }, R.id.button1
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    val intent = Intent()
                    intent.putExtra(Intent.EXTRA_TEXT, "${longPressItem?.content}")
                    intent.type = "text/plain"
                    startActivity(
                        Intent.createChooser(
                            intent,
                            resources.getText(R.string.app_name)
                        )
                    )
                    //转发
                }, R.id.button2
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //删除
                    showConfirmDialog("确实删除 -> ${longPressItem?.content}") {
                        if (it) {
                            pagingViewModel.delete(longPressItem?.id!!)
                        }
                    }
                }, R.id.button3
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //多选
                    startActivity(Intent(this@MainActivity, DeleteActivity::class.java))
                }, R.id.button4
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    //生成二维码
                    showImage(this, ZXWriter.createQRCode("${longPressItem?.content}"))
                }, R.id.button5
            )
            .onClick(
                { anyLayer, _ ->
                    anyLayer.dismiss()
                    startActivity(
                        Intent(
                            this@MainActivity,
                            WebViewActivity::class.java
                        ).putExtra("url", "${longPressItem?.content}")
                    )
                    //打开
                }, R.id.button6
            )
            .show()
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
        setOnChangeBoolean { isConnected ->
            runOnUiThread {
                tip?.apply {
                    this.visibility =
                        if (isConnected) View.GONE else View.VISIBLE
                }
            }
        }
        ws(this, App.wsServer) { message ->
            println("拿到进度，更新UI $message")
            receivedMessage(message)
            No.no(this, "收到来自服务器的消息", message)
        }
    }

    private val requestDataLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra("data")
                Log.i("gh0st", "${result.resultCode} $data")
                runOnUiThread {
                    //edit.requestFocus()
                    edit.setText(data)
                }
            }
        }

    private fun checkPermission() {
        val requestMultiplePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
                //通过的权限
                val grantedList = it.filterValues { it }.mapNotNull { it.key }
                //是否所有权限通过
                val allGranted = grantedList.size == it.size
                Log.d("gh0st", "$allGranted 授权结果:${it}")
            }
        requestMultiplePermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_qr -> requestDataLauncher.launch(Intent(this, ZXingActivity::class.java))
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_clear_local -> showConfirmDialog { if (it) with(App) { dao.clear() } }
            R.id.action_sort_local -> showConfirmDialog { if (it) showToast("本地排序") }
            R.id.action_backup_local -> showConfirmDialog { if (it) showToast("备份本地信息") }
        }
        return super.onOptionsItemSelected(item)
    }
}