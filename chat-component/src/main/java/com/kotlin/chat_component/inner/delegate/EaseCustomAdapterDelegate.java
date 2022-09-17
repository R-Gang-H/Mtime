package com.kotlin.chat_component.inner.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener;
import com.kotlin.chat_component.inner.viewholder.EaseChatRowViewHolder;
import com.kotlin.chat_component.inner.viewholder.EaseCustomViewHolder;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRow;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRowCustom;

public class EaseCustomAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.CUSTOM;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowCustom(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseCustomViewHolder(view, itemClickListener);
    }
}
