package com.mtime.base.signal;

import android.os.Handler;
import android.os.Looper;

import com.mtime.base.utils.MLogWriter;
import com.mtime.base.utils.MToastUtils;
import com.mtime.base.utils.recorder.Recorder;
import com.mtime.base.utils.recorder.RecorderManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2018-01-30
 */

@SuppressWarnings("ALL")
public abstract class AbsSignal {
    // 断开连接
    public static final int DISCONNECTED = Integer.MAX_VALUE;
    // auth 成功
    public static final int AUTH_SUCCESS = DISCONNECTED - 1;
    // auth 失败
    public static final int AUTH_FAIL = AUTH_SUCCESS - 1;

    public interface EventListener {
        void handleSignal(int code, JSONObject result) throws JSONException;
    }

    public interface EventTypeListener {
        void handleEvent(String eventType, JSONObject result) throws JSONException;
    }

    protected SocketManager mSocket;
    protected final Handler mMainHandler;
    private Recorder mSignalRecorder;
    private MLogWriter mLog = new MLogWriter(getLogTag());
    protected boolean mConnected;

    private JSONObject mDisconnected = null;

    private List<EventListener> mEvents = new CopyOnWriteArrayList<>();
    private List<EventTypeListener> mTypeEvents = new CopyOnWriteArrayList<>();
    private boolean mToastEnabled = false;

    protected AbsSignal(String host) {
        mSocket = new SocketManager(host);
        mMainHandler = new Handler(Looper.getMainLooper());
        mSignalRecorder = RecorderManager.get(getLogTag());
        listenSignal();
    }

    /**
     * 返回一个 tag ，默认使用简单类名，但是混淆会改类名
     *
     * @return tag
     */
    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    private void listenSignal() {
        mSocket.onConnected(new SocketManager.ConnectedListener() {
            @Override
            public void onConnected(Object... args) {
                AbsSignal.this.onConnected(args);
            }
        });
        mSocket.onDisconnect(new SocketManager.DisconnectListener() {
            @Override
            public void onDisconnect(Object... args) {
                AbsSignal.this.onDisconnect(args);
            }
        });
    }

    protected void onConnected(Object... args) {
        mSignalRecorder.open();
        toast("connected " + Arrays.toString(args));
        mConnected = true;
    }

    protected void onDisconnect(Object... args) {
        toast("disconnected " + Arrays.toString(args));
        mSignalRecorder.close();
        if (mConnected) {
            if (mDisconnected == null) {
                mDisconnected = new JSONObject();
                try {
                    mDisconnected.put("cmdCode", DISCONNECTED);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            dispatchEvent(DISCONNECTED, mDisconnected);
        }
    }

    protected void toast(String msg) {
        mLog.w(msg);
        mSignalRecorder.record(msg);
        if (mToastEnabled) {
            MToastUtils.showShortToast(msg);
        }
    }

    public void setToastEnabled(boolean enabled) {
        mToastEnabled = enabled;
    }

    protected void dealCmdCode(final JSONObject result) throws JSONException {
        if (!result.has("cmdCode")) {
            return;
        }
        final int cmdCode = result.optInt("cmdCode");
        dispatchEvent(cmdCode, result);
    }

    protected void dispatchEvent(final int cmdCode, final JSONObject result) {
        for (final EventListener signalListener : mEvents) {
            if (signalListener != null) {
                dispatchToUI(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            signalListener.handleSignal(cmdCode, result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    protected void dispatchEvent(final String eventType, final JSONObject result) {
        for (final EventTypeListener signalListener : mTypeEvents) {
            if (signalListener != null) {
                dispatchToUI(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            signalListener.handleEvent(eventType, result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    protected void dispatchToUI(Runnable run) {
        mMainHandler.post(run);
    }

    /**
     * 注册 信令事件
     *
     * @param listener listener
     */
    public void registerEvent(EventListener listener) {
        if (listener == null) {
            return;
        }
        if (!mEvents.contains(listener)) {
            mEvents.add(listener);
        }
    }

    /**
     * 反注册
     *
     * @param listener listener 需要和 注册时的 listener 是同一个对象
     */
    public void unregisterEvent(EventListener listener) {
        if (listener != null) {
            mEvents.remove(listener);
        }
    }

    /**
     * 注册 信令事件
     *
     * @param listener listener
     */
    public void registerEvent(EventTypeListener listener) {
        if (listener == null) {
            return;
        }
        if (!mTypeEvents.contains(listener)) {
            mTypeEvents.add(listener);
        }
    }

    /**
     * 反注册
     *
     * @param listener listener 需要和 注册时的 listener 是同一个对象
     */
    public void unregisterEvent(EventTypeListener listener) {
        if (listener != null) {
            mTypeEvents.remove(listener);
        }
    }

    protected void send(String signal, Map<String, Object> params) {
        send(signal, params, null);
    }

    protected void send(String signal, Map<String, Object> params, SignalListener listener) {
        send(signal, params, listener, true);
    }

    protected void send(final String signal, Map<String, Object> params, final SignalListener listener,
                        final boolean uithread) {
        toast(signal + " send " + params.toString());
        if (listener == null) {
            mSocket.send(signal, params);
        } else {
            mSocket.send(signal, params, new SocketManager.DataEventListener() {
                @Override
                public void onSuccess(final String msg, final JSONObject data) {
                    toast(signal + " success " + data.toString());
                    if (uithread) {
                        dispatchToUI(new Runnable() {
                            @Override
                            public void run() {
                                callListener(listener, msg, data, true);
                            }
                        });
                    } else {
                        callListener(listener, msg, data, true);
                    }
                }

                @Override
                public void onFailure(final String msg, final JSONObject data) {
                    toast(signal + " fail " + msg + " " + data);
                    if (uithread) {
                        dispatchToUI(new Runnable() {
                            @Override
                            public void run() {
                                callListener(listener, msg, data, false);
                            }
                        });
                    } else {
                        callListener(listener, msg, data, false);
                    }
                }
            });
        }
    }

    private void callListener(SignalListener listener,
                              String msg, JSONObject data, boolean suc) {
        if (listener == null) return;
        if (listener instanceof DataSignalListener) {
            DataSignalListener li = (DataSignalListener) listener;
            if (suc) {
                li.onSignalSuccess(msg, data);
            } else {
                li.onSignalFailure(msg, data);
            }
        } else {
            if (suc) {
                listener.onSignalSuccess(msg);
            } else {
                listener.onSignalFailure(msg);
            }
        }
    }

    /**
     * 释放资源，清空 listener
     */
    public void release() {
        mSocket.release();
        mTypeEvents.clear();
        mEvents.clear();
    }

    public void activeSocket() {
        mSocket.connect();
    }

    public void reactiveSocket() {
        mSocket.reconnect();
    }

    public void deactiveSocket() {
        mSocket.disconnect();
    }

}
