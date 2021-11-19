package com.example.wschat.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.wschat.R

fun showImage(context: Context, bitmap: Bitmap) {
    val alertBuilder = AlertDialog.Builder(context)
    alertBuilder.setIcon(context.getDrawable(R.mipmap.ic_launcher))
    alertBuilder.setTitle("二维码")
    val iv = ImageView(context)
    iv.setImageBitmap(bitmap)
    alertBuilder.setView(iv)
    alertBuilder.setPositiveButton(android.R.string.ok, null)
    alertBuilder.setNegativeButton(android.R.string.cancel, null)
    alertBuilder.create().show()
}