package com.kotlin.chat_component.inner.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.kotlin.chat_component.inner.constants.EaseConstant;
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener;
import com.kotlin.chat_component.inner.model.styles.EaseMessageListItemStyle;
import com.kotlin.chat_component.inner.viewholder.EaseChatRowViewHolder;
import com.kotlin.chat_component.inner.viewholder.EaseExpressionViewHolder;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRow;
import com.kotlin.chat_component.inner.widget.chatrow.EaseChatRowBigExpression;

/**
 * 表情代理类
 */
public class EaseExpressionAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseExpressionAdapterDelegate() {
        super();
    }

    public EaseExpressionAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.TXT && item.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false);
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowBigExpression(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseExpressionViewHolder(view, itemClickListener);
    }
}
