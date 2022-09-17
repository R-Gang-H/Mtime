package com.kotlin.chat_component.inner.interfaces

import android.view.ViewGroup
import com.hyphenate.chat.EMMessage
import com.kotlin.chat_component.inner.model.styles.EaseMessageListItemStyle
import com.kotlin.chat_component.inner.viewholder.EaseChatRowViewHolder

/**
 * 开发者可以通过实现下面的两个接口，提供相应的ViewHolder和ViewType
 */
interface IViewHolderProvider {
    /**
     * 提供对应的ViewHolder
     * @return key指的的对应的viewType, value为对应的ViewHolder
     * @param parent
     * @param itemClickListener
     * @param itemStyle
     */
    fun provideViewHolder(
        parent: ViewGroup?,
        viewType: Int,
        itemClickListener: MessageListItemClickListener?,
        itemStyle: EaseMessageListItemStyle?
    ): EaseChatRowViewHolder?

    /**
     * 根据消息类型提供相对应的view type
     * @param message
     * @return 返回的为viewType
     */
    fun provideViewType(message: EMMessage?): Int
}