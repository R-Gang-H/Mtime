package com.kotlin.android.publish.component.widget.article.view.item

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.text.InputFilter
import android.text.Spanned
import android.text.style.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.text.getSpans
import androidx.core.view.marginTop
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getTextHeightWithLines
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.keyboard.showSoftInput
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.publish.component.*
import com.kotlin.android.publish.component.logF
import com.kotlin.android.publish.component.logH
import com.kotlin.android.publish.component.logT
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.sytle.TextStyle
import com.kotlin.android.publish.component.widget.article.view.EditorState
import com.kotlin.android.publish.component.widget.article.view.entity.IElementData
import com.kotlin.android.publish.component.widget.article.view.entity.TextElementData
import com.kotlin.android.publish.component.widget.article.view.event.PEvent
import com.kotlin.android.publish.component.widget.article.xml.entity.Element

class TextSpan {
    var urlSpans: List<URLSpan>? = null
    var styleSpans: List<StyleSpan>? = null
    var underlineSpans: List<UnderlineSpan>? = null
    var strikethroughSpans: List<StrikethroughSpan>? = null
    var alignSpans: List<AlignmentSpan>? = null
    var sizeSpans: List<RelativeSizeSpan>? = null
    var foregroundColorSpans: List<ForegroundColorSpan>? = null

    val textStyle: Int
        get() {
            var style = 0
            val spans = styleSpans
            "styleSpans size:${spans?.size}".e()
            spans?.forEach {
                when (it.style) {
                    Typeface.BOLD -> {
                        style = style or TextStyle.BOLD.bit
                    }
                    Typeface.ITALIC -> {
                        style = style or TextStyle.ITALIC.bit
                    }
                    Typeface.BOLD_ITALIC -> {
                        style = style or TextStyle.BOLD.bit
                        style = style or TextStyle.ITALIC.bit
                    }
                }
            }
            val underSpans = underlineSpans
            "underlineSpans size:${underSpans?.size}".e()
            if (!underSpans.isNullOrEmpty()) {
                style = style or TextStyle.UNDERLINE.bit
            }
            val throughSpans = strikethroughSpans
            "strikethroughSpans size:${throughSpans?.size}".e()
            if (!throughSpans.isNullOrEmpty()) {
                style = style or TextStyle.LINE_THROUGH.bit
            }
            return style
        }

    val textAlign: TextAlign?
        get() {
            val spans = alignSpans
            "alignSpans size:${spans?.size}".e()
            return when {
                spans.isNullOrEmpty() -> {
                    TextAlign.JUSTIFY // 默认值
                }
                spans.size == 1 -> {
                    TextAlign.obtain(alignment = spans.first().alignment)
                }
                else -> {
                    null
                }
            }
        }

    val textFontSize: TextFontSize?
        get() {
            val spans = sizeSpans
            "sizeSpans size:${spans?.size}".e()
            return when {
                spans.isNullOrEmpty() -> {
                    TextFontSize.STANDARD // 默认值
                }
                spans.size == 1 -> {
                    TextFontSize.obtain(scale = spans.first().sizeChange)
                }
                spans.size == 2 -> {
                    TextFontSize.obtain(scale = spans[1].sizeChange)
                }
                else -> {
                    null
                }
            }
        }

