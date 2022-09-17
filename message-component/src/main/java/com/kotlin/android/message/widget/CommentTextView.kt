package com.kotlin.android.message.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.message.R

/**
 * Created by zhaoninglongfei on 2022/4/22
 * 评论textview 分段展示不同颜色
 *
 * 展开/收起
 */

@BindingAdapter("commentPre")
fun setCommentPre(view: CommentTextView, text: String?) {
    view.setCommentPre(text)
}

@BindingAdapter("commentText")
fun setCommentText(view: CommentTextView, text: String?) {
    view.setCommentText(text)
}


class CommentTextView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var commentPre: String? = null
    private var commentText: String? = null

    private val ellipsizeEnd = "..."
    private val ellipsizeListeners: MutableList<EllipsizeListener> = ArrayList()

    private var isEllipsized = false
    private var isStale = false
    private var programmaticChange = false
    private var fullText: String? = null
    private var maxLines = 3

    fun setCommentPre(text: String?) {
        commentPre = text
        applyComment()
    }

    fun setCommentText(text: String?) {
        commentText = text
        applyComment()
    }

    @SuppressLint("SetTextI18n")
    private fun applyComment() {
        if (commentPre.isNullOrEmpty() || commentText.isNullOrEmpty()) {
            return
        }
        spanTextApply("$commentPre$commentText")
    }

    private fun spanTextApply(totalText: String) {
        val preComment = commentPre ?: ""
        //截取pre后 剩余的部分
        val elseComment = totalText.substring(preComment.length)

        val builder = SpannableStringBuilder().append(preComment).append(elseComment)
        val preSpan = ForegroundColorSpan(getColor(R.color.color_8798af))
        val contentSpan = ForegroundColorSpan(getColor(R.color.color_3d4955))

        builder.setSpan(preSpan, 0, preComment.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        builder.setSpan(
            contentSpan,
            preComment.length,
            preComment.length + elseComment.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        this.text = builder
    }

    fun addEllipsizeListener(listener: EllipsizeListener) {
        ellipsizeListeners.add(listener)
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        this.maxLines = maxLines
        isStale = true
    }

    /*
     * 根据实际文本行数、设置初始展示行数来回切换
     */
    fun toggleShowLines() {
        val workingText = fullText
        workingText?.let {
            val layout = createMeasureLayout(it)
            val contentLines = layout.lineCount
            isEllipsized = !isEllipsized
            val linesCount = if (isEllipsized) maxLines else contentLines
            super.setMaxLines(linesCount)
            resetText(linesCount)
        }
    }

    override fun getMaxLines(): Int {
        return maxLines
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, after: Int) {
        super.onTextChanged(text, start, before, after)
        if (!programmaticChange) {
            fullText = text.toString()
            isStale = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (isStale) {
            super.setEllipsize(null)
            val allCount = getMaxLines()
            resetText(allCount)
        }
        super.onDraw(canvas)
    }

    @SuppressLint("NewApi")
    private fun resetText(showLines: Int) {
        var workingText = fullText
        isEllipsized = false
        if (showLines > 0 && workingText != null && workingText.isNotEmpty()) {
            val layout = createMeasureLayout(workingText)

            if (showLines > 3) {
                for (listener in ellipsizeListeners) {
                    listener.onExpand()
                }
            } else {

                //可以展示的行数 大于 设置的行数
                if (layout.lineCount > showLines) {
                    workingText =
                        fullText!!.substring(0, layout.getLineEnd(showLines - 1) - 1)
                            .trim { it <= ' ' }
                    val layout2 = createMeasureLayout(workingText + ellipsizeEnd)
                    while (layout2.lineCount > showLines) {
                        println(layout2.lineCount.toString() + "\t" + showLines)
                        val lastSpace = workingText!!.lastIndexOf(' ')
                        if (lastSpace == -1) {
                            break
                        }
                        workingText = workingText.substring(0, lastSpace)
                    }
                    workingText += ellipsizeEnd
                    isEllipsized = true
                    for (listener in ellipsizeListeners) {
                        listener.onCollapse()
                    }
                } else {
                    for (listener in ellipsizeListeners) {
                        listener.onLoss()
                    }
                }
            }
        } else {
            for (listener in ellipsizeListeners) {
                listener.onLoss()
            }
        }

        if (workingText != this.text) {
            programmaticChange = true
            try {
                workingText?.let {
                    spanTextApply(it)
                }
            } finally {
                programmaticChange = false
            }
        }
        isStale = false
    }

    private fun createMeasureLayout(workingText: String): Layout {
        return StaticLayout.Builder.obtain(
            workingText, 0, workingText.length, this.paint, this.width - this.paddingLeft
                    - this.paddingRight
        ).build()
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) {}

    interface EllipsizeListener {
        /**
         * 展开状态
         */
        fun onExpand()

        /**
         * 收起状态
         */
        fun onCollapse()

        /**
         * 行数小于最小行数，不满足展开或者收起条件
         */
        fun onLoss()
    }
}