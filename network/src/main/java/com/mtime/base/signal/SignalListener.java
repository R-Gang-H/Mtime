package com.mtime.base.signal;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-08-29
 */

public interface SignalListener {
    void onSignalSuccess(String msg);

    void onSignalFailure(String msg);
}
