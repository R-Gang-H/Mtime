package com.kotlin.android.publish.component.widget.article.label

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.publish.component.R
import com.kotlin.android.widget.titlebar.TextTouchListener

/**
 * 添加PK
 *
 * Created on 2022/4/18.
 *
 * @author o.s
 */
class EditorPKOptionView : LinearLayout {
    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) { initView() }

    private val mTitleHeight = 50.dp
    private val mViewpointHeight = 30.dp
    private val mViewpointCornerRadius = 15.dpF
    private val mTitlePaddingStart = 15.dp
    private val mTitlePaddingEnd = 16.dp
    private val mTitleTextSize = 15F
    private val mViewpointTextSize = 12F

    var action: ((data: TextTouchListener.Data) -> Unit)? = null

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            value?.apply {
                vote?.opts?.apply {
                    if (size == 2) {
                        add(
                            viewpointA = this[0].optDesc,
                            viewpointB = this[1].optDesc,
                        )
                    }
                }
            }
        }

    var title: CharSequence = ""
        set(value) {
            field = value
            titleView.text = value
        }

    @StringRes
    var titleRes: Int = R.string.publish_component_title_input_article
        set(value) {
            field = value
            titleView.setText(value)
        }

    val viewpointA: CharSequence?
        get() = if (_hasViewpoint) viewpointViewA.text else null

    val viewpointB: CharSequence?
        get() = if (_hasViewpoint) viewpointViewB.text else null

    private var _hasViewpoint: Boolean = false

    val hasViewpoint: Boolean
        get() = _hasViewpoint

    /**
     * 添加观点
     */
    fun add(
        viewpointA: CharSequence? = null,
        viewpointB: CharSequence? = null,
    ) {
        if (viewpointA.isNullOrEmpty() || viewpointB.isNullOrEmpty()) {
            _hasViewpoint = false
            syncPKView()
            return
        }
        _hasViewpoint = true
        viewpointViewA.text = viewpointA
        viewpointViewB.text = viewpointB
        syncPKView()
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        addView(titleView)
        addView(viewpointViewA)
        addView(viewpointViewB)
    }

    private val titleView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mTitleHeight)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(mTitlePaddingStart, 0, mTitlePaddingEnd, 0)
            textSize = mTitleTextSize
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.color_404c57))
            setCompoundDrawables(null, null, getCompoundDrawable(R.drawable.ic_editor_18_add_2), null)
            setOnTouchListener(
                TextTouchListener(
                    context = context,
                    textView = this,
                    action = {
                        if (_hasViewpoint) {
                            _hasViewpoint = false
                            syncPKView()
                        } else {
                            action?.invoke(it)
                        }
                    }
                )
            )
        }
    }

    private val viewpointViewA by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mViewpointHeight).apply {
                marginStart = 15.dp
                marginEnd = 15.dp
            }
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = mViewpointCornerRadius
            )
            gravity = Gravity.CENTER
            setTextColor(getColor(R.color.color_8798af))
            textSize = mViewpointTextSize
            isVisible = false
        }
    }

    private val viewpointViewB by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mViewpointHeight).apply {
                marginStart = 15.dp
                marginEnd = 15.dp
                topMargin = 10.dp
                bottomMargin = 15.dp
            }
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = mViewpointCornerRadius
            )
            gravity = Gravity.CENTER
            setTextColor(getColor(R.color.color_8798af))
            textSize = mViewpointTextSize
            isVisible = false
        }
    }

    private fun syncPKView() {
        viewpointViewA.isVisible = _hasViewpoint
        viewpointViewB.isVisible = _hasViewpoint
        syncAddView()
    }

    /**
     * 同步添加视图的显示隐藏
     */
    private fun syncAddView() {
        val addDrawable = if (_hasViewpoint) {
            getCompoundDrawable(R.drawable.ic_editor_18_del)
        } else {
            getCompoundDrawable(R.drawable.ic_editor_18_add_2)
        }
        titleView.setCompoundDrawables(null, null, addDrawable, null)
    }
}