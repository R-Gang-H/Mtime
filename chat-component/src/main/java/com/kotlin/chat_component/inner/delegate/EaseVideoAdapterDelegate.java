package com.kotlin.chat_component.inner.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener;
import com.kotlin.chat_component.inner.model.styles.EaseMessageListItemStyle;
import com.kotlin.chat_component.inner.viewholder.EaseChatRowViewHolder;
import com.kotlin.chat_component.inner.viewholder.EaseVideoViewHolder;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRow;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRowVideo;

/**
 * 视频代理类
 */
public class EaseVideoAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseVideoAdapterDelegate() {
    }

    public EaseVideoAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.VIDEO;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowVideo(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseVideoViewHolder(view, itemClickListener);
    }
}
