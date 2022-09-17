package com.kotlin.android.publish.component.widget.dialog

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.publish.component.R

/**
 * create by lushan on 2022/4/18
 * des:发布成功弹框布局
 **/
class PublishSuccessView @JvmOverloads constructor(
    var ctx: Context,
    var attributeSet: AttributeSet? = null,
    var defStyle: Int = 0
) : LinearLayout(ctx, attributeSet, defStyle) {
    private val ivWidth = 89.dp
    private val ivHeight = 61.dp
    private var successIv: ImageView? = null
    private var tipsTv: TextView? = null
    private var scanBtn: Button? = null
    var listener: (() -> Unit)? = null

    init {
        removeAllViews()
        orientation = LinearLayout.VERTICAL
        val ivLayoutParams = LinearLayout.LayoutParams(ivWidth, ivHeight).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = 28.dp
        }
        successIv = getSuccessImageView()
        addView(successIv, ivLayoutParams)

        val tipsLayoutParams = LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = 17.dp
        }
        tipsTv = getTipsTextView()
        addView(tipsTv, tipsLayoutParams)

        val scanLayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 40.dp).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = 23.dp
            bottomMargin = 26.dp
            leftMargin = 40.dp
            rightMargin = 40.dp
        }
        scanBtn = getScanButton()
        scanBtn?.onClick {
            listener?.invoke()
        }
        addView(scanBtn, scanLayoutParams)

        setBackground(colorRes = R.color.white, cornerRadius = 12.dpF)
    }

    private fun getSuccessImageView(): AppCompatImageView {
        return AppCompatImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.ic_publish_success)
        }
    }

    private fun getTipsTextView(): AppCompatTextView {
        return AppCompatTextView(context).apply {
            text = getString(R.string.publish_success)
            setTextColor(getColor(R.color.color_303a47))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
    }

    private fun getScanButton(): AppCompatButton {
        return AppCompatButton(context).apply {
            text = getString(R.string.publish_component_click_to_scan)
            setTextColor(getColor(R.color.white))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            gravity = Gravity.CENTER
            setBackground(colorRes = R.color.color_20a0da, cornerRadius = 20.dpF)
        }
    }

    fun setTitle(@StringRes resId: Int) {
        tipsTv?.text = getString(resId)
    }

    fun setTitle(title: String) {
        tipsTv?.text = title
    }
}