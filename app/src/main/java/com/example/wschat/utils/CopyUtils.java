package com.example.wschat.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class CopyUtils {
    public static void copy(Context activity, View view, String text) {
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", text);
        cm.setPrimaryClip(mClipData);
        Snackbar.make(view, "已经拷贝到剪贴板", Snackbar.LENGTH_SHORT).show();
    }
}
