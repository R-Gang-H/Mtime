package com.kotlin.android.publish.component.widget.article.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.publish.component.*
import com.kotlin.android.publish.component.logE
import com.kotlin.android.publish.component.logF
import com.kotlin.android.publish.component.logH
import com.kotlin.android.publish.component.widget.ActionEvent
import com.kotlin.android.publish.component.widget.article.view.entity.*
import com.kotlin.android.publish.component.widget.article.view.event.PEvent
import com.kotlin.android.publish.component.widget.article.view.item.*
import com.kotlin.android.publish.component.widget.article.xml.entity.Element

/**
 * 文章Item布局
 *
 * Created on 2022/3/29.
 *
 * @author o.s
 */
class EditorItemLayout : FrameLayout, IItemView, ITextChanged {

    private val actionViewWidth = 24.dp
    private val actionViewHeight = actionViewWidth
    private val actionMarginOffset = 15.dp
    private val defActionMarginBottom = 20.dp
    private val defActionMarginEnd = 25.dp
    private val defActionSecondMarginEnd = actionMarginOffset + actionViewHeight + defActionMarginEnd
    private val descTextSize = 12F

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

    private var itemView: IItemView? = null
    private var moved: Boolean = false

    val isEmptyTextCard: Boolean
        get() = (ItemType.TEXT_CARD == type) && isEmpty

    val movieCard: MovieCard?
        get() = itemView as? MovieCard

    val imageCard: ImageCard?
        get() = itemView as? ImageCard

    val videoCard: VideoCard?
        get() = itemView as? VideoCard

    val textCard: TextCard?
        get() = itemView as? TextCard

    val textElementData: TextElementData?
        get() = elementData as? TextElementData

    val movieElementData: MovieElementData?
        get() = elementData as? MovieElementData

    val imageElementData: ImageElementData?
        get() = elementData as? ImageElementData

    val videoElementData: VideoElementData?
        get() = elementData as? VideoElementData

    /**
     * 事件处理
     */
    var action: ((v: EditorItemLayout, actionEvent: ActionEvent) -> Unit)? = null
        set(value) {
            field = value
            moveView.setAction(itemView = this, action = value)
        }

    var dragChange: ((v: EditorItemLayout, dy: Float) -> Unit)? = null
        set(value) {
            field = value
            moveView.dragChange = value
        }

    var type: ItemType = ItemType.TEXT_CARD
        get() = element.itemType ?: field
        set(value) {
            field = value
            // 根据类型初始化对应类型视图，并获取对应类型的默认 Element 对象
            itemView = when (value) {
                ItemType.IMAGE_CARD -> {
                    ImageCard(context)
                }
                ItemType.MOVIE_CARD -> {
                    MovieCard(context)
                }
                ItemType.TEXT_CARD -> {
                    TextCard.create(
                        context = context,
                        pEvent = pEvent,
                        notifyTextChanged = notifyTextChanged,
                        focusChanged = focusChanged,
                    )
                }
                ItemType.VIDEO_CARD -> {
                    VideoCard(context)
                }
            }
            itemView?.element?.apply {
                element = this
            }
//            element = Element.obtain(value)
        }

    /**
     * 第一个文本hint
     */
    var hint: CharSequence = ""
        set(value) {
            field = value
            (itemView as? TextCard)?.hint = value
        }

    /**
     * 图片描述
     */
    var desc: CharSequence = ""
        get() = (itemView as? ImageCard)?.desc ?: ""
        set(value) {
            field = value
            (itemView as? ImageCard)?.desc = value
        }

    /**
     * 段落事件
     */
    var pEvent: ((event: PEvent) -> Unit)? = null
        set(value) {
            field = value
            (itemView as? TextCard)?.pEvent = value
        }

    /**
     * 通知全部item的状态
     */
    fun notifyStateAll(state: EditorState) {
        (parent as? EditorLayout)?.state = state
    }

    override val view: View
        get() = this

