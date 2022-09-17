package com.kotlin.android.widget.binder

import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.mtime.ktx.getDrawableOrNull
import com.kotlin.android.mtime.ktx.getStringOrDefault
import com.kotlin.android.mtime.ktx.getStringOrNull
import com.kotlin.android.widget.R
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.databinding.BinderLabelBinding

/**
 * APP全局标签 [LabelBinder]。形如：
 *
 *      -----------------------------------------------------------------
 *      |  正在热映  即将上映                                       全部 >  |
 *      -----------------------------------------------------------------
 *
 *      -----------------------------------------------------------------
 *      |  整体表现                                                全部 ⬇ |
 *      |  每日中午12点更新前一天数据                                        |
 *      -----------------------------------------------------------------
 *
 *      注意：
 *      1，可隐藏对应区域；
 *      2，点击事件已向外传递；
 *      3，标题选中状态已内部实现；
 *
 *      标题高度说明：
 *      1，普通标题高度49.dp = 12.dp（marginTop） + 25.dp（标题高度） + 12.dp（marginBottom）
 *      2，包含子标题的总高度68.dp = 12.dp（marginTop） + 25.dp（标题高度） + 2.dp（间隙） + 17.dp（子标题高度） + 12.dp（marginBottom）
 *
 *
 * [tag] tag标识，用于区别不同 [LabelBinder]，可配合 viewId 进行匹配事件点击回调，默认 null 可以不设置。
 * [title] [titleRes] 标题
 * [secondTitle] [secondTitleRes] 第二标题
 * [subTitle] [subTitleRes] 子标题
 * [actionTitle] [actionTitleRes] 右侧点击按钮文本，不需要时为空即可。
 * [actionDrawable] [actionDrawableRes] 右侧点击按钮Icon，默认 [R.drawable.ic_label_12_arrow_right]，下拉可明确指定 [R.drawable.ic_label_15_triangle_down]
 *
 * [displayAction] true：显示右侧按钮，默认false
 * [displayTitleInfo] true：显示标题的提示信息Icon，默认false
 *
 * [rootMargin] 标题外边距，默认左右边距15dp，上下12dp，即：Rect(15.dp, 12.dp, 15.dp, 12.dp)
 *
 * 点击事件传递：配合 tag 属性
 * [R.id.titleView] 标题点击
 * [R.id.secondTitleView] 第二标题点击
 * [R.id.titleInfoView] 标题提示信息Icon点击
 * [R.id.actionView] 右侧按钮点击
 *
 * Created on 2022/1/17.
 *
 * @author o.s
 */
