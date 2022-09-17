package com.kotlin.android.card.monopoly.widget.menu

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 大富翁下拉功能菜单栏：官方家族、游戏信息、排行榜、游戏介绍、留言板、游戏分享
 *
 * Created on 2020/9/8.
 *
 * @author o.s
 */
class FunctionMenuView : ConstraintLayout {

    private val mIconWidth = 44.dp
    private val mIconHeight = 44.dp
    private val mPaddingLeft = 13.dp
    private val mPaddingTop = 60.dp
    private val mPaddingBottom = 15.dp
    private val mDrawablePadding = 4.dp
    private val mTextSize = 10F

    var action: ((item: Item) -> Unit)? = null

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
        id = R.id.menuView
        translationZ = 2.dpF
        setPadding(mPaddingLeft, mPaddingTop, mPaddingLeft, mPaddingBottom)
        setBackground(
                colorRes = R.color.color_18b2f7,
                cornerRadius = 16.dpF,
                direction = Direction.LEFT_BOTTOM or Direction.RIGHT_BOTTOM
        )

        addItemView(Item.OFFICIAL_FAMILY)
        addItemView(Item.GAME_INFO)
        addItemView(Item.GAME_RANKING_LIST)
        addItemView(Item.GAME_GUIDE)
        addItemView(Item.MSG_BOARD)
        addItemView(Item.GAME_SHARE)

        initConstraintSet()
    }

    private fun addItemView(item: Item) {
        TextView(context).apply {
            id = item.id
            gravity = Gravity.CENTER
            compoundDrawablePadding = mDrawablePadding
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            text = item.label

            getDrawable(item.icon)?.apply {
                setBounds(0, 0, mIconWidth, mIconHeight)
                setCompoundDrawables(null, this, null, null)
            }
            setOnClickListener {
                action?.invoke(item)
            }

            addView(this)
        }
    }

    private fun initConstraintSet() {
        val set = ConstraintSet()

        val len = childCount
        children.forEachIndexed { index, view ->
            val start = if (index > 0) {
                getChildAt(index - 1)
            } else {
                null
            }
            val end = if (index < len - 1) {
                getChildAt(index + 1)
            } else {
                null
            }
            setConstraintSet(set, view, start, end)
        }

        set.applyTo(this)
    }

    private fun setConstraintSet(set: ConstraintSet, self: View, start: View?, end: View?) {
        val selfId = self.id
        start?.apply {
            set.connect(selfId, ConstraintSet.START, id, ConstraintSet.END)
        } ?: set.connect(selfId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        end?.apply {
            set.connect(selfId, ConstraintSet.END, id, ConstraintSet.START)
        } ?: set.connect(selfId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(selfId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

        set.constrainWidth(selfId, mIconWidth)
        set.constrainHeight(selfId, ConstraintSet.WRAP_CONTENT)
    }

    /**
     * 菜单项
     */
    enum class Item(val label: String, @DrawableRes val icon: Int, @IdRes val id: Int) {
        OFFICIAL_FAMILY("官方家族", R.drawable.ic_official_family, R.id.menuItem0),
        GAME_INFO("游戏信息", R.drawable.ic_game_infos, R.id.menuItem1),
        GAME_RANKING_LIST("排行榜", R.drawable.ic_game_ranking_list, R.id.menuItem2),
        GAME_GUIDE("游戏介绍", R.drawable.ic_game_introduce, R.id.menuItem3),
        MSG_BOARD("留言板", R.drawable.ic_msg_board, R.id.menuItem4),
        GAME_SHARE("游戏分享", R.drawable.ic_game_share, R.id.menuItem5),
    }
}