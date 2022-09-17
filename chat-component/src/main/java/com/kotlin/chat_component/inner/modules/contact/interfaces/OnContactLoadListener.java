package com.kotlin.chat_component.inner.modules.contact.interfaces;

import com.kotlin.chat_component.inner.domain.EaseUser;

import java.util.List;

/**
 * 会话列表加载数据的监听
 */
public interface OnContactLoadListener {
    /**
     * 加载完成后回调
     * @param data
     */
    void loadDataFinish(List<EaseUser> data);

    /**
     * 加载数据失败后回调
     * @param message
     */
    default void loadDataFail(String message) {}

}
