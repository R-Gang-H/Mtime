package com.kotlin.android.publish.component.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.publish.component.R
import java.lang.StringBuilder

/**
 * 发布富文本Item视图布局
 *
 * Created on 2020/7/13.
 *
 * @author o.s
 */
class PublishItemView : FrameLayout {

    private val tag = "  3 ->"

    private val padding = 8.dp
    private val deleteViewWidth = 24.dp
    private val deleteViewHeight = deleteViewWidth
    var richEditText: RichEditText? = null
    var imageCard: ImageCard? = null
    var movieCard: MovieCard? = null
    private var deleteView: ImageView? = null
    private var moved: Boolean = false

    /**
     * true: 单一的输入框；
     * false: 多输入框；
     * 1、在多输入框模式下，按下回车键，将试图生成下一个新的段落。
     * 2、光标在文本开头时点击删除，将试图合并到上一段落。
     */
    var single: Boolean = true
        set(value) {
            field = value
            richEditText?.single = value
        }

    var movie: Movie? = null
        set(value) {
            field = value
            movieCard?.movie = value
        }

    val body: String
        get() {
            val sb = StringBuilder()
            when (itemType) {
                ItemType.TEXT -> {
                    sb.append(richEditText?.body)
                }
                ItemType.IMAGE_CARD -> {
                    sb.append(imageCard?.body)
                }
                ItemType.MOVIE_CARD -> {
                    sb.append(movieCard?.body)
                }
            }
            return sb.toString()
        }

    /**
     * body状态
     */
    var bodyState: ((isEmpty: Boolean) -> Unit)? = null
        set(value) {
            field = value
            richEditText?.bodyState = value
        }

    /**
     * 文本段落body是否为空
     */
    val isBodyEmpty: Boolean
        get() {
            if (itemType == ItemType.TEXT) {
                return richEditText?.isBodyEmpty ?: true // (richEditText?.text?.length ?: 0) <= 0
            }
            return true
        }

    /**
     * 段落触发事件，上一段落，下一段落
     */
    var paragraphEvent: ((event: RichEditText.ParagraphEvent) -> Unit)? = null
        set(value) {
            field = value
            richEditText?.paragraphEvent = value
        }

    /**
     * 富文本视图状态
     */
    var state: State = State.NORMAL
        set(value) {
//            if (field != value) {
//            }
            field = value
            changeState()
        }

