package com.kotlin.android.publish.component.widget.article.view.item

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.logF
import com.kotlin.android.publish.component.logH
import com.kotlin.android.publish.component.widget.article.view.EditorState
import com.kotlin.android.publish.component.widget.article.view.entity.IElementData
import com.kotlin.android.publish.component.widget.article.view.entity.MovieElementData
import com.kotlin.android.publish.component.widget.article.xml.entity.Element

/**
 * 电影卡片
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
class MovieCard : FrameLayout, IItemView {

    private val mHeight = 74.dp

    override val view: View
        get() = this

    override var element: Element
        get() = elementData.element
        set(value) {
            elementData.element = value
            fillData()
        }

    override val elementData: IElementData = MovieElementData()

    val movieElementData: MovieElementData = elementData as MovieElementData

    override val count: Int = 0
    override val hasDelete: Boolean = true
    override val hasMove: Boolean = true
    override val hasDesc: Boolean = false
    override val hasLink: Boolean = true

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) { initView() }

    override var focusChanged: ((View, Boolean) -> Unit)? = null

    override var hasFocused: ((View, Boolean) -> Unit)? = null

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        logF(gainFocus)
        focusChanged?.invoke(this, gainFocus)
        state = if (gainFocus) {
            hasFocused?.invoke(this, gainFocus)
            EditorState.EDIT
        } else {
            EditorState.NORMAL
        }
    }

    override var state: EditorState = EditorState.NORMAL
        set(value) {
            field = value
            dispatchState(value)
        }

    /**
     * 正常状态
     */
    override fun normalState() {
        logH("normalState", height)
    }

    /**
     * 编辑状态 修改/删除
     */
    override fun editState() {
        logH("  editState", height)
    }

    /**
     * 移动状态
     */
    override fun moveState() {
        logH("  moveState", height)
    }

    var data: Movie? = null
        set(value) {
            field = value
            movieElementData.movie = value
            fillData()
        }

    fun initView() {
        layoutParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, mHeight)

        addView(titleView)
        addView(labelView)
    }

    private val labelView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(16.dp, 16.dp).apply {
                gravity = Gravity.CENTER_VERTICAL
                marginStart = 25.dp
            }
            setImageResource(R.drawable.icon_publish_content_movie)

        }
    }

    private val titleView by lazy {
        TextView(context).apply {
            setBackground(colorRes = R.color.color_f2f3f6, cornerRadius = cornerRadius)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                setMargins(itemMargin, itemMargin, itemMargin, itemMargin)
                gravity = Gravity.CENTER_VERTICAL
            }
            gravity = Gravity.CENTER_VERTICAL
            setPadding(45.dp, 0, 75.dp, 0)
            setTextColor(getColor(R.color.color_303a47))
            textSize = 17F
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
    }

    private fun fillData() {
        post {
            titleView.text = movieElementData.movie?.name.orEmpty()
        }
    }
}