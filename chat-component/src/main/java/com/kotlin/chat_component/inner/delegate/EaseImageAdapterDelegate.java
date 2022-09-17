package com.kotlin.chat_component.inner.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener;
import com.kotlin.chat_component.inner.model.styles.EaseMessageListItemStyle;
import com.kotlin.chat_component.inner.viewholder.EaseChatRowViewHolder;
import com.kotlin.chat_component.inner.viewholder.EaseImageViewHolder;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRow;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRowImage;

/**
 * 图片代理类
 */
public class EaseImageAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseImageAdapterDelegate() {
    }

    public EaseImageAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.IMAGE;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowImage(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseImageViewHolder(view, itemClickListener);
    }
}
