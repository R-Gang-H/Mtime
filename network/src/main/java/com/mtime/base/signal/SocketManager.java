package com.mtime.base.signal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import android.util.Log;

import com.mtime.base.utils.CollectionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by yuhengyi on 2017/7/19.
 * <p>
 * 匹配房间 加入房间 离开房间 取消匹配等 长连接
 * 看代码前 要先阅读 信令wiki文档http://gitlab.mtime-dev.com/short-video/svs-room/wikis/home
 */

public class SocketManager {

    public static final String WEAK_UP_SOCKET = "mtimeinterweakupsocket";

    private static final int RECONNECT_DELAY = 1500;
    // 发送消息 超时
    private static final long SOCKET_ACK_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = -2;

    private Socket sSocket;
    private static OkHttpClient okHttpClient;

    /**
     * 是否需要主动重连
     */
    private boolean mNeedReconnect = false;

    private static final ConcurrentHashMap<String, List<Cookie>> sCookies = new ConcurrentHashMap<>();

    private final Handler mReconnectHandler;

    private boolean mIsUserDisconnect;

    private final JSONObject mInnerError = new JSONObject();

    SocketManager(String host) {
        mReconnectHandler = new ReconnectHandler(LooperThread.get().getLooper());
        IO.Options options = new IO.Options();
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.cookieJar(new CookieJar() {

                private String url(HttpUrl url) {
                    return url.scheme() + "://" + url.host() + ":" + url.port() + url.encodedPath();
                }

                @Override
                public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                    if (CollectionUtils.size(cookies) > 0) {
                        sCookies.put(url(url), cookies);
                    }
                }

                @Override
                public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                    List<Cookie> cookies = sCookies.get(url(url));
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            });
            okHttpClient = builder.build();
        }
        options.webSocketFactory = okHttpClient;
        options.callFactory = okHttpClient;
        options.transports = new String[]{
                "websocket"
        };
        options.reconnection = false;
        try {
            sSocket = IO.socket(host, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Looper getLooper() {
        return LooperThread.get().getLooper();
    }

    void onConnected(final ConnectedListener listener) {
        sSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mReconnectHandler.removeCallbacksAndMessages(null);
                listener.onConnected(args);
            }
        });
    }

    void onDisconnect(final DisconnectListener listener) {
        sSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (!mIsUserDisconnect) {
                    mReconnectHandler.sendEmptyMessageDelayed(1, RECONNECT_DELAY);
                }
                listener.onDisconnect(args);
            }
        });
    }

    public void on(String event, EmitterListener emitterListener) {
        sSocket.on(event, emitterListener);
    }

    public void send(String event, Map<String, Object> params) {
        send(event, params, null);
    }

    public void send(String event, Map<String, Object> params, final EventListener listener) {
        if (mNeedReconnect) return;
        if (!sSocket.connected()) {
            connect();
            return;
        }
        if (event.equals(WEAK_UP_SOCKET)) return;
        JSONObject obj = buildParams(params);
        Object[] objects = new Object[]{obj};
        emit(event, objects, listener);
    }

    @SuppressWarnings("unchecked")
    private JSONObject buildParams(Map<String, Object> params) {
        JSONObject obj = new JSONObject();
        if (params == null) return obj;
        Iterator<String> iterator = params.keySet().iterator();
        try {
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = params.get(key);
                if (value instanceof Map) {
                    JSONObject jObj = buildParams((Map<String, Object>) value);
                    obj.put(key, jObj);
                } else {
                    obj.put(key, value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    public void send(String event, JSONObject jsonObject) {
        send(event, jsonObject, null);
    }

    public void send(String event, JSONObject jsonObject, final EventListener listener) {
        if (mNeedReconnect) return;
        if (!sSocket.connected()) {
            connect();
            return;
        }
        if (event.equals(WEAK_UP_SOCKET)) return;
        Object[] objects = new Object[]{jsonObject};
        emit(event, objects, listener);
    }

    private void emit(String event, Object[] objects, EventListener listener) {
        SocketTimeoutAck ack = null;
        if (listener != null) {
            ack = new SocketTimeoutAck(listener);
            mReconnectHandler.postDelayed(ack, SOCKET_ACK_TIMEOUT);
        }
        sSocket.emit(event, objects, ack);
    }

    /**
     * 连接socket
     */
    void connect() {
        if (sSocket == null) {
            return;
        }
        sSocket.connect();
        mReconnectHandler.sendEmptyMessageDelayed(1, RECONNECT_DELAY);
        mIsUserDisconnect = false;
        mNeedReconnect = false;
    }

    public void cancelAttemptReconnet() {
        mIsUserDisconnect = true;
    }

    /**
     * 主动重连
     */
    void reconnect() {
        if (mNeedReconnect) {
            connect();
            mNeedReconnect = false;
        }
        mIsUserDisconnect = false;
        mReconnectHandler.sendEmptyMessageDelayed(1, RECONNECT_DELAY);
    }

    /**
     * 断开
     */
    void disconnect() {
        if (null != sSocket) {
            sSocket.disconnect();
        }
        mIsUserDisconnect = true;
        mReconnectHandler.removeCallbacksAndMessages(null);
        mNeedReconnect = true;
    }

    /**
     * 释放资源
     */
    void release() {
        if (sSocket != null) {
            sSocket.disconnect();
        }
        mReconnectHandler.removeCallbacksAndMessages(null);
        mIsUserDisconnect = true;
        sCookies.clear();

        mNeedReconnect = false;
    }

    private class ReconnectHandler extends Handler {

        private ReconnectHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (sSocket != null && !sSocket.connected()) {
                sSocket.connect();
                Log.i("socket", "reconnect");
                sendEmptyMessageDelayed(1, 1500);
            }
        }
    }

    private class SocketTimeoutAck implements Ack, Runnable {

        EventListener mListener;
        // 原子操作，防止多线程异常
        private final AtomicBoolean handling = new AtomicBoolean(false);

        SocketTimeoutAck(EventListener listener) {
            mListener = listener;
        }

        @Override
        public void call(Object... args) {
            if (!handling.compareAndSet(false, true)) return;
            mReconnectHandler.removeCallbacks(this);
            JSONObject result = (JSONObject) args[0];
            String msg = result.has("showMsg") ?
                    result.optString("showMsg", "")
                    : result.optString("msg", "");
            if (result.has("code") && result.optInt("code") == 1) {
                if (mListener != null)
                    mListener.onSuccess(msg, result);
            } else {
                if (mListener != null) {
                    if (mListener instanceof DataEventListener) {
                        DataEventListener del = (DataEventListener) mListener;
                        del.onFailure(msg, result);
                    } else {
                        mListener.onFailure(msg, result.optInt("code"));
                    }
                }
            }
            mListener = null;
        }

        @Override
        public void run() {
            if (!handling.compareAndSet(false, true)) return;
            if (mListener != null) {
                String msg = "socket send timeout: -2";
                if (mListener instanceof DataEventListener) {
                    fillInnerMsgCode(msg, SOCKET_TIMEOUT);
                    DataEventListener del = (DataEventListener) mListener;
                    del.onFailure(msg, mInnerError);
                } else {
                    mListener.onFailure(msg, SOCKET_TIMEOUT);
                }
            }
            mListener = null;
        }
    }

    private void fillInnerMsgCode(String msg, int code) {
        try {
            mInnerError.put("code", code);
            mInnerError.put("showMsg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface EventListener {
        void onSuccess(String msg, JSONObject data);

        void onFailure(String msg, int code);
    }

    public static abstract class DataEventListener implements EventListener {
        public void onFailure(String msg, JSONObject data) {
            onFailure(msg, data.optInt("code"));
        }

        @Override
        public void onFailure(String msg, int code) {
        }
    }

    public static abstract class EmitterListener implements Emitter.Listener {

        @Override
        public final void call(Object... args) {
            JSONObject result = (JSONObject) args[0];
            try {
                onRecive(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public abstract void onRecive(JSONObject data) throws JSONException;
    }

    interface ConnectedListener {
        void onConnected(Object... args);
    }

    interface DisconnectListener {
        void onDisconnect(Object... args);
    }
}
