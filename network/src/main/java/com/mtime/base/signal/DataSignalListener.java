package com.mtime.base.signal;

import org.json.JSONObject;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-11-17
 */

public abstract class DataSignalListener implements SignalListener {

    public void onSignalSuccess(String msg, JSONObject result) {
        onSignalSuccess(msg);
    }

    @Override
    public void onSignalSuccess(String msg) {
    }

    public void onSignalFailure(String msg, JSONObject result) {
        onSignalFailure(msg);
    }

    @Override
    public void onSignalFailure(String msg) {
    }
}
