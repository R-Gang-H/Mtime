package com.mtime.network;

/**
 * Created by yinguanping on 17/2/23.
 */

public interface RequestCallback {
    void onSuccess(Object o);

    void onFail(Exception e);

}
