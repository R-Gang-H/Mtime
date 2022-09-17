package com.kotlin.android.publish.component.widget

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.text.getSpans
import androidx.core.view.marginTop
import androidx.core.widget.doAfterTextChanged
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getTextHeight
import com.kotlin.android.ktx.ext.core.getTextHeightWithLines
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.keyboard.showSoftInput
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.ktx.ext.span.LINK_IMAGE_PLACEHOLDER
import com.kotlin.android.ktx.ext.span.toCenterImageSpan
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.span.toUrl
import com.kotlin.android.publish.component.R
import java.lang.StringBuilder

/**
 * 可编辑富文本视图
 *
 * Created on 2020/7/10.
 *
 * @author o.s
 */
class RichEditText : AppCompatEditText {

    private val tag = "    5 ->"
    private val margin = 8.dp
    private val maxLineLimit = 100
    private val maxLength = 10000
    private val moveStateLineLimit = 3
    private var mTextSize = 16F
    private val links = ArrayList<Link>()

    var action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)? = null // 事件处理
    var paragraphEvent: ((event: ParagraphEvent) -> Unit)? = null // 段落触发事件，上一段落，下一段落
    var bodyState: ((isEmpty: Boolean) -> Unit)? = null // body状态

    /**
     * true: 单一的输入框；
     * false: 多输入框；
     * 1、在多输入框模式下，按下回车键，将试图生成下一个新的段落。
     * 2、光标在文本开头时点击删除，将试图合并到上一段落。
     */
    var single: Boolean = true
        set(value) {
            field = value
            setImeOptions(value)
        }

    val body: String
        get() {
            val rich = richText
            return if (!TextUtils.isEmpty(rich)) {
                """<p><span>$rich</span></p>"""
            } else {
                ""
            }
        }

    private val richText: String
        get() {
            val sb = StringBuilder()
            return text?.run {
                val content = toString()
                val spans = getSpans<URLSpan>(0, length)
                var index = 0
                spans.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)

                    sb.append(content.substring(index, start).replaceSpecial())

                    val link = content.substring(start, end).replaceSpecial()
                    val a = """<a href="${it.url}">$link</a>"""
                    sb.append(a)

                    index = end
                }
                sb.append(content.substring(index).replaceSpecial())
                sb.toString().replace(LINK_IMAGE_PLACEHOLDER, "")
            } ?: ""
        }

    var isBodyEmpty: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                bodyState?.invoke(value)
            }
        }

    /**
     * 富文本视图状态
     */
    var state: State = State.NORMAL
        set(value) {
//            if (field == value) {
//                return
//            }
            field = value
            refreshUI()
        }

    val moveStateHeight: Int
        get() {
            // 三行文字限制
            val tempHeight = getTextHeightWithLines(moveStateLineLimit)
            val count = tempHeight / lineHeight
            val dy = measuredHeight % lineHeight
            val limitHeight = lineHeight * count + dy

            "$tag  text moveStateHeight :: measuredHeight = $measuredHeight :: limitHeight = $limitHeight :: lineHeight() = ${getTextHeight()} :: lineHeight = $lineHeight :: letterSpacing = $letterSpacing :: lineSpacingExtra = $lineSpacingExtra :: paddingTop = $paddingTop :: paddingBottom = $paddingBottom :: marginTop = $marginTop".w()
            if (measuredHeight > limitHeight) {
                return limitHeight
            }
            return 0 // heightOutSide
        }

    /**
     * 添加影片超链接
     */
    fun addLink(movie: Movie) {
        val link = Link().apply {
            this.movie = movie
        }
        links.add(link)
        movie.name?.apply {
            addLinkText(this, movie.href ?: "")
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
        val params = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT).apply {
            setMargins(margin, margin, margin, 0)
        }
        layoutParams = params

        isFocusable = true
        isFocusableInTouchMode = true

        setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
        setLineSpacing(10.dpF, 1F)
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
        setTextColor(getColor(R.color.color_4e5e73))
        setHintTextColor(getColor(R.color.color_cbd0d7))
        background = null
        movementMethod = LinkMovementMethod.getInstance()

        initParagraphEvent()

        doAfterTextChanged {
            isBodyEmpty = TextUtils.isEmpty(it)
        }
    }

    /**
     * 初始化段落设置
     */
    private fun initParagraphEvent() {
        setImeOptions(single)
        setOnEditorActionListener { _, _, event ->
            when (event.keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    if (!single) {
                        paragraphEvent?.invoke(ParagraphEvent.NEXT)
                        return@setOnEditorActionListener true
                    }
                }
                else -> {}
            }
            return@setOnEditorActionListener false
        }
        setOnKeyListener { _, _, event ->
            val action = event.action
            when (event.keyCode) {
                KeyEvent.KEYCODE_DEL -> {
                    if (!single) {
                        when (action) {
                            KeyEvent.ACTION_DOWN -> {
                                if (selectionStart == 0 && selectionEnd == 0) {
                                    paragraphEvent?.invoke(ParagraphEvent.PREVIOUS)
                                    return@setOnKeyListener true
                                }
                            }
                        }

                    }
                }
                else -> {}
            }
            return@setOnKeyListener false
        }
    }

    /**
     * 设置IME：Enter键图标
     */
    private fun setImeOptions(isDone: Boolean) {
        imeOptions = if (isDone) {
            EditorInfo.IME_ACTION_DONE
        } else {
            EditorInfo.IME_ACTION_UNSPECIFIED
        }
    }

    private fun refreshUI() {
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
        "$tag editState".e()
        showSoftInput(0, object : ResultReceiver(Handler {
            val obj = it.obj
            val what = it.what
            "${this@RichEditText.tag} Handler :: Message = $what :: obj = $obj".w()
            true
        }) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                super.onReceiveResult(resultCode, resultData)
                "${this@RichEditText.tag} onReceiveResult :: resultCode = $resultCode :: resultData = $resultData".i()
            }
        })
    }

    private fun moveState() {
        "$tag moveState".e()
        foreground = null
        // 移动时最大行数限制
//        if (lineCount > moveStateLineLimit) {
//            maxLines = moveStateLineLimit
//        }
    }

    private fun normalState() {
        "$tag normalState".e()
        foreground = null
        // 文本输入最大行数限制
        maxLines = maxLineLimit
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) {
            state = State.NORMAL
        }
    }

    private fun addLinkText(content: String, url: String) {
        val imageSpan = getDrawable(R.drawable.ic_publish_link).toCenterImageSpan()
        val urlSpan = content.toSpan()
//                .toColor(color = getColor(R.color.color_20a0da))
                .toUrl(url)
        text = imageSpan
                .append(urlSpan)
                .append(" ")
    }

    /**
     * 替换特殊字符
     */
    private fun String.replaceSpecial(): String {
        return this.replace("<", "&lt;").replace(">", "&gt;")
    }

    class Link {
        var start: Int = 0
        var end: Int = 0
        var text: String = ""
        var movie: Movie = Movie()
    }

    enum class ParagraphEvent {
        PREVIOUS,
        NEXT,
        DONE
    }
}