package com.kotlin.android.publish.component.widget.article.view.entity

import android.graphics.Typeface
import android.text.Editable
import android.text.ParcelableSpan
import android.text.style.*
import androidx.core.text.getSpans
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.xml.entity.Element

/**
 * 富文本卡片的数据模型，关联富文本相关 Element 元素
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
class TextElementData : IElementData {

    private var textElement: Element = Element(
        text = "",
    )

    /**
     * 段落根元素
     */
    override var element: Element = Element(
        tag = "p",
        items = arrayListOf(textElement),
    )
        set(value) {
            field = value
            value.apply {
            }
        }

//    var data: String? = null

    /**
     * 富文本对象
     */
    var span: Editable?
        get() = element.toEditable()
        set(value) {
            fill(value)
        }

    var text: CharSequence = ""
        set(value) {
            field = value
            element.text = value.toString()
        }

    private fun fill(edit: Editable?) {
        edit?.apply {
            val len = length
            // 对齐span仅一个直接处理
            getSpans<AlignmentSpan>(0, len).forEach {
                element.style = TextAlign.obtain(it.alignment)?.value
            }

            // 获取所有的span集合
            val ranges = ArrayList<SpanRange>()
            // TextFontSize
            getSpans<RelativeSizeSpan>(0, len).forEach {
                ranges.add(
                    SpanRange(
                        type = TYPE_SIZE,
                        start = getSpanStart(it),
                        end = getSpanEnd(it),
                        span = it,
                        size = TextFontSize.obtain(it.sizeChange)?.clazz.orEmpty()
                    )
                )
            } // <p> <span>

            // TextColor
            getSpans<ForegroundColorSpan>(0, len).forEach {
                ranges.add(
                    SpanRange(
                        type = TYPE_COLOR,
                        start = getSpanStart(it),
                        end = getSpanEnd(it),
                        span = it,
                        color = TextColor.obtain(it.foregroundColor)?.style.orEmpty()
                    )
                )
            } // <span>

            // TextStyle
            getSpans<UnderlineSpan>(0, len).forEach {
                ranges.add(
                    SpanRange(
                        type = TYPE_U,
                        start = getSpanStart(it),
                        end = getSpanEnd(it),
                        span = it
                    )
                )
            } // <span>
            getSpans<StrikethroughSpan>(0, len).forEach {
                ranges.add(
                    SpanRange(
                        type = TYPE_S,
                        start = getSpanStart(it),
                        end = getSpanEnd(it),
                        span = it
                    )
                )
            } // <span>
            getSpans<StyleSpan>(0, len).forEach {
                when (it.style) {
                    Typeface.BOLD -> {
                        ranges.add(
                            SpanRange(
                                type = TYPE_B,
                                start = getSpanStart(it),
                                end = getSpanEnd(it),
                                span = it
                            )
                        )
                    }
                    Typeface.ITALIC -> {
                        ranges.add(
                            SpanRange(
                                type = TYPE_I,
                                start = getSpanStart(it),
                                end = getSpanEnd(it),
                                span = it
                            )
                        )
                    }
                }
            } // <em> <strong> <p> <span>

            getSpans<URLSpan>(0, len).forEach {
                ranges.add(
                    SpanRange(
                        type = TYPE_A,
                        start = getSpanStart(it),
                        end = getSpanEnd(it),
                        span = it,
                        href = it.url.orEmpty()
                    )
                )
            } // <a>

            handleSpanRanges(edit = this, ranges)
        }
    }

    /**
     * 处理span集合
     */
    private fun handleSpanRanges(edit: Editable, ranges: ArrayList<SpanRange>) {
        edit.apply {
            val len = length
            // 清空历史数据
            if (element.items == null) {
                element.items = ArrayList()
            } else {
                element.items?.clear()
            }

            val sectionRanges = ArrayList<SectionRange>()
            // 顶点去重集合
            val points = HashSet<Int>()
            // 超链接范围
            val aRanges = HashSet<IntRange>()
            ranges.forEach {
                if (it.type == TYPE_A) {
                    aRanges.add(IntRange(it.start, it.end))
                }
            }
            ranges.forEach {
                val start = it.start
                val end = it.end
                if (aRanges.isEmpty()) {
                    // 添加顶点：没有超链接直接添加
                    points.add(start)
                    points.add(end)
                } else {
                    aRanges.forEach { aRange ->
                        // 添加顶点：不能在超链接内部
                        if (start !in aRange) {
                            points.add(start)
                        }
                        if (end !in aRange) {
                            points.add(end)
                        }
                    }
                }
            }
            // 添加顶点：然后把超链接顶点加入
            aRanges.forEach { aRange ->
                points.add(aRange.first)
                points.add(aRange.last)
            }
            // 最后添加两个端点
            points.add(0)
            points.add(len)

            // 自然排序后，切割区间范围
            points.sorted().forEach {
                if (it > 0) {
                    val last = sectionRanges.lastOrNull()
                    val start = last?.end ?: 0
                    sectionRanges.add(SectionRange(start = start, end = it))
                }
            }

            // 遍历区间集合，根据span投影进行加权，赋值，注意超链接进行全覆盖。
            sectionRanges.forEach { sectionRange ->
                val start = sectionRange.start
                val end = sectionRange.end
                // 赋值text
                sectionRange.text = substring(start, end)

                ranges.forEach {
                    if ((it.start <= start && end <= it.end)
                        || (it.start <= start && start < it.end) // 解决超链接，设置部分样式的情况，进行全覆盖
                        || (it.start < end && end <= it.end) // 解决超链接，设置部分样式的情况，进行全覆盖
                    ) {
                        // 根据span投影加权
                        sectionRange.type = sectionRange.type or it.type
                        // 赋值
                        when (it.type) {
                            TYPE_A -> {
                                sectionRange.href = it.href
                            }
                            TYPE_COLOR -> {
                                sectionRange.color = it.color
                            }
                            TYPE_SIZE -> {
                                sectionRange.size = it.size
                            }
                        }
                    }
                }
            }

            // 遍历处理好的区间，添加元素。
            sectionRanges.forEach {
                addElement(sectionRange = it)
            }
        }
    }

    /**
     * 根据区间信息，像富文本区域添加元素
     * <span style="color: rgb(241, 196, 15);text-decoration: line-through underline;" class="standard_">
     */
    private fun addElement(sectionRange: SectionRange) {
        val type = sectionRange.type
        // 仅文本区间，直接添加文本
        if (type == TYPE_NONE) {
            addText(text = sectionRange.text)
            return
        }

        // 仅超链接，直接添加超链接
        if (type == TYPE_A) {
            addA(text = sectionRange.text, href = sectionRange.href)
            return
        }

        // 添加span：处理 style, class 和字体样式
        val color = if (type and TYPE_COLOR == TYPE_COLOR) {
            sectionRange.color
        } else {
            ""
        }
        val size = if (type and TYPE_SIZE == TYPE_SIZE) {
            sectionRange.size
        } else {
            ""
        }
        val u = if (type and TYPE_U == TYPE_U) {
            " underline"
        } else {
            ""
        }
        val s = if (type and TYPE_S == TYPE_S) {
            " line-through"
        } else {
            ""
        }
        val isB = type and TYPE_B == TYPE_B
        val isI = type and TYPE_I == TYPE_I
        val isA = type and TYPE_A == TYPE_A
        val line = if (u.isNotEmpty() || s.isNotEmpty()) {
            "text-decoration:$u$s;"
        } else {
            ""
        }
        addSpan(
            style = "$color$line",
            clazz = size,
            text = sectionRange.text,
            href = sectionRange.href,
            isB = isB,
            isI = isI,
            isA = isA,
        )
    }

    /**
     * 添加span元素
     */
    private fun addSpan(
        style: String = "",
        clazz: String = "",
        text: String? = null,
        href: String? = null,
        isB: Boolean = false,
        isI: Boolean = false,
        isA: Boolean = false,
    ) {
        val spanElement = Element(
            tag = "span",
            style = style,
            clazz = clazz,
        )
        if (isB && isI) {
            val bElement = Element(tag = "strong")
            val iElement = Element(tag = "em")
            if (isA) {
                val aElement = Element(tag = "a", href = href)
                aElement.items = ArrayList()
                aElement.items?.add(Element(text = text))
                iElement.items = ArrayList()
                iElement.items?.add(aElement)
            } else {
                iElement.items = ArrayList()
                iElement.items?.add(Element(text = text))
            }
            spanElement.items = ArrayList()
            spanElement.items?.add(bElement)
            bElement.items = ArrayList()
            bElement.items?.add(iElement)
        } else if (isB) {
            val bElement = Element(tag = "strong")
            if (isA) {
                val aElement = Element(tag = "a", href = href)
                aElement.items = ArrayList()
                aElement.items?.add(Element(text = text))
                bElement.items = ArrayList()
                bElement.items?.add(aElement)
            } else {
                bElement.items = ArrayList()
                bElement.items?.add(Element(text = text))
            }
            spanElement.items = ArrayList()
            spanElement.items?.add(bElement)
        } else if (isI) {
            val iElement = Element(tag = "em")
            if (isA) {
                val aElement = Element(tag = "a", href = href)
                aElement.items = ArrayList()
                aElement.items?.add(Element(text = text))
                iElement.items = ArrayList()
                iElement.items?.add(aElement)
            } else {
                iElement.items = ArrayList()
                iElement.items?.add(Element(text = text))
            }
            spanElement.items = ArrayList()
            spanElement.items?.add(iElement)
        } else {
            if (isA) {
                val aElement = Element(tag = "a", href = href)
                aElement.items = ArrayList()
                aElement.items?.add(Element(text = text))
                spanElement.items = ArrayList()
                spanElement.items?.add(aElement)
            } else {
                spanElement.items = ArrayList()
                spanElement.items?.add(Element(text = text))
            }
        }

        element.items?.add(spanElement)
    }

    /**
     * 添加超链接元素
     */
    private fun addA(text: String, href: String) {
        val aElement = Element(tag = "a", href = href)
        aElement.items = ArrayList()
        aElement.items?.add(Element(text = text))
        element.items?.add(aElement)
    }

    /**
     * 添加文本元素
     */
    private fun addText(text: String) {
        element.items?.add(Element(text = text))
    }

}

