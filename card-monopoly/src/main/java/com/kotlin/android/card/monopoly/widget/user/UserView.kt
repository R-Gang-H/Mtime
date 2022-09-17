package com.kotlin.android.card.monopoly.widget.user

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.app.data.entity.monopoly.BufferInfo
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.user.UserManager
import kotlinx.android.synthetic.main.view_user_label.view.*

/**
 * 大富翁用户信息：头像、金币、名称、排名、套装、命中道具卡信息
 *
 * Created on 2020/11/19.
 *
 * @author o.s
 */
class UserView : FrameLayout {
    private val mOnlineViewWidth = 10.dp
    private val mOnlineViewHeight = mOnlineViewWidth
    private val mLabelWith = 16.dp
    private val mLabelHeight = mLabelWith
    private val mLabelAddWith = 15.dp
    private val mLabelAddHeight = mLabelAddWith
    private val mLabelPropsWith = 26.dp//14.dp //30.dp
    private val mLabelPropsHeight = mLabelPropsWith

    private val mOnlineStateDrawable by lazy {
        getDrawableStateList(
                normalRes = R.drawable.ic_user_state_offline_full,
                activatedRes = R.drawable.ic_user_state_online_full
        ).apply {
            setBounds(0, 0, mOnlineViewWidth, mOnlineViewHeight)
        }
    }

    private val labelCoinDrawable by lazy {
        getDrawable(R.drawable.ic_label_user_coin)?.apply {
            setBounds(0, 0, mLabelWith, mLabelHeight)
        }
    }
    private val labelAddCoinDrawable by lazy {
        getDrawable(R.drawable.ic_label_user_add)?.apply {
            setBounds(0, 0, mLabelAddWith, mLabelAddHeight)
        }
    }
    private val labelSuitDrawable by lazy {
        getDrawable(R.drawable.ic_label_user_suit)?.apply {
            setBounds(0, 0, mLabelWith, mLabelHeight)
        }
    }
    private val labelFriendDrawable by lazy {
        getDrawable(R.drawable.ic_label_user_friend)?.apply {
            setBounds(0, 0, mLabelWith, mLabelHeight)
        }
    }
    private val labelPropsDrawable by lazy {
        getDrawable(R.drawable.ic_label_user_props)?.apply {
//        getDrawable(R.drawable.ic_magic_wand)?.apply {
            setBounds(0, 0, mLabelPropsWith, mLabelPropsHeight)
        }
    }
    private val rightArrowDrawable by lazy {
        getDrawable(R.drawable.ic_issued_arrow_right)?.let {
            it.setBounds(0, 0, 18.dp, 18.dp)
            DrawableCompat.wrap(it).apply {
                DrawableCompat.setTintList(this, ColorStateList.valueOf(getColor(R.color.color_20a0da)))
            }
        }
    }

    var action: ((event: ActionEvent) -> Unit)? = null

    var userInfo: UserInfo? = null
        set(value) {
            field = value
            fillData()
        }

    var bufferInfo: BufferInfo? = null
        set(value) {
            field = value
            fillBuffView()
        }

    /**
     * true：朋友关系
     */
    var friendRelation: Boolean? = true
        set(value) {
            field = value
            notifyAddFriendUIState(value ?: false)
        }

    var isOnline: Boolean = false
        set(value) {
            field = value
            notifyOnlineState()
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
        val view = inflate(context, R.layout.view_user_label, null)
        addView(view)

        bufferView?.apply {
            setOnClickListener {
                action?.invoke(ActionEvent.BUFF)
            }
        }

        avatarView?.apply {
            setOnClickListener {
                action?.invoke(ActionEvent.AVATAR)
            }
        }

        onlineView?.apply {
            background = mOnlineStateDrawable
            isActivated = userInfo?.isOnline ?: false
        }

        coinView?.apply {
            setBackground(
                    colorRes = R.color.color_19b3c2,
                    cornerRadius = 9.dpF
            )
            if (isSelf()) {
                setPadding(1.dp, 0, 2.dp, 0)
                setCompoundDrawables(labelCoinDrawable, null, labelAddCoinDrawable, null)
            } else {
                setPadding(1.dp, 0, 5.dp, 0)
                setCompoundDrawables(labelCoinDrawable, null, null, null)
            }
            setOnClickListener {
                if (isSelf()) {
                    action?.invoke(ActionEvent.COIN)
                }
            }
        }

        suitView?.apply {
            setBackground(
                    colorRes = R.color.color_19b3c2,
                    cornerRadius = 9.dpF
            )
            setCompoundDrawables(labelSuitDrawable, null, null, null)
            setOnClickListener {
                action?.invoke(ActionEvent.SUIT)
            }
        }

        friendView?.apply {
            setBackground(
                    colorRes = R.color.color_19b3c2,
                    cornerRadius = 9.dpF
            )
            setCompoundDrawables(labelFriendDrawable, null, null, null)
            setOnClickListener {
                action?.invoke(ActionEvent.FRIEND)
            }
        }

        addFriendView?.apply {
            setOnClickListener {
                action?.invoke(ActionEvent.ADD_FRIEND)
            }
        }

        propsView?.apply {
//            setBackground(
//                    colorRes = R.color.color_ffffff,
//                    cornerRadius = 3
//            )
            setCompoundDrawables(labelPropsDrawable, null, null, null)
//            setDrawable(DrawableTextView.LEFT, labelPropsDrawable, mLabelPropsWith, mLabelPropsHeight)
            setOnClickListener {
                if (isSelf()) {
                    action?.invoke(ActionEvent.PROPS)
                } else {
                    action?.invoke(ActionEvent.USE_PROPS)
                }
            }
        }

        signatureView?.apply {
            text = ""
        }

        currentIssuedView?.apply {
            setCompoundDrawables(null, null, rightArrowDrawable, null)
            setOnClickListener { 
                action?.invoke(ActionEvent.CURRENT_ISSUE)
            }
        }
    }

