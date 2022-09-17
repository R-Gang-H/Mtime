package com.kotlin.android.publish.component.widget.article.view.item

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.marginBottom
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.logE
import com.kotlin.android.publish.component.logF
import com.kotlin.android.publish.component.logH
import com.kotlin.android.publish.component.widget.article.view.EditorState
import com.kotlin.android.publish.component.widget.article.view.entity.IElementData
import com.kotlin.android.publish.component.widget.article.view.entity.ImageElementData
import com.kotlin.android.publish.component.widget.article.xml.entity.Element

/**
 * 图片卡片
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
class ImageCard : LinearLayout, IItemView {

    private val mMinHeight = 54.dp
    private val mHeight = ((screenWidth - itemMargin * 2) * 1.1F).toInt()
    private val defBorderMargin = itemMargin - borderMarginOffset
    private var _editMarginRect: Rect = Rect(defBorderMargin, defBorderMargin, defBorderMargin, defBorderMargin)
    private val mMarginV: Int = itemMargin * 2

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

    override val parentItemView: IItemView?
        get() = parent as? IItemView

    override val view: View
        get() = this

    override var element: Element
        get() = elementData.element
        set(value) {
            elementData.element = value
            fillData()
        }

    override val elementData: IElementData = ImageElementData()

    val imageElementData: ImageElementData = elementData as ImageElementData

    override val count: Int = 0
    override val hasDelete: Boolean = true
    override val hasMove: Boolean = true
    override val hasDesc: Boolean = true
    override val hasLink: Boolean = false

    override val footerHeight: Int
        get() = if (EditorState.MOVE == state) {
            0
        } else {
            if (desc.isNullOrEmpty().not()) {
                descView.marginTop + descView.height + descView.marginBottom
            } else {
                0
            }
        }

    override val actionMarginBottom: Int
        get() = if (EditorState.MOVE == state) {
            25.dp
        } else {
            footerHeight + 25.dp
        }

    override val editBorderMarginRect: Rect
        get() {
            _editMarginRect.bottom = itemMargin - borderMarginOffset + footerHeight
            return _editMarginRect
        }

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
        val oldH = height
//        cardView.layoutParams = normalLayoutParams
        if (desc.isNullOrEmpty().not()) {
            descView.isVisible = true
        }
        post {
            logH("normalState", oldH, height)
        }
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
        val oldH = height
//        cardView.layoutParams = moveLayoutParams
//        descView.isVisible = false
        cardView.apply {
            layout(left, top, right, top + moveStateHeight - mMarginV)
        }
        post {
            logH("  moveState", oldH, height)
        }
    }

    var data: PhotoInfo? = null
        set(value) {
            field = value
            imgView.loadImage(data = value?.uri)
        }

    /**
     * 图片网络url
     */
    var url: CharSequence? = null
        set(value) {
            field = value
            value?.let {
                imgView.loadImage(data = it, useProxy = false)
            }
        }

    /**
     * 本地图片Uri，提升本地加载速度
     */
    var uri: Uri? = null
        set(value) {
            field = value
            value?.let {
//                imgView.loadImage(data = it, useProxy = false)
            }
        }

    var desc: CharSequence? = null
        set(value) {
            field = value
            if (value.isNullOrEmpty().not()) {
                imageElementData.text = value
                descView.isVisible = true
                descView.text = value
            } else {
                descView.isVisible = false
            }
            post {
                parentItemView?.state = EditorState.NORMAL
                logE("desc", "EditorState.NORMAL footerHeight=$footerHeight parentItemView=${parentItemView?.javaClass?.simpleName}")
            }
        }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)

        isFocusable = true

        addView(cardView)
        addView(descView)
    }

    private val normalLayoutParams by lazy {
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(itemMargin, itemMargin, itemMargin, itemMargin)
        }
    }

    private val moveLayoutParams by lazy {
        LayoutParams(LayoutParams.MATCH_PARENT, mMinHeight).apply {
            setMargins(itemMargin, itemMargin, itemMargin, itemMargin)
        }
    }

    private val cardView by lazy {
        CardView(context).apply {
            layoutParams = normalLayoutParams
            minimumHeight = 100.dp
            radius = cornerRadius

            addView(imgView)
        }
    }

    private val imgView by lazy {
        ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setImageResource(R.drawable.default_image)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private val descView by lazy {
        TextView(context).apply {
            layoutParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT).apply {
                setMargins(itemMargin, 0, itemMargin, itemMargin)
            }
            setTextColor(getColor(R.color.color_8798af))
            textSize = 12F
            gravity = Gravity.CENTER
            isVisible = false
        }
    }

    private fun fillData() {
        post {
            logE("fillData", "footerHeight=$footerHeight")
            url = imageElementData.url
            uri = imageElementData.uri
            desc = imageElementData.text
        }
    }
}