    val textColor: TextColor?
        get() {
            val spans = foregroundColorSpans
            "foregroundColorSpans size:${spans?.size}".e()
            return when {
                spans.isNullOrEmpty() -> {
                    TextColor.BLACK // 默认值
                }
                spans.size == 1 -> {
                    TextColor.obtain(color = spans.first().foregroundColor)
                }
                spans.size == 2 -> {
                    TextColor.obtain(color = spans[1].foregroundColor)
                }
                else -> {
                    null
                }
            }
        }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("urlSpans:")
        urlSpans?.forEach {
            sb.append(it.spanTypeId)
            sb.append(":")
            sb.append(it.underlying)
            sb.append(":")
            sb.append(it.url)
            sb.append(", ")
        }
        sb.append("\n")
        sb.append("styleSpans:")
        styleSpans?.forEach {
            sb.append(it.spanTypeId)
            sb.append(":")
            sb.append(it.underlying)
            sb.append(":")
            sb.append(it.style)
            sb.append(", ")
        }
        sb.append("\n")
        sb.append("underlineSpans:")
        underlineSpans?.forEach {
            sb.append(it.spanTypeId)
            sb.append(":")
            sb.append(it.underlying)
            sb.append(":")
            sb.append(it.describeContents())
            sb.append(", ")
        }
        sb.append("\n")
        sb.append("strikethroughSpans:")
        strikethroughSpans?.forEach {
            sb.append(it.spanTypeId)
            sb.append(":")
            sb.append(it.underlying)
            sb.append(":")
            sb.append(it.describeContents())
            sb.append(", ")
        }
        sb.append("\n")
        sb.append("alignSpans:")
        alignSpans?.forEach {
            sb.append(it.alignment)
            sb.append(", ")
        }
        sb.append("\n")
        sb.append("sizeSpans:")
        sizeSpans?.forEach {
            sb.append(it.spanTypeId)
            sb.append(":")
            sb.append(it.underlying)
            sb.append(":")
            sb.append(it.sizeChange)
            sb.append(", ")
        }
        sb.append("\n")
        sb.append("foregroundColorSpans:")
        foregroundColorSpans?.forEach {
            sb.append(it.spanTypeId)
            sb.append(":")
            sb.append(it.underlying)
            sb.append(":")
            sb.append(it.foregroundColor)
            sb.append(", ")
        }
        sb.append("\n")
        return sb.toString()
    }
}