class LabelBinder(
    val tag: Any? = null,
    val title: CharSequence = "",
    val secondTitle: CharSequence? = null,
    val subTitle: CharSequence? = null,
    var actionTitle: CharSequence? = null,
    var actionDrawable: Drawable? = null,
    @StringRes val titleRes: Int? = null,
    @StringRes val secondTitleRes: Int? = null,
    @StringRes val subTitleRes: Int? = null,
    @StringRes val actionTitleRes: Int? = null,
    @DrawableRes val actionDrawableRes: Int? = R.drawable.ic_label_12_arrow_right,
    val displayAction: Boolean = false,
    val displayTitleInfo: Boolean = false,
    val rootMargin: Rect = Rect(15.dp, 12.dp, 15.dp, 12.dp), // 普通标题高度49.dp = 12.dp + 25.dp + 12.dp
    val isFullSpan: Boolean = true //如果列表是StaggeredGridLayoutManager则填满
) : MultiTypeBinder<BinderLabelBinding>() {
    /**
     * 当前选中的tab位置。actionView
     */
    private var currentPosition = 0

    /**
     * 返回LayoutId，供Adapter使用
     */
    override fun layoutId(): Int = R.layout.binder_label

    /**
     * 数据内容比较，比较两次内容是否一致，刷新UI时用到。
     */
    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return false
    }

    override fun onBindViewHolder(binding: BinderLabelBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.apply {
            root.apply {
                marginTop = rootMargin.top
                marginBottom = rootMargin.bottom
                marginStart = rootMargin.left
                marginEnd = rootMargin.right
                // 如果列表是StaggeredGridLayoutManager则是否填满
                if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                    (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = isFullSpan
                }
            }
            // 第一标题
            titleView.apply {
                setTextColor(
                    getColorStateList(
                        normalColorRes = R.color.color_8798af,
                        pressColorRes = R.color.color_1d2736,
                        selectedColorRes = R.color.color_1d2736,
                    )
                )
                isSelected = currentPosition == 0
                typeface = getTypeface(isSelected)
                textSize = if (isSelected) {
                    18F
                } else {
                    14F
                }
                text = getStringOrDefault(titleRes, title)
            }
            // 第二标题
            secondTitleView.isVisible = secondTitleRes != null || secondTitle.isNullOrEmpty().not()
            if (secondTitleView.isVisible) {
                secondTitleView.apply {
                    setTextColor(
                        getColorStateList(
                            normalColorRes = R.color.color_8798af,
                            pressColorRes = R.color.color_1d2736,
                            selectedColorRes = R.color.color_1d2736,
                        )
                    )
                    isSelected = currentPosition == 1
                    typeface = getTypeface(isSelected)
                    textSize = if (isSelected) {
                        18F
                    } else {
                        14F
                    }
                    text = getStringOrNull(secondTitleRes) ?: secondTitle
                }
                titleView.onClick {
                    currentPosition = 0
                    notifyAdapterSelfChanged()
                    mOnClickListener?.invoke(it, this@LabelBinder)
                }
                secondTitleView.onClick {
                    currentPosition = 1
                    notifyAdapterSelfChanged()
                    mOnClickListener?.invoke(it, this@LabelBinder)
                }


            }
            // 子标题
            subTitleView.isVisible = subTitleRes != null || subTitle.isNullOrEmpty().not()
            if (subTitleView.isVisible) {
                subTitleView.text = getStringOrNull(subTitleRes) ?: subTitle
            }
            // 标题提示信息info
            titleInfoView.isVisible = displayTitleInfo
            if (titleInfoView.isVisible) {
                titleInfoView.onClick {
                    mOnClickListener?.invoke(titleInfoView, this@LabelBinder)
                }
            }
            // 右侧action
            actionView.isVisible = displayAction
            if (actionView.isVisible) {
                // 点击事件传递
                actionView.onClick {
                    mOnClickListener?.invoke(it, this@LabelBinder)
                }
                val actionText = getStringOrNull(actionTitleRes) ?: actionTitle ?: ""
                actionView.text = actionText
                // 处理padding
                if (actionText.isEmpty()) {
                    actionView.setPadding(5.dp, 0, 5.dp, 0)
                    actionView.compoundDrawablePadding = 0
                } else {
                    actionView.setPadding(10.dp, 0, 5.dp, 0)
                    actionView.compoundDrawablePadding = 2.dp
                }
                // 处理右侧Icon
                val endDrawable = actionDrawable ?: getDrawableOrNull(actionDrawableRes)
                endDrawable?.apply {
                    setBounds(0, 0, minimumWidth, minimumHeight)
                }
                actionView.setCompoundDrawables(null, null, endDrawable, null)
                // 处理背景
                if ((endDrawable?.minimumWidth ?: 0) > 15.dp) {
                    actionView.background = null
                } else {
                    actionView.setBackground(
                        colorRes = R.color.color_f3f3f4,
                        cornerRadius = 11.dpF,
                    )
                }
            }
        }
    }

    private fun getTypeface(isSelected: Boolean): Typeface {
        return if (isSelected) {
            Typeface.defaultFromStyle(Typeface.BOLD)
        } else {
            Typeface.defaultFromStyle(Typeface.NORMAL)
        }
    }
}