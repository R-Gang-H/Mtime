package com.kotlin.android.message.widget

import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageViewEmptyBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/5/12
 *
 */
class EmptyViewBinder : MultiTypeBinder<MessageViewEmptyBinding>() {

    override fun layoutId(): Int = R.layout.message_view_empty

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is EmptyViewBinder
    }
}