/**
 * 文本卡片
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
class TextCard : RelativeLayout, IItemView, ITextChanged {

    companion object {

        fun create(
            context: Context?,
            pEvent: ((event: PEvent) -> Unit)? = null,
            notifyTextChanged: (() -> Unit)? = null,
            focusChanged: ((View, Boolean) -> Unit)? = null,
        ): TextCard {
            return TextCard(context).apply {
                this.pEvent = pEvent
                this.notifyTextChanged = notifyTextChanged
                this.focusChanged = focusChanged
            }
        }
    }

    private val _textSpan by lazy { TextSpan() }

    private val maxLineLimit = 100
    private val maxLength = 10000
    private val moveStateLineLimit = 3
    private var mTextSize = 16F
    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    override val view: View
        get() = this

    override var element: Element
        get() = elementData.element
        set(value) {
            elementData.element = value
            fillText()
        }

    override val elementData: IElementData = TextElementData()

    override val count: Int
        get() = if (isEmpty) {
            0
        } else {
            editText.text.length
        }

    override val isEmpty: Boolean
        get() = editText.text?.trim().isNullOrEmpty()

    override val hasDelete: Boolean = false
    override val hasMove: Boolean = false
    override val hasDesc: Boolean = false
    override val hasLink: Boolean = false

    private val textElementData: TextElementData
        get() = elementData as TextElementData

    override var notifyTextChanged: (() -> Unit)? = null

    val selectionStart: Int
        get() = editText.selectionStart

    val selectionEnd: Int
        get() = editText.selectionEnd

    val isRange: Boolean
        get() = selectionStart != selectionEnd

    val selectionRange: IntRange?
        get() = if (isRange) IntRange(selectionStart, selectionEnd) else null

    fun getTextSpan(): TextSpan {
        editText.apply {
            text?.apply {
                val urlSpan = getSpans<URLSpan>(selectionStart, selectionEnd) // <a>

                // TextStyle
                val styleSpan = getSpans<StyleSpan>(selectionStart, selectionEnd) // <em> <strong> <p> <span>
                val underlineSpan = getSpans<UnderlineSpan>(selectionStart, selectionEnd) // <span>
                val strikethroughSpan = getSpans<StrikethroughSpan>(selectionStart, selectionEnd) // <span>

                // TextAlign
                val alignSpan = getSpans<AlignmentSpan>(0, length) // <p>

                // TextFontSize
                val sizeSpan = getSpans<RelativeSizeSpan>(selectionStart, selectionEnd) // <p> <span>

                // TextColor
                val foregroundColorSpan = getSpans<ForegroundColorSpan>(selectionStart, selectionEnd) // <span>

                _textSpan.urlSpans = urlSpan.asList()
                _textSpan.styleSpans = styleSpan.asList()
                _textSpan.underlineSpans = underlineSpan.asList()
                _textSpan.strikethroughSpans = strikethroughSpan.asList()
                _textSpan.alignSpans = alignSpan.asList()
                _textSpan.sizeSpans = sizeSpan.asList()
                _textSpan.foregroundColorSpans = foregroundColorSpan.asList()
            }
        }

        _textSpan.e()
        return _textSpan
    }

    /**
     * 设置字体样式
     */
    fun setTextStyle(style: Int) {
        editText.apply {
            text?.apply {
                val needBold = style and TextStyle.BOLD.bit == TextStyle.BOLD.bit
                val needItalic = style and TextStyle.ITALIC.bit == TextStyle.ITALIC.bit
                val needUnderline = style and TextStyle.UNDERLINE.bit == TextStyle.UNDERLINE.bit
                val needLineThrough = style and TextStyle.LINE_THROUGH.bit == TextStyle.LINE_THROUGH.bit

                correctSelection()

                val sStart = selectionStart
                val sEnd = selectionEnd

                // 字体
                val styleSpans = getSpans<StyleSpan>(sStart, sEnd)
                if (styleSpans.find { it.style == Typeface.BOLD } == null) {
                    updateSpan(style = TextStyle.BOLD, isNeed = needBold, before = null)
                }
                if (styleSpans.find { it.style == Typeface.ITALIC } == null) {
                    updateSpan(style = TextStyle.ITALIC, isNeed = needItalic, before = null)
                }
                styleSpans.forEach {
                    when (it.style) {
                        Typeface.BOLD -> {
                            updateSpan(style = TextStyle.BOLD, isNeed = needBold, before = it)
                        }
                        Typeface.ITALIC -> {
                            updateSpan(style = TextStyle.ITALIC, isNeed = needItalic, before = it)
                        }
                        Typeface.BOLD_ITALIC -> {
                            "斜体加粗组合!!!!".e()
                        }
                    }
                }

                // 下划线
                val underlineSpans = getSpans<UnderlineSpan>(sStart, sEnd)
                if (underlineSpans.isEmpty()) {
                    updateSpan(style = TextStyle.UNDERLINE, isNeed = needUnderline, before = null)
                } else {
                    underlineSpans.forEach {
                        updateSpan(style = TextStyle.UNDERLINE, isNeed = needUnderline, before = it)
                    }
                }

                // 中划线
                val throughSpans = getSpans<StrikethroughSpan>(sStart, sEnd)
                if (throughSpans.isEmpty()) {
                    updateSpan(style = TextStyle.LINE_THROUGH, isNeed = needLineThrough, before = null)
                } else {
                    throughSpans.forEach {
                        updateSpan(style = TextStyle.LINE_THROUGH, isNeed = needLineThrough, before = it)
                    }
                }
            }
        }
        syncSpan()
    }

    /**
     * 更新字体样式
     */
    private fun updateSpan(style: TextStyle, isNeed: Boolean, before: Any?) {
        editText.apply {
            text?.apply {
                val sStart = selectionStart
                val sEnd = selectionEnd

                if (before != null && isNeed) {
                    // nothing
                } else {
                    if (isNeed) {
                        // 之前没有下划线，现在需要，则添加
                        setSpan(newSpan(style), sStart, sEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    } else if (before != null) {
                        val start = getSpanStart(before)
                        val end = getSpanEnd(before)
                        // 之前有下划线，现在不需要，看情况处理：
                        // 1，如果，下划线在range内，则清除下划线
                        if (sStart <= start && end <= sEnd) {
                            removeSpan(before)
                        }
                        // 2，如果，下划线尾部在range内，则从range开始处截断下划线
                        if (end in sStart..sEnd && start < sStart) {
                            setSpan(before, start, sStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                        // 3，如果，下划线头部在range内，则从range结尾处截断下划线
                        if (start in sStart..sEnd && sEnd < end) {
                            setSpan(before, sEnd, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        }
                        // 4，如果，range在下划线内，则从range开始和结尾处分别截断下划线
                        if (start < sStart && sEnd < end) {
                            setSpan(before, start, sStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                            setSpan(newSpan(style), sEnd, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        }
                    } else {
                        // nothing
                    }
                }
            }
        }
    }

    /**
     * 新建字体样式
     */
    private fun newSpan(style: TextStyle): Any {
        return when (style) {
            TextStyle.UNDERLINE -> UnderlineSpan()
            TextStyle.LINE_THROUGH -> StrikethroughSpan()
            TextStyle.BOLD -> StyleSpan(Typeface.BOLD)
            TextStyle.ITALIC -> StyleSpan(Typeface.ITALIC)
            TextStyle.NONE -> StyleSpan(Typeface.NORMAL)
        }
    }

    /**
     * 设置对其方式
     */
    fun setTextAlign(textAlign: TextAlign) {
        editText.apply {
            text?.apply {
                getSpans<AlignmentSpan>(0, length).forEach {
                    removeSpan(it)
                }
                if (TextAlign.JUSTIFY == textAlign) {
                    justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                    setSpan(AlignmentSpan.Standard(textAlign.alignment), 0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                } else {
                    justificationMode = LineBreaker.JUSTIFICATION_MODE_NONE
                    setSpan(AlignmentSpan.Standard(textAlign.alignment), 0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }
        syncSpan()
    }

    /**
     * 设置字体大小
     */
    fun setTextFontSize(textFontSize: TextFontSize) {
        editText.apply {
            text?.apply {
                correctSelection()

                val sStart = selectionStart
                val sEnd = selectionEnd

                getSpans<RelativeSizeSpan>(sStart, sEnd).forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    if (start < sStart && end > sEnd) {
                        // 之前的在开头的，截取到开头之前；之前的结尾的，截取到结尾之后
                        setSpan(it, start, sStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        setSpan(RelativeSizeSpan(it.sizeChange), sEnd, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    } else if (start < sStart) {
                        // 之前的在开头的截取到开头之前
                        setSpan(it, start, sStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    } else if (end > sEnd) {
                        // 之前的结尾的截取到结尾之后
                        setSpan(it, sEnd, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    } else if (start >= sStart && end <= sEnd) {
                        removeSpan(it)
                    }
                }
                setSpan(RelativeSizeSpan(textFontSize.scale), sStart, sEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        syncSpan()
    }

    /**
     * 设置字体颜色
     */
    fun setTextColor(textColor: TextColor) {
        editText.apply {
            text?.apply {
                correctSelection()

                val sStart = selectionStart
                val sEnd = selectionEnd

                getSpans<ForegroundColorSpan>(sStart, sEnd).forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    if (start < sStart && end > sEnd) {
                        // 之前的在开头的，截取到开头之前；之前的结尾的，截取到结尾之后
                        setSpan(it, start, sStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(it.foregroundColor), sEnd, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    } else if (start < sStart) {
                        // 之前的在开头的，截取到开头之前
                        setSpan(it, start, sStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    } else if (end > sEnd) {
                        // 之前的结尾的，截取到结尾之后
                        setSpan(it, sEnd, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    } else if (start >= sStart && end <= sEnd) {
                        removeSpan(it)
                    }
                }
                setSpan(ForegroundColorSpan(textColor.color), sStart, sEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        syncSpan()
    }

    /**
     * 矫正光标，主要解决超链接作为整体处理的问题
     */
    private fun correctSelection() {
        editText.apply {
            text?.apply {
                getSpans<URLSpan>(selectionStart, selectionEnd).forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    var tempStart = selectionStart
                    var tempEnd = selectionEnd
                    var needChange = false
                    if (selectionStart in (start + 1) until end) {
                        tempStart = start
                        needChange = true
                    }
                    if (selectionEnd in (start + 1) until end) {
                        tempEnd = end
                        needChange = true
                    }
                    if (needChange) {
                        setSelection(tempStart, tempEnd)
                    }
                }
            }
        }
    }

    /**
     * 添加超链接
     */
    fun addLink(title: CharSequence, url: CharSequence) {
        editText.apply {
            text?.apply {
                val sStart = selectionStart
                val sEnd = selectionEnd
                val len = title.length
                val end = sStart + len
                replace(sStart, sEnd, title, 0, len)
                setSpan(URLSpan(url.toString()), sStart, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            syncSpan()
        }
    }

    /**
     * 当前段落删除后，光标后面的部分，合并到上一段落
     */
    fun mergeToPre(textCard: TextCard?) {
        textCard?.also {
            val position = editText.text.length
            val len = it.editText.text.length
            if (len > 0) {
                pre(next = it.editText)
//                editText.text.append(it.editText.text)
            }
            setSelection(position)
        }
    }

    /**
     * 前一段落光标之后的部分，合并到新段落
     */
    fun mergeToNext(textCard: TextCard?) {
        textCard?.also {
            val start = it.editText.selectionStart
            val len = it.editText.text.length
            if (start < len) {
                // 需要处理段落移动问题
                next(pre = it.editText)
            }
            setSelection(0)
        }
    }

    /**
     * 合并到上一段落，
     */
    private fun pre(next: EditText) {
        val position = editText.text.length
        val nextEdit = next.text
        val start = 0
        val end = nextEdit.length

        // 1，抑制段落末尾的样式，防止延伸到合并上来的next文本部分。
        restrainSpans(position = position)

        // 2，用next段落填充pre段落末尾文本内容
        editText.text.append(next.text.toString())
        val edit = editText.text

        // 3，开始执行样式操作
        nextEdit.getSpans<URLSpan>(start, end).forEach {
            val s = nextEdit.getSpanStart(it)
            val e = nextEdit.getSpanEnd(it)
            if (s < e) {
                var isSet = false
                edit.getSpans<URLSpan>(position, position).forEach { span ->
                    val ss = edit.getSpanStart(span)
                    val ee = edit.getSpanEnd(span)
                    if (s == 0 && span.url == it.url) {
                        edit.setSpan(span, ss, ee + e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        isSet = true
                    }
                }
                if (!isSet) {
                    edit.setSpan(it, position + s, position + e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        } // <a>

        // TextStyle
        nextEdit.getSpans<StyleSpan>(start, end).forEach {
            val s = nextEdit.getSpanStart(it)
            val e = nextEdit.getSpanEnd(it)
            if (s < e) {
                var isSet = false
                edit.getSpans<StyleSpan>(position, position).forEach { span ->
                    val ss = edit.getSpanStart(span)
                    val ee = edit.getSpanEnd(span)
                    if (s == 0 && span.style == it.style) {
                        val flags = if (e == end) {
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        } else {
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        }
                        edit.setSpan(span, ss, ee + e, flags)
                        isSet = true
                    }
                }
                if (!isSet) {
                    edit.setSpan(it, position + s, position + e, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
        } // <em> <strong> <p> <span>

        nextEdit.getSpans<UnderlineSpan>(start, end).forEach {
            val s = nextEdit.getSpanStart(it)
            val e = nextEdit.getSpanEnd(it)
            if (s < e) {
                var isSet = false
                edit.getSpans<UnderlineSpan>(position, position).forEach { span ->
                    val ss = edit.getSpanStart(span)
                    val ee = edit.getSpanEnd(span)
                    if (s == 0) {
                        val flags = if (e == end) {
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        } else {
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        }
                        edit.setSpan(span, ss, ee + e, flags)
                        isSet = true
                    }
                }
                if (!isSet) {
                    edit.setSpan(it, position + s, position + e, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
        } // <span>

        nextEdit.getSpans<StrikethroughSpan>(start, end).forEach {
            val s = nextEdit.getSpanStart(it)
            val e = nextEdit.getSpanEnd(it)
            if (s < e) {
                var isSet = false
                edit.getSpans<StrikethroughSpan>(position, position).forEach { span ->
                    val ss = edit.getSpanStart(span)
                    val ee = edit.getSpanEnd(span)
                    if (s == 0) {
                        val flags = if (e == end) {
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        } else {
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        }
                        edit.setSpan(span, ss, ee + e, flags)
                        isSet = true
                    }
                }
                if (!isSet) {
                    edit.setSpan(it, position + s, position + e, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
        } // <span>

        // TextFontSize
        nextEdit.getSpans<RelativeSizeSpan>(start, end).forEach {
            val s = nextEdit.getSpanStart(it)
            val e = nextEdit.getSpanEnd(it)
            if (s < e) {
                var isSet = false
                edit.getSpans<RelativeSizeSpan>(position, position).forEach { span ->
                    val ss = edit.getSpanStart(span)
                    val ee = edit.getSpanEnd(span)
                    if (s == 0 && span.sizeChange == it.sizeChange) {
                        val flags = if (e == end) {
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        } else {
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        }
                        edit.setSpan(span, ss, ee + e, flags)
                        isSet = true
                    }
                }
                if (!isSet) {
                    edit.setSpan(it, position + s, position + e, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
        } // <p> <span>

        // TextColor
        nextEdit.getSpans<ForegroundColorSpan>(start, end).forEach {
            val s = nextEdit.getSpanStart(it)
            val e = nextEdit.getSpanEnd(it)
            if (s < e) {
                var isSet = false
                edit.getSpans<ForegroundColorSpan>(position, position).forEach { span ->
                    val ss = edit.getSpanStart(span)
                    val ee = edit.getSpanEnd(span)
                    if (s == 0 && span.foregroundColor == it.foregroundColor) {
                        val flags = if (e == end) {
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        } else {
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        }
                        edit.setSpan(span, ss, ee + e, flags)
                        isSet = true
                    }
                }
                if (!isSet) {
                    edit.setSpan(it, position + s, position + e, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
        } // <span>

        // 3，最后，清空next段落文本内容
        next.setText("")
    }

    /**
     * 抑制span蔓延
     */
    private fun restrainSpans(position: Int) {
        editText.text?.apply {
            getSpans<URLSpan>(position, position).forEach {
                val s = getSpanStart(it)
                val e = getSpanEnd(it)
                setSpan(it, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            getSpans<StyleSpan>(position, position).forEach {
                val s = getSpanStart(it)
                val e = getSpanEnd(it)
                setSpan(it, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            getSpans<UnderlineSpan>(position, position).forEach {
                val s = getSpanStart(it)
                val e = getSpanEnd(it)
                setSpan(it, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            getSpans<StrikethroughSpan>(position, position).forEach {
                val s = getSpanStart(it)
                val e = getSpanEnd(it)
                setSpan(it, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            getSpans<RelativeSizeSpan>(position, position).forEach {
                val s = getSpanStart(it)
                val e = getSpanEnd(it)
                setSpan(it, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            getSpans<ForegroundColorSpan>(position, position).forEach {
                val s = getSpanStart(it)
                val e = getSpanEnd(it)
                setSpan(it, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    /**
     * 处理下一段落文本问题，
     * 上一段落光标后面的文本整体移动（包括样式，相应的上一段落样式也会做相应的调整或删除）。
     */
    private fun next(pre: EditText) {
        val preEdit = pre.text
        val start = pre.selectionStart
        val end = preEdit.length
        // 1，填充新段落文本内容
        editText.setText(pre.text.toString().subSequence(start, end))
        val edit = editText.text

        // 2，开始执行样式操作
        preEdit.getSpans<URLSpan>(start, end).forEach {
            val s = preEdit.getSpanStart(it)
            val e = preEdit.getSpanEnd(it)
            if (s >= start) {
                preEdit.removeSpan(it)
                edit.setSpan(it, s - start, e - start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                preEdit.setSpan(it, s, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                edit.setSpan(URLSpan(it.url), 0, e - start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } // <a>

        // TextStyle
        preEdit.getSpans<StyleSpan>(start, end).forEach {
            val s = preEdit.getSpanStart(it)
            val e = preEdit.getSpanEnd(it)
            if (s >= start) {
                preEdit.removeSpan(it)
                edit.setSpan(it, s - start, e - start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            } else {
                preEdit.setSpan(it, s, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                edit.setSpan(StyleSpan(it.style), 0, e - start, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } // <em> <strong> <p> <span>

        preEdit.getSpans<UnderlineSpan>(start, end).forEach {
            val s = preEdit.getSpanStart(it)
            val e = preEdit.getSpanEnd(it)
            if (s >= start) {
                preEdit.removeSpan(it)
                edit.setSpan(it, s - start, e - start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            } else {
                preEdit.setSpan(it, s, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                edit.setSpan(UnderlineSpan(), 0, e - start, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } // <span>

        preEdit.getSpans<StrikethroughSpan>(start, end).forEach {
            val s = preEdit.getSpanStart(it)
            val e = preEdit.getSpanEnd(it)
            if (s >= start) {
                preEdit.removeSpan(it)
                edit.setSpan(it, s - start, e - start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            } else {
                preEdit.setSpan(it, s, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                edit.setSpan(StrikethroughSpan(), 0, e - start, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } // <span>

        if (start == 0) {
            // TextAlign
            preEdit.getSpans<AlignmentSpan>(0, end).firstOrNull()?.apply {
                edit.setSpan(AlignmentSpan.Standard(this.alignment), 0, edit.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            } // <p>
        }

        // TextFontSize
        preEdit.getSpans<RelativeSizeSpan>(start, end).forEach {
            val s = preEdit.getSpanStart(it)
            val e = preEdit.getSpanEnd(it)
            if (s >= start) {
                preEdit.removeSpan(it)
                edit.setSpan(it, s - start, e - start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            } else {
                preEdit.setSpan(it, s, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                edit.setSpan(RelativeSizeSpan(it.sizeChange), 0, e - start, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } // <p> <span>

        // TextColor
        preEdit.getSpans<ForegroundColorSpan>(start, end).forEach {
            val s = preEdit.getSpanStart(it)
            val e = preEdit.getSpanEnd(it)
            if (s >= start) {
                preEdit.removeSpan(it)
                edit.setSpan(it, s - start, e - start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            } else {
                preEdit.setSpan(it, s, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                edit.setSpan(ForegroundColorSpan(it.foregroundColor), 0, e - start, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } // <span>

        // 3，最后，截取上一段落文本内容
        pre.text = preEdit.replace(start, end, "")

        // 4，【非常重要！！！】同步span样式，否则是默认文本
        syncSpan()
    }

    /**
     * 删除前需要清除文本数据，防止重复计数
     */
    fun clearText() {
        editText.setText("")
    }

    /**
     * 清空输入框焦点
     */
    fun clearEditFocus() {
        editText.clearFocus()
    }

    /**
     * 设置光标
     */
    fun setSelection(index: Int) {
        requestFocus()
        editText.setSelection(index)
        editText.showSoftInput()
    }

    /**
     * true: 单一的输入框；
     *  按下回车键，文本换行，不会生成新的段落（EditText）
     * false: 多输入框；
     *  1、在多输入框模式下，按下回车键，将试图生成下一个新的段落。
     *  2、光标在文本开头时点击删除，将试图合并到上一段落。
     */
    var single: Boolean = false
        set(value) {
            field = value
            setImeOptions(value)
        }

    var hint: CharSequence = ""
        set(value) {
            field = value
            editText.hint = value
        }

    var pEvent: ((event: PEvent) -> Unit)? = null // 段落触发事件，上一段落，下一段落

    private val editText by lazy {
        EditText(context).apply {
            layoutParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT).apply {
//                setMargins(itemMargin, itemMargin, itemMargin, itemMargin)
                setMargins(5.dp, 5.dp, 5.dp, 5.dp)
            }

            gravity = Gravity.CENTER_VERTICAL

            isFocusable = true
            isFocusableInTouchMode = true

            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setLineSpacing(10.dpF, 1F)
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            setTextColor(getColor(R.color.color_4e5e73))
            setHintTextColor(getColor(R.color.color_cbd0d7))
            background = null
            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
//            breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
//            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
//            movementMethod = LinkMovementMethod.getInstance() // 设置该属性，文本为空时光标不可见

            doBeforeTextChanged { text, start, count, after ->
                logT("doBeforeTextChanged", text = text, start = start, count = count, after = after)
            }

            doOnTextChanged { text, start, before, count ->
                logT("    doOnTextChanged", text = text, start = start, before = before, count = count)
            }

            doAfterTextChanged {
                logT(" doAfterTextChanged", text = it)
                syncSpan()
//                textElementData.text = it.toString()
                notifyTextChanged?.invoke()
            }
        }
    }

    /**
     * 同步span
     */
    private fun syncSpan() {
        textElementData.span = editText.text
    }
    /**
     * 初始化段落设置
     */
    private fun initPEvent() {
        setImeOptions(single)
        editText.setOnEditorActionListener { _, _, event ->
            val action = event.action
            when (event.keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    logE("initPEvent", "KEYCODE_ENTER single:$single index:${element.index} ## ${element.view} ## $element")
                    if (!single) {
                        when (action) {
                            KeyEvent.ACTION_DOWN -> {
                                pEvent?.invoke(PEvent.NEXT)
                                return@setOnEditorActionListener true
                            }
                        }
                    }
                }
                else -> {}
            }
            return@setOnEditorActionListener false
        }
        editText.setOnKeyListener { _, _, event ->
            val action = event.action
            when (event.keyCode) {
                KeyEvent.KEYCODE_DEL -> {
                    logE("initPEvent", "KEYCODE_DEL single:$single index:${element.index} ## ${element.view} ## $element")
                    if (!single) {
                        when (action) {
                            KeyEvent.ACTION_DOWN -> {
                                if (editText.selectionStart == 0 && editText.selectionEnd == 0) {
                                    pEvent?.invoke(PEvent.PRE)
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
        editText.imeOptions = if (isDone) {
            EditorInfo.IME_ACTION_DONE
        } else {
            EditorInfo.IME_ACTION_UNSPECIFIED
        }
    }

    private fun initView() {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        editText.setBackgroundColor(Color.parseColor("#99f3f4f5"))
        addView(editText)
        initPEvent()
    }

    private fun fillText() {
        post {
            val span = textElementData.span
            if (span.isNullOrEmpty().not()) {
                editText.text = span
            }
        }
    }

    override var focusChanged: ((View, Boolean) -> Unit)? = null
        set(value) {
            field = value
            editText.setOnFocusChangeListener { v, hasFocus ->
                value?.invoke(v, hasFocus)
            }
        }

    override var hasFocused: ((View, Boolean) -> Unit)? = null
        set(value) {
            field = value
            editText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    value?.invoke(v, hasFocus)
                }
            }
        }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        logF(gainFocus)
        state = if (gainFocus) {
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
//        foreground = null
        // 文本输入最大行数限制
//        editText.maxLines = maxLineLimit
        post {
            logH("normalState", oldH, height)
        }
    }

    /**
     * 编辑状态 修改/删除
     */
    override fun editState() {
        val oldH = height
        editText.maxLines = maxLineLimit
        logH("  editState", oldH, height)
    }

    /**
     * 移动状态
     */
    override fun moveState() {
        val oldH = height
//        foreground = null
//        editText.maxLines = moveStateLineLimit
        editText.apply {
            layout(left, top, right, top + moveStateHeight - mMarginV)
        }
        post {
            logH("  moveState", oldH, height)
        }
    }

    override val moveStateHeight: Int
        get() {
            // 三行文字限制
            val count = baseHeight / lineHeight
            val dy = editHeight % lineHeight
            val limitHeight = lineHeight * count + dy + mMarginV

            logW("moveStateHeight", "dy = $dy :: measuredHeight = $editHeight :: limitHeight = $limitHeight :: lineHeight = $lineHeight :: letterSpacing = ${editText.letterSpacing} :: lineSpacingExtra = ${editText.lineSpacingExtra} :: paddingTop = ${editText.paddingTop} :: paddingBottom = ${editText.paddingBottom} :: marginTop = ${editText.marginTop}")

            val mHeight = editHeight + mMarginV
            if (mHeight > limitHeight) {
                return limitHeight
            }
            return mHeight // heightOutSide
        }

    private val lineHeight = editText.lineHeight
    private val baseHeight = editText.getTextHeightWithLines(moveStateLineLimit)
    private val editHeight: Int
        get() = editText.measuredHeight
    private val mMarginV: Int = itemMargin * 2

//    private fun log(methodName: String, old: Int, new: Int) {
//        "$logTag $methodName :: height($old -> $new)".i()
//    }
}