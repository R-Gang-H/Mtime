package com.kotlin.android.publish.component.widget

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.children
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.removeFromParent
import com.kotlin.android.ktx.ext.core.setForeground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import com.kotlin.android.publish.component.widget.dialog.showDescDialog
import kotlinx.android.synthetic.main.view_publish_image_card.view.*

/**
 * 图片卡片视图：
 *
 * Created on 2020/7/10.
 *
 * @author o.s
 */
class ImageCard : RelativeLayout {

    private val tag = "   4 ->"

    private val margin = 8.dp
    private val mMinHeight = 80.dp
    private val views = ArrayList<View>()
    private var data: PhotoInfo? = null
    private var itemView: PublishItemView? = null

//    private val textElement by lazy {
//        Element()
//    }
//
//    private val pElement by lazy {
//        Element(
//            tag = "p",
//            items = arrayListOf(textElement)
//        )
//    }
//
//    private val imgElement by lazy {
//        Element(
//            tag = "img",
//        )
//    }
//
//    private val figCaptionElement by lazy {
//        Element(
//            tag = "figcaption",
//            items = arrayListOf(pElement)
//        )
//    }
//
//    val element by lazy {
//        Element(
//            tag = "figure",
//            clazz = "image",
//            items = ArrayList<Element>()
//                .apply {
//                       add(imgElement)
//                       add(figCaptionElement)
//            },
//        )
//    }
//
//    private fun syncElement() {
//        data?.apply {
//            imgElement.src = url
//            imgElement.fileId = fileID
//            imgElement.imageFormat = imageFormat
//            figCaptionElement.style = "width: 100%;"
//            pElement.editable = "false"
//            pElement.style = TextColor.GRAY.color
//            textElement.text = imageDesc
//        }
//    }

    val body: String
        get() {
            return data?.run {
                """
                <figure class="image">
                    <img  src="$url" data-mt-fileId="$fileID" data-mt-format="$imageFormat" />
                    <figcaption style="width: 100%;">
                        <p contenteditable="false" style="color: #8798AF !important" title="">$imageDesc</p>
                    </figcaption>
                </figure>
            """.trimIndent()
            } ?: ""
        }

//    val body1: String
//        get() {
//            return element.body
//        }

    private val imageDesc: String
        get() {
            return descView?.text.toString()
        }

    /**
     * 富文本视图状态
     */
    var state: State = State.NORMAL
        set(value) {
            if (field == value) {
                return
            }
            field = value
            changeState()
        }

    /**
     * 事件处理
     */
    private var action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)? = null
        set(value) {
            field = value
            children.forEach {
                (it as? ActionMoveView)?.setAction(itemView, action)
                (it as? ActionDescView)?.setAction(itemView) { _, _ ->
                    showDescDialog()
                }

            }
        }

    val moveStateHeight: Int
        get() {
            "$tag moveStateHeight :: measuredHeight = $measuredHeight :: mMinHeight = $mMinHeight".i()
            if (measuredHeight > mMinHeight) {
                move()
                return mMinHeight
            }
            return 0
        }

    private fun move() {
        "$tag image moveStateHeight :: measuredHeight = $measuredHeight :: mMinHeight = $mMinHeight".i()
        if (measuredHeight > mMinHeight) {
            actionMove?.run {
                val t = (mMinHeight - measuredHeight) / 2
                val b = t + measuredHeight
                "${this@ImageCard.tag} image moveStateHeight :: (t, b) -> ($t,  $b) :: (top, bottom) -> ($top, $bottom)".w()
                layout(left, t, right, b)
            }
        }
    }

    /**
     * 拖拽改变监听
     */
    var dragChange: ((v: View, dy: Float) -> Unit)? = null
        set(value) {
            field = value
            children.forEach {
                val v = it as? ActionMoveView
                v?.dragChange = value
            }
        }

    fun setAction(itemView: PublishItemView?,
                  action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)?) {
        this.itemView = itemView
        this.action = action
    }

    fun setDate(data: PhotoInfo) {
        this.data = data
        "$tag PhotoInfo = $data".e()
        syncImage()
//        syncElement()
    }

    private fun syncImage() {
//        data?.uri?.apply {
//            icon?.loadLocalImage(this, getRequestOptionsWithCropRoundCorners(320.dp, 320.dp))
//        }
        data?.url?.apply {
            icon?.loadImage(data = this, width = 320.dp, height = 320.dp)
        }
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
        val params = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT).apply {
            setMargins(margin, margin, margin, 0)
        }
        layoutParams = params
//        iconLayout?.setBackground(colorRes = R.color.color_f2f3f6, cornerRadius = 4)
        val view = View.inflate(context, R.layout.view_publish_image_card, null) as? RelativeLayout
        view?.run {
            children.forEach {
                views.add(it)
            }
        }
        views.forEach {
            it.removeFromParent()
            addView(it)
        }

        isFocusable = true

        actionMove?.setAction(itemView, action)
        actionDesc?.setAction(itemView) { _, _ ->
            showDescDialog()
        }
    }

    private fun showDescDialog() {
        showDescDialog(imageDesc) {
            descView?.apply {
                if (TextUtils.isEmpty(it)) {
                    gone()
                } else {
                    text = it
                    visible()
                }
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

    private fun editState() {
        "$tag editState".i()
        iconLayout?.setForeground(
            strokeColorRes = R.color.color_20a0da,
            strokeWidth = 2.dp,
            cornerRadius = 4.dpF
        )
        actionDesc?.visibility = View.GONE
        actionMove?.visibility = View.GONE
    }

    private fun moveState() {
        "$tag moveState".i()
        iconLayout?.foreground = null
//        iconLayout?.setBackground(colorRes = R.color.color_f2f3f6,
//                cornerRadius = 4)

        actionDesc?.visibility = View.GONE
        actionMove?.visibility = View.VISIBLE
        move()
    }

    private fun normalState() {
        "$tag normalState".i()
        iconLayout?.foreground = null
//        iconLayout?.setBackground(colorRes = R.color.color_f2f3f6,
//                cornerRadius = 4)

        layoutParams.height = MarginLayoutParams.WRAP_CONTENT
        actionDesc?.visibility = View.VISIBLE
        actionMove?.visibility = View.VISIBLE
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        state = if (gainFocus) {
            State.EDIT
        } else {
            State.NORMAL
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
        ev?.action?.let {
            when (it) {
                MotionEvent.ACTION_DOWN -> {
                    "$tag onTouchEvent ACTION_DOWN".e()
                }
                MotionEvent.ACTION_UP -> {
                    "$tag onTouchEvent ACTION_UP".e()
                }
                MotionEvent.ACTION_CANCEL -> {
                    "$tag onTouchEvent ACTION_CANCEL".e()
                }
                MotionEvent.ACTION_MOVE -> {
                    "$tag onTouchEvent ACTION_MOVE".e()
                }
            }
        }
        return super.onTouchEvent(ev)
    }

}

/**
"""
<!--上传图片模版--->
<figure class="image">
    <img  src="http://img5.mtime.cn/mg/2020/10/29/112051.56034253.jpg" data-mt-fileid="[i5]2020/10/29/112051.56034253" data-mt-format="jpg" />
    <figcaption style="width: 100%;">
        <!-- 图注模板 -->
        <p contenteditable="false" style="color: #8798AF !important" title="少年的你">xxx</p>
    </figcaption>
</figure>
"""
 */