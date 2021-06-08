package com.example.wschat.ws;


import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WSClient {
    private OkHttpClient okHttpClient;
    private WebSocket webSocket;
    private boolean isConnected = false;
    private static WSClient client;
    private String mUrl = "";

    public static WSClient getClient() {
        if (client == null) {
            client = new WSClient();
        }
        return client;
    }

    public void retry(String url) {
        mUrl = url;
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }
        if (webSocket != null) {
            webSocket.close(1000, "disconnect");
        }
        if (!isConnected && !mUrl.isEmpty()) {
            connect(url);
        }
    }

    public boolean isConnected() {
        return isConnected && webSocket != null;
    }

    public void updateStatus() {
        if (wsStatusUpdateListener != null) {
            wsStatusUpdateListener.update(isConnected);
        }
    }

    public void dispatcher() {
        println("关闭WebSocket终端");
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }
        if (webSocket != null) {
            webSocket.close(1000, "disconnect");
        }
    }

    private WSMessageUpdateListener wsMessageUpdateListener;
    private WSStatusUpdateListener wsStatusUpdateListener;

    public void setWSMessageListener(WSMessageUpdateListener listener) {
        wsMessageUpdateListener = listener;
    }

    public void setWsStatusUpdateListener(WSStatusUpdateListener listener) {
        wsStatusUpdateListener = listener;
    }

    private void connect(String url) {
        println("connect " + url);
        if (webSocket != null) {
            println("重连");
            isConnected = false;
            webSocket.cancel();
        }
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
        }

        webSocket = okHttpClient.newWebSocket(new Request.Builder().url(url).build(), new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                isConnected = true;
                println("WebSocket 打开成功");
                updateStatus();
            }

            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                isConnected = false;
                println("WebSocket 已经关闭");
                updateStatus();
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                isConnected = false;
                println("WebSocket 服务器端主动关闭");
                updateStatus();
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                isConnected = false;
                println("WebSocket 打开失败 " + t.getMessage());
                updateStatus();
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                isConnected = true;
                println("接收到消息1:" + text);
                if (wsMessageUpdateListener != null) wsMessageUpdateListener.update(text);
                updateStatus();
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                isConnected = true;
                System.out.println("接收到消息2:" + bytes);
                updateStatus();
            }
        });
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            System.out.println(message);
            webSocket.send(message);
        }
        if (!isConnected && !TextUtils.isEmpty(mUrl)) retry(mUrl);
    }

    private void println(String message) {
        System.out.println(message);
    }
}
