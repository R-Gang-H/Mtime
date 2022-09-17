package com.kotlin.android.community.family.component.ui.manage.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString

/**
 *
 * @des 家族中加入按钮展示样式
 * @author zhangjian
 * @date 2022/3/22 15:28
 */
class JoinBtnView : LinearLayout {

    var onClickChange: ((type: Long) -> Unit)? = null

    private var textView: AppCompatTextView? = null
    private var type: Long = 0L

    constructor(context: Context) : this(context, null)

    constructor(context: Context, @Nullable attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, @Nullable attr: AttributeSet?, def: Int) : super(
        context,
        attr,
        def
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        textView = AppCompatTextView(context)
        val params = LayoutParams(55.dp, 25.dp)
        textView?.apply {
            setTextColor(getColor(R.color.color_ffffff))
            textSize = 12f
            gravity = Gravity.CENTER
            layoutParams = params
        }
        textView?.onClick {
            onClickChange?.invoke(type)
        }
        addView(textView)
    }

    //设置类型
    @SuppressLint("RestrictedApi")
    fun setStyle(type: Long? = 0) {
        this.type = type ?: 0L
        when (type) {
            FamilyConstant.CONSTANT_STATE_0 -> {
                textView?.apply {
                    gravity = Gravity.CENTER
                    text = getString(R.string.community_join_btn)
                    setBackground(
                        colorRes = R.color.color_20a0da,
                        strokeColorRes = R.color.color_20a0da,
                        cornerRadius = 15.dpF
                    )
                    setPadding(0, 0, 0, 0)
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    setTextColor(getColor(R.color.color_ffffff))
                }
            }

            FamilyConstant.CONSTANT_STATE_1 -> {
                textView?.apply {
                    text = getString(R.string.community_join_btn)
                    setPadding(8.dp, 0, 0, 0)
                    setBackground(
                        colorRes = R.color.color_ffffff,
                        cornerRadius = 15.dpF,
                        strokeWidth = 1.dp,
                        strokeColorRes = R.color.color_20a0da
                    )
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkb, 0, 0, 0)
                    supportCompoundDrawablesTintList =
                        ColorStateList.valueOf(getColor(R.color.color_20a0da))
                    setTextColor(getColor(R.color.color_20a0da))
                }
            }
            FamilyConstant.CONSTANT_STATE_2 -> {
                textView?.apply {
                    gravity = Gravity.CENTER
                    text = getString(R.string.community_joining_btn)
                    setBackground(
                        colorRes = R.color.color_ffffff,
                        cornerRadius = 15.dpF,
                        strokeWidth = 1.dp,
                        strokeColorRes = R.color.color_20a0da
                    )
                    setPadding(0, 0, 0, 0)
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    setTextColor(getColor(R.color.color_20a0da))
                }
            }
            FamilyConstant.CONSTANT_STATE_4 -> {
               textView?.apply {
                    gravity = Gravity.CENTER
                    text = getString(R.string.community_manage_btn)
                   setBackground(
                       colorRes = R.color.color_20a0da,
                       strokeColorRes = R.color.color_20a0da,
                       cornerRadius = 15.dpF
                   )
                   setPadding(0, 0, 0, 0)
                   setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                   setTextColor(getColor(R.color.color_ffffff))
                }
            }
        }
        invalidate()
    }

    fun setText(@StringRes str:Int){
        textView?.text = getString(str)
    }

}