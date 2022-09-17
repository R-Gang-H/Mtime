package com.kotlin.android.widget.textview

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ScaleXSpan
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView


/**
 * 创建者: zl
 * 创建时间: 2020/11/12 1:35 下午
 * 描述:在TextView直接设置android:textStyle="bold|italic"属性，发现右边的字会被切去一些
 * TextView的显示长度是根据文本的长度来显示的，因此可以在文本末再新增一个空格，这样让TextView的显示区域更大一些。
 */
class SpacingTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var originalText: CharSequence? = ""

    companion object {
        private const val SCALE_X = 1.0f // x轴缩放比例
    }

    override fun setText(text: CharSequence?, type: TextView.BufferType?) {
        if (!text.isNullOrEmpty()) {
            originalText = text
            applyLastLetterSpacing()
        } else {
            super.setText(text, type)
        }
    }

    override fun getText(): CharSequence? {
        return originalText
    }

    private fun applyLastLetterSpacing() {
        if (originalText.isNullOrEmpty()) {
            return
        }
        val builder = StringBuilder()
        val length = originalText?.length ?: 0
        for (i in 0 until length) {
            val c = "" + originalText!![i]
            builder.append(c)
        }
        builder.append("\u00A0") // 末端新增一个空格
        val finalText = SpannableString(builder.toString())
        // 为了让空格看起来很明显，对空格进行一定的缩放
        finalText.setSpan(ScaleXSpan(SCALE_X),
                builder.toString().length - 1, builder.toString().length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        super.setText(finalText, BufferType.SPANNABLE)
    }
}