    /**
     * 填充user信息
     */
    private fun fillData() {
        avatarView.setUserInfo(userInfo)
        isOnline = userInfo?.isOnline ?: true
        if (isSelf()) {
            isOnline = true
        }
        coinView?.text = (userInfo?.cardGold ?: 0L).toString()
        suitView?.text = getString(R.string.str_suit, userInfo?.suitCount ?: 0)
        signatureView?.text = userInfo?.signature.orEmpty()
        updateFriendView()
        updatePropsView()
        updateCurrentIssuedView()
    }

    /**
     * 填充buff信息
     */
    private fun fillBuffView() {
        val data = ArrayList<BuffType>()
        bufferInfo?.apply {
            if (hasHackerBuffer) {
                data.add(BuffType.HACKER_CARD)
            }
            if (hasSlaveBuffer) {
                data.add(BuffType.SLAVE_CARD)
            }
            if (hasMammonBuffer) {
                data.add(BuffType.WEALTH_CARD)
            }
            if (hasGuardBuffer) {
                data.add(BuffType.GUARD_CARD)
            }
            if (hasBounceBuffer) {
                data.add(BuffType.BOUNCE_CARD)
            }
            if (hasScampBuffer) {
                data.add(BuffType.ROGUE_CARD)
            }
            if (hasStealthBuffer) {
                data.add(BuffType.HIDE_CARD)
            }
            if (hasPocketBuffer) {
                data.add(BuffType.POCKET_CARD)
            }
            if (hasRobBuffer) {
                data.add(BuffType.ROBBERY_CARD)
            }
            bufferView?.data = data
        }
    }

    /**
     * 更新好友标签视图
     */
    private fun updateFriendView() {
        if (isSelf()) {
            friendView?.apply {
                visible()
                userInfo?.friendCount?.apply {
                    text = "$onlineCount/$totalCount"
                } ?: resetFriendView()
            }
        } else {
            friendView.gone()
        }
    }

    private fun resetFriendView() {
        friendView?.text = "0/0"
    }

    /**
     * 更新道具视图
     */
    private fun updatePropsView() {
        if (isSelf()) {
            propsView?.apply {
//                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11F)
                setText(R.string.props)
            }
        } else {
            propsView?.apply {
//                setTextSize(TypedValue.COMPLEX_UNIT_SP, 8F)
                setText(R.string.use_props)
            }
        }
    }

    private fun updateCurrentIssuedView() {
        if (isSelf()) {
            currentIssuedView?.visible()
        } else {
            currentIssuedView?.gone()
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
     * 通知在线状态
     */
    private fun notifyOnlineState() {
        onlineView?.isActivated = isOnline
    }

    /**
     * 添加好友UI状态
     */
    private fun notifyAddFriendUIState(friendRelation: Boolean) {
        addFriendView?.visibility = if (isSelf()) {
            View.GONE
        } else {
            if (friendRelation) View.GONE else View.VISIBLE
        }
    }

    /**
     * 事件
     */
    enum class ActionEvent {
        /**
         * 头像
         */
        AVATAR,

        /**
         * 金币（我的
         */
        COIN,

        /**
         * 我的套装/TA的套装
         */
        SUIT,

        /**
         * 好友列表
         */
        FRIEND,

        /**
         * 添加好友
         */
        ADD_FRIEND,

        /**
         * 道具卡（我的）
         */
        PROPS,

        /**
         * 对TA使用道具
         */
        USE_PROPS,

        /**
         * 点击buff
         */
        BUFF,
        
        /**
         * 本期发行
         */
        CURRENT_ISSUE,
    }
}