    override var element: Element = Element()
        set(value) {
            field = value
            value.view = this
            value.apply {
                removeAllViews()
                when (tag) {
                    "figure" -> {
                        when (clazz) {
                            "image" -> {
                                // 图片
                                if (itemView !is ImageCard) {
                                    itemView = ImageCard(context)
                                }
                            }
                            "movieCard" -> {
                                // 电影
                                if (itemView !is MovieCard) {
                                    itemView = MovieCard(context)
                                }
                            }
                        }
                    }
                    "p" -> {
                        if (items?.find { it.tag == "video" } != null) {
                            // 视频
                            if (itemView !is VideoCard) {
                                itemView = VideoCard(context)
                            }
                        } else {
                            // 文本
                            if (itemView !is TextCard) {
                                itemView = TextCard.create(
                                    context = context,
                                    pEvent = pEvent,
                                    notifyTextChanged = notifyTextChanged,
                                    focusChanged = focusChanged,
                                )
                            }
                        }
                    }
                }

                itemView?.let {
                    it.element = value
                    addView(it.view)
                    if (it.hasDelete) {
                        addView(editBorderView)
                        addView(deleteView)
                    }
                    if (it.hasMove) {
                        addView(moveView)
                    }
                    if (it.hasDesc) {
                        addView(descView)
                    }
                    if (it.hasLink) {
                        addView(linkView)
                    }
                }
//                state = EditorState.NORMAL
            }
        }

    override val elementData: IElementData
        get() = itemView?.elementData ?: TextElementData()

    override val count: Int
        get() = itemView?.count ?: 0

    override val isReady: Boolean
        get() = itemView?.isReady ?: true

    override val isError: Boolean
        get() = itemView?.isError ?: false

    override val isEmpty: Boolean
        get() = itemView?.isEmpty ?: true

    override var notifyTextChanged: (() -> Unit)? = null
        set(value) {
            field = value
            (itemView as? TextCard)?.notifyTextChanged = value
        }

