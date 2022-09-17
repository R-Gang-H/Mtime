package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.app.data.entity.monopoly.HitToolCard
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager

/**
 * 大富翁用户信息：头像、金币、名称、排名、套装、命中道具卡信息
 *
 * Created on 2020/11/19.
 *
 * @author o.s
 */
class UserView : FrameLayout {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }
    private val mineProvider by lazy {
        getProvider(IMineProvider::class.java)
    }
    private val homeProvider by lazy {
        getProvider(IHomeProvider::class.java)
    }
    private val mHeight = 60.dp
    private val mAvatarWidth = 40.dp
    private val mAvatarHeight = mAvatarWidth
    private val mBorderWidth = 1.dp
    private val mTitleHeight = 25.dp
    private val mTitleMarginStart = 50.dp
    private val mTitleMarginTop = 3.dp
    private val mBubbleHeight = 35.dp
    private val mBubbleMarginStart = 35.dp

    var action: ((event: ActionEvent) -> Unit)? = null

    var userInfo: UserInfo? = null
        set(value) {
            field = value
            fillUserTitleView()
        }

    var hitToolCard: HitToolCard? = null
        set(value) {
            field = value
            fillBubbleView()
        }

    fun hideBubble() {
        tipsBubbleView.setTips()
    }

    private val avatarView by lazy {
        CircleImageView(context).apply {
            layoutParams = LayoutParams(mAvatarWidth, mAvatarHeight)
            borderColor = getColor(R.color.color_48d6ea)
            borderWidth = mBorderWidth
            isBorderOverlay = false

            setOnClickListener {
                action?.invoke(ActionEvent.AVATAR)
            }
        }
    }

    private val userTitleView by lazy {
        UserTitleView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mTitleHeight).apply {
                marginStart = mTitleMarginStart
                topMargin = mTitleMarginTop
            }
            action = {
                this@UserView.action?.invoke(ActionEvent.obtain(it))
                when (it) {
                    UserTitleView.ActionEvent.COIN -> {
                        if (isSelf()) {
                            mineProvider?.startMemberCenterActivity(context)
                        }
                    }
                    UserTitleView.ActionEvent.BADGE -> {
                        homeProvider?.startToplistGameDetailActivity(userInfo?.rankInfo?.rankType ?: 1L)
                    }
                    UserTitleView.ActionEvent.SUIT -> {
                        userInfo?.apply {
                            mProvider?.startSuitActivity(this)
                        }
                    }
                }
            }
        }
    }

    private val tipsBubbleView by lazy {
        TipsBubble(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mBubbleHeight).apply {
                marginStart = mBubbleMarginStart
                gravity = Gravity.BOTTOM
            }
            gone()
            action = {
                if (TipsBubble.Style.ACTION == it) {
                    this@UserView.action?.invoke(ActionEvent.PROP)
//                    mProvider?.startPropActivity(PROPS_USE_TYPE_2)
                }
            }
        }
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
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mHeight)
        addView(avatarView)
        addView(userTitleView)
        addView(tipsBubbleView)
    }

    private fun fillUserTitleView() {
        avatarView.setUserInfo(userInfo)
        userTitleView.userInfo = userInfo
    }

    private fun fillBubbleView() {
        hitToolCard?.apply {
            tipsBubbleView.apply {
                if (toolCardDescList?.isNotEmpty() == true) {
                    val sb = StringBuilder()
                    toolCardDescList?.forEachIndexed { index, s ->
                        if (index > 0) {
                            sb.append("；")
                        }
                        sb.append("➤")
                        sb.append(s)
                    }
                    setTips(sb.toString())
                } else {
                    showTAState()
                }
            }
        } ?: showTAState()
    }

    /**
     * 显示TA的使用道具气泡
     */
    private fun showTAState() {
        if (!isSelf()) {
            tipsBubbleView.apply {
                visible()
                style = TipsBubble.Style.ACTION
            }
        } else {
            tipsBubbleView.gone()
        }
    }

    /**
     * 判断是否自己
     */
    private fun isSelf(): Boolean {
        return userInfo?.run {
            userId <= 0L || UserManager.instance.userId == userId
        } ?: true
    }

    /**
     * 事件
     */
    enum class ActionEvent {
        COIN,
        BADGE,
        SUIT,
        PROP,
        AVATAR;

        companion object {
            fun obtain(event: UserTitleView.ActionEvent): ActionEvent {
                return when (event) {
                    UserTitleView.ActionEvent.COIN -> COIN
                    UserTitleView.ActionEvent.BADGE -> BADGE
                    UserTitleView.ActionEvent.SUIT -> SUIT
                }
            }
        }
    }
}