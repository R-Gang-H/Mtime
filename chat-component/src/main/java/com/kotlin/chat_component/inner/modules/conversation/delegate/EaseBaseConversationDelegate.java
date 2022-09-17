package com.kotlin.chat_component.inner.modules.conversation.delegate;

import com.kotlin.chat_component.inner.adapter.EaseAdapterDelegate;
import com.kotlin.chat_component.inner.adapter.EaseBaseRecyclerViewAdapter;
import com.kotlin.chat_component.inner.modules.conversation.model.EaseConversationSetStyle;

public abstract class EaseBaseConversationDelegate<T, VH extends EaseBaseRecyclerViewAdapter.ViewHolder<T>> extends EaseAdapterDelegate<T, VH> {
    public EaseConversationSetStyle setModel;

    public void setSetModel(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }

    public EaseBaseConversationDelegate(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }
}