    /**
     * 事件处理
     */
    var action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)? = null
        set(value) {
            field = value
            children.forEach {
                (it as? RichEditText)?.action = action
                (it as? MovieCard)?.setAction(this, action)
                (it as? ImageCard)?.setAction(this, action)
            }
        }

    /**
     * 拖拽改变监听
     */
    var dragChange: ((v: View, dy: Float) -> Unit)? = null
        set(value) {
            field = value
            children.forEach {
                (it as? MovieCard)?.dragChange = value
                (it as? ImageCard)?.dragChange = value
            }
        }

    /**
     * 视图类型
     */
    var itemType: ItemType? = null
        set(value) {
            if (field != value) {
                field = value
                initViewWithType()
            }
        }

    /**
     * 移动状态的高度，根据视图类型及实际情况设定。
     */
    val moveStateHeight: Int
        get() {
            if(ItemType.IMAGE_CARD == itemType) {
                return imageCard?.run {
                    val limitHeight = moveStateHeight
                    if (limitHeight > 0) {
//                        "$tag image :: (l, t, r, b) -> ($left, $top, $right, ${top + moveStateHeight}) :: bottom = $bottom".w()
//                        layout(left, top, right, top + moveStateHeight - 100)
                        limitHeight + marginTop + marginBottom
                    } else {
                        0
                    }
                } ?: 0
            } else if (ItemType.TEXT == itemType) {
                return richEditText?.run {
                    val limitHeight = moveStateHeight
                    if (limitHeight > 0) {
//                        "$tag  text :: (l, t, r, b) -> ($left, $top, $right, ${top + moveStateHeight}) :: bottom = $bottom".w()
//                        layout(left, top, right, top + moveStateHeight - 100)
                        limitHeight + marginTop + marginBottom
                    } else {
                        0
                    }
                } ?: 0
            }
            return 0
        }

    fun setImageDate(data: PhotoInfo) {
        this.imageCard?.setDate(data)
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

    private fun initView() {
        val params = MarginLayoutParams(
                MarginLayoutParams.MATCH_PARENT,
                MarginLayoutParams.WRAP_CONTENT).apply {
            marginStart = 10.dp
            marginEnd = 10.dp
//            topMargin = 3.dp
//            bottomMargin = 3.dp
        }
        layoutParams = params
        setPadding(0, 0, 0, padding)

        isFocusable = true
        isFocusableInTouchMode = true
        initViewWithType()
    }

    private fun initViewWithType() {
        removeAllViews()
        when (itemType) {
            ItemType.TEXT -> {
                richEditText = RichEditText(context)
                addView(richEditText)
            }
            ItemType.IMAGE_CARD -> {
                imageCard = ImageCard(context)
                imageCard?.setAction(this, action)
                addView(imageCard)
                addView(getDeleteView())
            }
            ItemType.MOVIE_CARD -> {
                movieCard = MovieCard(context)
                movieCard?.setAction(this, action)
                addView(movieCard)
                addView(getDeleteView())
            }
        }
    }

    private fun changeState() {
        when (state) {
            State.EDIT -> {
                editState()
            }
            State.MOVE -> {
                moveState()
            }
            State.NORMAL -> {
                normalState()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        "$tag dispatchTouchEvent".i()
        return super.dispatchTouchEvent(ev)
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        "$tag requestDisallowInterceptTouchEvent".i()
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        "$tag onInterceptTouchEvent".i()
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        super.onTouchEvent(ev)
        ev?.action?.let {
            when (it) {
                MotionEvent.ACTION_DOWN -> {
                    "$tag onTouchEvent ACTION_DOWN".e()
                }
                MotionEvent.ACTION_UP -> {
                    "$tag onTouchEvent ACTION_UP".e()
                    moved = false
                    handleFocus()
                }
                MotionEvent.ACTION_CANCEL -> {
                    "$tag onTouchEvent ACTION_CANCEL".e()
                    moved = false
                }
                MotionEvent.ACTION_MOVE -> {
                    "$tag onTouchEvent ACTION_MOVE".i()
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

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        "$tag onFocusChanged".w()
        state = if (gainFocus) {
            State.EDIT
        } else {
            State.NORMAL
        }
    }

    private fun editState() {
        "$tag editState".i()
        background = null
        deleteView?.visibility = View.VISIBLE
        movieCard?.state = State.EDIT
        imageCard?.state = State.EDIT
        richEditText?.state = State.EDIT
        "$tag editState".w()
    }

    private fun moveState() {
        "$tag moveState :: measuredHeight1 = $measuredHeight".i()
//        setBackground(strokeColorRes = R.color.color_afd956,
//                cornerRadius = 4,
//                strokeWidth = 10)
        setBackgroundResource(R.drawable.layer_list_publish_move_state_bg)
        deleteView?.visibility = View.GONE
        movieCard?.state = State.MOVE
        imageCard?.state = State.MOVE
        richEditText?.state = State.MOVE
        "$tag moveState :: measuredHeight2 = $measuredHeight".w()

//        move()
    }

    private fun normalState() {
        "$tag normalState :: measuredHeight1 = $measuredHeight".i()
        translationZ = 0F
        background = null
        deleteView?.visibility = View.GONE
        movieCard?.state = State.NORMAL
        imageCard?.state = State.NORMAL
        richEditText?.state = State.NORMAL
        "$tag normalState :: measuredHeight2 = $measuredHeight".w()
    }

    private fun getDeleteView(): View? {
        deleteView = ImageView(context).apply {
            val params = LayoutParams(deleteViewWidth, deleteViewHeight)
            params.gravity = Gravity.END
            layoutParams = params
            visibility = View.GONE
            setImageResource(R.drawable.icon_publish_content_del)
            setOnClickListener {
                "$tag click close".e()
                action?.invoke(this@PublishItemView, ActionEvent.EVENT_CLOSE)
            }
        }
        return deleteView
    }

    /**
     * 高亮itemView
     */
    fun highlightView() {
        "$tag highlightView".e()
        translationZ = 10F
        setBackground(
            strokeColorRes = android.R.color.transparent,
            colorRes = R.color.color_80_666c7b,
            cornerRadius = 4.dpF,
            strokeWidth = 7.dp
        )
    }

}