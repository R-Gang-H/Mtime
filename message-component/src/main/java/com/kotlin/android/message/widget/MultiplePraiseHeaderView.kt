package com.kotlin.android.message.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.message.databinding.MessageViewMultiplePraiseHeaderBinding

/**
 * Created by zhaoninglongfei on 2022/4/28
 * 多人点赞头像view
 */

@BindingAdapter("multiple_praise_header")
fun setMultiplePraiseHeader(
    view: MultiplePraiseHeaderView,
    property: MultiplePraiseHeaderView.MultiplePraiseHeader?
) {
    view.applyMultiplePraiseHeader(property)
}

class MultiplePraiseHeaderView : ConstraintLayout {

    private lateinit var binding: MessageViewMultiplePraiseHeaderBinding

    data class MultiplePraiseHeader(
        val authHeader1: AuthHeaderView.AuthHeader,
        val authHeader2: AuthHeaderView.AuthHeader,
        var unread: Boolean? = false,//是否未读
    ) : ProguardRule

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        binding = MessageViewMultiplePraiseHeaderBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(binding.root)
    }

    fun applyMultiplePraiseHeader(property: MultiplePraiseHeader?) {
        binding.multipleHeader = property
    }
}