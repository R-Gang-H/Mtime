package com.kotlin.android.message.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageViewAuthHeaderBinding
import com.kotlin.android.mtime.ktx.getDrawable

/**
 * Created by zhaoninglongfei on 2022/4/27
 * 带未读提醒的用户认证view
 */

@BindingAdapter("authHeader")
fun setAuthHeader(view: AuthHeaderView, property: AuthHeaderView.AuthHeader?) {
    view.applyAuthHeader(property)
}

@BindingAdapter("headerWidth")
fun setHeaderWidth(view: AuthHeaderView, headerWidth: Int?) {
    view.applyHeaderWidth(headerWidth)
}

@BindingAdapter("multiple")
fun setMultiple(view: AuthHeaderView, multiple: Boolean) {
    view.applyMultiple(multiple)
}

class AuthHeaderView : ConstraintLayout {

    private lateinit var binding: MessageViewAuthHeaderBinding

    data class AuthHeader(
        val userId: Long?,
        val headImg: String?,
        var unread: Boolean? = false,//是否未读
        var authType: Long? = null,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
        var authRole: String? = null//认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
    ) : ProguardRule {
        fun hasAuthType(): Boolean {
            if (authType == null) {
                return false
            }
            return authType == 2L || authType == 3L || authType == 4L
        }

        fun authTypeIcon(): Drawable? {
            return when (authType) {
//                2L -> getDrawable(R.mipmap.ic_auth_yingpingren_60)
//                3L -> getDrawable(R.mipmap.ic_auth_yingren_60)
//                4L -> getDrawable(R.mipmap.ic_auth_jigou_60)
                //这个资源的命名是正确的 但是本期按照产品的意思这样展示认证标
                2L -> getDrawable(R.mipmap.ic_auth_jigou_60)
                3L -> getDrawable(R.mipmap.ic_auth_jigou_60)
                4L -> getDrawable(R.mipmap.ic_auth_yingren_60)
                else -> null
            }
        }
    }

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
        binding = MessageViewAuthHeaderBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(binding.root)
    }

    fun applyAuthHeader(authHeader: AuthHeader?) {
        binding.authHeader = authHeader
    }

    fun applyHeaderWidth(headerWidth: Int?) {
        binding.headerWidth = headerWidth
    }

    fun applyMultiple(multiple: Boolean) {
        binding.multiple = multiple
    }
}