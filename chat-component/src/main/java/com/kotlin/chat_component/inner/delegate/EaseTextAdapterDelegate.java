package com.kotlin.chat_component.inner.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener;
import com.kotlin.chat_component.inner.model.styles.EaseMessageListItemStyle;
import com.kotlin.chat_component.inner.viewholder.EaseChatRowViewHolder;
import com.kotlin.chat_component.inner.viewholder.EaseTextViewHolder;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRow;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRowText;

/**
 * 文本代理类
 */
public class EaseTextAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseTextAdapterDelegate() {
    }

    public EaseTextAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.TXT;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowText(parent.getContext(), isSender);
    }

    @Override
    public EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseTextViewHolder(view, itemClickListener);
    }

}