    private fun initView() {
        layoutParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT).apply {
            marginStart = 5.dp
            marginEnd = 5.dp
        }

        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        super.onTouchEvent(ev)
        ev?.action?.let {
            when (it) {
                MotionEvent.ACTION_DOWN -> {
                    logE("onTouchEvent", "ACTION_DOWN")
                }
                MotionEvent.ACTION_UP -> {
                    logE("onTouchEvent", "ACTION_UP")
                    moved = false
                    handleFocus()
                }
                MotionEvent.ACTION_CANCEL -> {
                    logE("onTouchEvent", "ACTION_CANCEL")
                    moved = false
                }
                MotionEvent.ACTION_MOVE -> {
                    logD("onTouchEvent", "ACTION_MOVE")
                    moved = true
                }
            }
        }
        return true
    }

    private fun handleFocus() {
        if (moved) {
            return
        }
        if (isFocused) {
            clearFocus()
        } else {
            requestFocus()
        }
    }

    override var focusChanged: ((View, Boolean) -> Unit)? = null
        set(value) {
            field = value
            itemView?.focusChanged = value
        }

    override var hasFocused: ((View, Boolean) -> Unit)? = null
        set(value) {
            field = value
            itemView?.hasFocused = value
        }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        logF(gainFocus)
        focusChanged?.invoke(this, gainFocus)
        state = if (gainFocus) {
            EditorState.EDIT
        } else {
            EditorState.NORMAL
        }
    }

    override var state: EditorState = EditorState.NORMAL
        set(value) {
            field = value
            itemView?.state = value
            dispatchState(value)
        }

    /**
     * 正常状态
     */
    override fun normalState() {
        val oldH = height
        translationZ = 0F
        background = null
        itemView?.apply {
            if (hasDelete) {
                editBorderView.isVisible = false
                deleteView.isVisible = false

                editBorderView.setMargin(editBorderMarginRect)
            }
            if (hasMove) {
                moveView.isVisible = true

                moveView.marginBottom = actionMarginBottom
            }
            if (hasDesc) {
                descView.isVisible = true

                descView.marginBottom = actionMarginBottom
            }
            if (hasLink) {
                linkView.isVisible = true

                linkView.marginBottom = actionMarginBottom
            }
        }
        post {
            logH("normalState", oldH, height, isD = true)
        }
    }

    /**
     * 编辑状态 修改/删除
     */
    override fun editState() {
        val oldH = height
        background = null
        itemView?.apply {
            if (hasDelete) {
                editBorderView.isVisible = true
                deleteView.isVisible = true

                editBorderView.setMargin(editBorderMarginRect)
            }
            if (hasMove) {
                moveView.isVisible = false

                moveView.marginBottom = actionMarginBottom
            }
            if (hasDesc) {
                descView.isVisible = false

                descView.marginBottom = actionMarginBottom
            }
            if (hasLink) {
                linkView.isVisible = false

                linkView.marginBottom = actionMarginBottom
            }
        }
        logH("  editState", oldH, height, isD = true)
    }

    /**
     * 移动状态
     */
    override fun moveState() {
        val oldH = height
        post {
            setBackgroundResource(R.drawable.layer_list_publish_move_state_bg)
            itemView?.apply {
                if (hasDelete) {
                    editBorderView.isVisible = false
                    deleteView.isVisible = false

                    editBorderView.setMargin(editBorderMarginRect)
                }
                if (hasMove) {
                    moveView.isVisible = true

                    moveView.marginBottom = actionMarginBottom
                }
                if (hasDesc) {
                    descView.isVisible = false

                    descView.marginBottom = actionMarginBottom
                }
                if (hasLink) {
                    linkView.isVisible = false

                    linkView.marginBottom = actionMarginBottom
                }
            }
            logH("  moveState", oldH, height, isD = true)
        }
    }

    override val moveStateHeight: Int
        get() = itemView?.moveStateHeight ?: 74.dp

    private fun syncActionView() {
        itemView?.apply {
            when (this) {
                is ImageCard -> {
                    desc
                }
                is VideoCard -> {

                }
            }
        }
    }

    private val deleteView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(actionViewWidth, actionViewHeight).apply {
                gravity = Gravity.END
            }
            visibility = View.GONE
            setImageResource(R.drawable.icon_publish_content_del)
            setOnClickListener {
                action?.invoke(this@EditorItemLayout, ActionEvent.EVENT_CLOSE)
            }
        }
    }

    private val editBorderView by lazy {
        View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            visibility = View.GONE
            setForeground(
                strokeColorRes = R.color.color_20a0da,
                strokeWidth = 2 * borderMarginOffset,
                cornerRadius = cornerRadius
            )
        }
    }

    private val moveView by lazy {
        ActionMoveView(context).apply {
            layoutParams = LayoutParams(actionViewWidth, actionViewHeight).apply {
                setMargins(0, 0, defActionMarginEnd, defActionMarginBottom)
                gravity = Gravity.BOTTOM or Gravity.END
            }
            visibility = View.GONE
            setImageResource(R.drawable.icon_publish_content_move)

//            setOnLongClickListener {
//                action?.invoke(this@EditorItemLayout, ActionEvent.EVENT_MOVE)
//                highlightView()
//                return@setOnLongClickListener true
//            }
        }
    }

    private val descView by lazy {
        AppCompatTextView(context).apply {
            layoutParams = LayoutParams(90.dp, actionViewHeight).apply {
                setMargins(0, 0, defActionSecondMarginEnd, defActionMarginBottom)
                gravity = Gravity.BOTTOM or Gravity.END
            }
            visibility = View.GONE
            setBackgroundResource(R.drawable.icon_publish_img_des)
            setPadding(30.dp, 0, 0, 0)
            gravity = Gravity.CENTER_VERTICAL
            textSize = descTextSize
            setTextColor(getColor(R.color.color_ffffff))
            setText(R.string.publish_img_desc)
            setOnClickListener {
                action?.invoke(this@EditorItemLayout, ActionEvent.EVENT_DESC)
            }
        }
    }

    private val linkView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(actionViewWidth, actionViewHeight).apply {
                setMargins(0, 0, defActionSecondMarginEnd, defActionMarginBottom)
                gravity = Gravity.BOTTOM or Gravity.END
            }
            visibility = View.GONE
            setImageResource(R.drawable.icon_publish_content_movie_link)
            setOnClickListener {
                action?.invoke(this@EditorItemLayout, ActionEvent.EVENT_LINK)
            }
        }
    }

    /**
     * 高亮itemView
     */
    fun highlightView() {
        logE("highlightView", "高亮")
        translationZ = 10F
        setBackground(
            strokeColorRes = android.R.color.transparent,
            colorRes = R.color.color_80_666c7b,
            cornerRadius = cornerRadius,
            strokeWidth = 7.dp
        )
    }
}