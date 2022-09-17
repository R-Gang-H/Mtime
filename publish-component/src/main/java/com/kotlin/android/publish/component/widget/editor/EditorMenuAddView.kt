package com.kotlin.android.publish.component.widget.editor

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.ext.topMargin
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.EditorFooterLayoutPostBinding

/**
 * 编辑器菜单
 *
 * Created on 2022/4/21.
 *
 * @author o.s
 */
class EditorMenuAddView : LinearLayout {

    enum class Action {
        MOVIE,
        VIDEO,
        LINK
    }

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private var mPadding = 20.dp
    private var mItemHeight = 60.dp
    private var mItemMarginTop = 10.dp
    private var mItemTextSize = 13F

    var action: ((Action) -> Unit)? = null

    /**
     * 显示/隐藏对应位置的按钮
     */
    fun show(index: Int, isShow: Boolean) {
        when (index) {
            0 -> {
                movieLayout.isVisible = isShow
            }
            1 -> {
                videoLayout.isVisible = isShow
            }
            2 -> {
                linkLayout.isVisible = isShow
            }
            else -> {

            }
        }
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(mPadding, mPadding, mPadding, mPadding)

        addView(movieLayout)
        addView(videoLayout)
        addView(linkLayout)
    }

    private val movieLayout by lazy {
        createItem(
            titleRes = R.string.publish_component_menu_add_movie,
            drawableRes = R.drawable.ic_publish_label_movie,
        ).apply {
            setOnClickListener {
                action?.invoke(Action.MOVIE)
            }
        }
    }

    private val videoLayout by lazy {
        createItem(
            titleRes = R.string.publish_component_menu_add_video,
            drawableRes = R.drawable.ic_publish_label_video,
            itemMarginTop = mItemMarginTop,
        ).apply {
            setOnClickListener {
                action?.invoke(Action.VIDEO)
            }
        }
    }

    private val linkLayout by lazy {
        createItem(
            titleRes = R.string.publish_component_menu_add_link,
            drawableRes = R.drawable.ic_publish_label_link,
            itemMarginTop = mItemMarginTop,
        ).apply {
            setOnClickListener {
                action?.invoke(Action.LINK)
            }
        }
    }

    private fun createItem(
        @StringRes titleRes: Int,
        @DrawableRes drawableRes: Int,
        itemMarginTop: Int = 0,
    ): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight).apply {
                topMargin = itemMarginTop
            }
            setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 8.dpF
            )
            addView(createLabel(titleRes = titleRes, drawableRes = drawableRes))
        }
    }

    private fun createLabel(
        @StringRes titleRes: Int,
        @DrawableRes drawableRes: Int
    ): TextView {
        return TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            gravity = Gravity.CENTER
            textSize = mItemTextSize
            setTextColor(getColor(R.color.color_404c57))
            compoundDrawablePadding = 6.dp
            setCompoundDrawables(getCompoundDrawable(drawableRes), null, null, null)
            setText(titleRes)
        }
    }
}