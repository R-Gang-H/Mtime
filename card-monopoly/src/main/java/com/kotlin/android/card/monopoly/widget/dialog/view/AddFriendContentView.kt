package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.card.monopoly.widget.CircleImageView
import com.kotlin.android.card.monopoly.widget.nickname.NickNameView
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 添加卡友对话框内容视图：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class AddFriendContentView : LinearLayout {

    private val mAvatarWidth = 40.dp
    private val mAvatarHeight = 40.dp
    private val mInputHeight = 38.dp
    private val mAvatarMarginBottom = 10.dp
    private val mNickNameMarginStart = 10.dp
    private val mTitleTextSize = 15F
    private val mProgressTextSize = 14F
    private val mProgressLimit = 100

    private val mAvatarView by lazy { initAvatarView() }
    private val mNickNameView by lazy { initNickNameView() }
    private val mInputView by lazy { initInputView() }
    private val mProgressView by lazy { initProgressView() }

    var action: (() -> Unit)? = null

    var avatarUrl: String = ""
        set(value) {
            field = value
            mAvatarView.setUserInfo(value)
        }

    var nickName: String? = ""
        get() = mNickNameView.text.toString()
        set(value) {
            field = value
            mNickNameView.text = value ?: ""
        }

    /**
     * 附言
     */
    var postscript: String? = ""
        get() = mInputView.text
        set(value) {
            field = value
            mInputView.text = value ?: ""
        }

    var isOnline: Boolean = false
        set(value) {
            field = value
            mNickNameView.isOnline = value
        }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val avatarLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mAvatarHeight).apply {
                bottomMargin = mAvatarMarginBottom
            }

            addView(mAvatarView)
            addView(mNickNameView)
        }

        addView(avatarLayout)
        addView(mInputView)
        addView(mProgressView)
        mInputView.hint = getString(R.string.postscript)
    }

    /**
     * 设置头像
     */
    fun setAvatar(url: String) {
//        mAvatarView
    }

    /**
     * 设置昵称
     */
    fun setNickName(nickName: String, isOnline: Boolean = false) {
        mNickNameView.apply {
            text = nickName
            this.isOnline = isOnline
        }
    }

    private fun initAvatarView(): CircleImageView {
        return CircleImageView(context).apply {
            borderWidth = 1.dp
            borderColor = getColor(R.color.color_e3e5ed)
            isBorderOverlay = true
            layoutParams = LayoutParams(mAvatarWidth, mAvatarHeight)
        }
    }

    private fun initNickNameView(): NickNameView {
        return NickNameView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mAvatarHeight).apply {
                marginStart = mNickNameMarginStart
            }
            gravity = Gravity.CENTER_VERTICAL
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
        }
    }

    /**
     * 初始化输入框
     */
    private fun initInputView(): UnitInputView {
        return UnitInputView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)
            setBackground(
                    colorRes = R.color.color_ebedf2,
                    cornerRadius = 19.dpF
            )
            inputChange = {
                mProgressView.progress = it
            }
        }
    }

    /**
     * 初始化输入框
     */
    private fun initProgressView(): NumProgressView {
        return NumProgressView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.END
            }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mProgressTextSize)
            setTextColor(getColor(R.color.color_8798af))
            limit = mInputView.maxLength // mProgressLimit
        }
    }
}