/**
 * span样式范围
 */
data class SpanRange(
    var type: Int,
    var start: Int,
    var end: Int,
    var span: ParcelableSpan,
    var size: String = "",
    var color: String = "",
    var href: String = "",
)

/**
 * 区间范围：将span样式划分多个区间范围，确保每一个区间范围样式的完整，解决样式嵌套及交叉嵌套范围问题
 * [type] 多样式类型
 */
data class SectionRange(
    var type: Int = TYPE_NONE,
    var start: Int,
    var end: Int,
    var text: String = "",
    var size: String = "",
    var color: String = "",
    var href: String = "",
)

/**
 * span权重
 */
const val TYPE_NONE =   0b00000000 // 文本
const val TYPE_B =      0b00000001
const val TYPE_I =      0b00000010
const val TYPE_U =      0b00000100
const val TYPE_S =      0b00001000
const val TYPE_SIZE =   0b00010000
const val TYPE_COLOR =  0b00100000
const val TYPE_A =      0b01000000 // 超链接
const val TYPE_ALL =    0b11111111


//    private fun handleSpanRanges2(edit: Editable, ranges: ArrayList<SpanRange>) {
//        edit.apply {
//            val len = length
//            // 清空历史数据
//            lastSpan = null
//            if (element.items == null) {
//                element.items = ArrayList()
//            } else {
//                element.items?.clear()
//            }
//
//            ranges.sortBy { it.start }
//            val minPosition = ranges.firstOrNull()?.start ?: 0
//            var maxPosition = ranges.firstOrNull()?.end ?: len
//            if (minPosition > 0) {
//                // 添加开头普通文本
//                addText(text = substring(0, minPosition))
//            }
//            var pStart = minPosition
//            val pRange = ArrayList<IntRange>()
//            ranges.forEach {
//                val start = it.start
//                val end = it.end
//
//                if (start >= maxPosition) {
//                    // 新起一个区间
//                    pRange.add(IntRange(pStart, start))
//                    pStart = start
//                    if (start > maxPosition) {
//                        // 添加中间普通文本，必须有数据才添加
//                        addText(text = substring(maxPosition, start))
//                    }
//                }
//
//                if (end > maxPosition) {
//                    maxPosition = end
//                }
//
//                if (it.type == TYPE_A) { // 超链接
//                    addA(range = it, text = substring(it.start, it.end))
//                } else {
//                    addSpan(lastSpan = lastSpan, range = it, text = substring(it.start, it.end))
//                }
//            }
//            if (maxPosition < len) {
//                // 添加结尾普通文本
//                addText(text = substring(maxPosition))
//            }
//
//            val sectionRanges = ArrayList<SectionRange>()
//            val size = ranges.size
//            ranges.forEachIndexed { index, spanRange ->
//                val type = spanRange.type
//                val start = spanRange.start
//                val end = spanRange.end
//                val next = index + 1
//                val lastRange = sectionRanges.lastOrNull()
//                if (type == TYPE_A) {
//
//                }
//                if (lastRange == null) {
//                    if (start > 0) {
//                        sectionRanges.add(
//                            SectionRange(
//                                type = TYPE_NONE,
//                                start = 0,
//                                end = start,
//                                maxEnd = start,
//                                text = substring(0, start)
//                            )
//                        )
//                    }
//                    var relEnd = end
//                    if (next < size) {
//                        val nextStart = ranges[next].start
//                        if (nextStart < end) {
//                            relEnd = nextStart
//                        }
//                    }
//                    sectionRanges.add(
//                        SectionRange(
//                            type = type,
//                            start = start,
//                            end = relEnd,
//                            maxEnd = end,
//                            text = substring(start, relEnd)
//                        )
//                    )
//                } else {
//                    val lastEnd = lastRange.end
//                    if (start > lastEnd) {
//                        sectionRanges.add(
//                            SectionRange(
//                                type = TYPE_NONE,
//                                start = lastEnd,
//                                end = start,
//                                maxEnd = start,
//                                text = substring(lastEnd, start)
//                            )
//                        )
//                    }
//                    var relEnd = end
//                    if (next < size) {
//                        val nextStart = ranges[next].start
//                        if (end > nextStart) {
//                            relEnd = nextStart
//                        }
//                        // 重要！！！遍历已经处理过的区间，待定区间[end, maxEnd]问题
//                        sectionRanges.forEach {
//                            val maxEnd = it.maxEnd
//                            if (maxEnd > it.end) {
//                                if (relEnd > maxEnd) {
//                                    relEnd = maxEnd
//                                }
//                            }
//                        }
//                    }
//                    var relType = type
//                    ranges.forEach {
//                        if (it.start <= start && relEnd <= it.end) {
//                            relType = relType or it.type
//                        }
//                    }
//                    sectionRanges.add(
//                        SectionRange(
//                            type = relType,
//                            start = start,
//                            end = relEnd,
//                            maxEnd = end,
//                            text = substring(0, relEnd)
//                        )
//                    )
//                }
//            }
//
////            ranges.sortBy { it.end }
////            ranges.forEach {
////                val type = it.type
////                val start = it.start
////                val end = it.end
////                val lastRange = sectionRanges.lastOrNull()
////                if (lastRange != null) {
////                    val lastEnd = lastRange.end
////                    if (it.end > lastEnd) {
////                        var relType = type
////                        ranges.forEach {
////                            if (it.start <= start && relEnd <= it.end) {
////                                relType = relType or it.type
////                            }
////                        }
////
////                        sectionRanges.add(
////                            SectionRange(
////                                type = relType,
////                                start = start,
////                                end = relEnd,
////                                maxEnd = end,
////                                text = substring(0, relEnd)
////                            )
////                        )
////                    }
////                }
////            }
//        }
//    }