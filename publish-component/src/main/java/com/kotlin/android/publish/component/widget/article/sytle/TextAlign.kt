package com.kotlin.android.publish.component.widget.article.sytle

import android.text.Layout.Alignment

/**
 * 文本对齐方式
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
enum class TextAlign(val value: String, val alignment: Alignment) {

    /**
     * 无
     */
    NONE("text-align: justify;", alignment = Alignment.ALIGN_NORMAL),

    /**
     * 左右对齐
     * <p style="text-align: justify;">左右对齐</p>
     */
    JUSTIFY("text-align: justify;", alignment = Alignment.ALIGN_NORMAL),

    /**
     * 左对齐
     * <p style="text-align: left;">左对齐</p>
     */
    LEFT("text-align: left;", alignment = Alignment.ALIGN_NORMAL),

    /**
     * 居中
     * <p style="text-align: center;">居中</p>
     */
    CENTER("text-align: center;", alignment = Alignment.ALIGN_CENTER),

    /**
     * 右对齐
     * <p style="text-align: right;">右对齐</p>
     */
    RIGHT("text-align: right;", alignment = Alignment.ALIGN_OPPOSITE);

    companion object {
        fun obtain(alignment: Alignment): TextAlign? {
            return when (alignment) {
                JUSTIFY.alignment -> JUSTIFY
                LEFT.alignment -> LEFT
                CENTER.alignment -> CENTER
                RIGHT.alignment -> RIGHT
                NONE.alignment -> NONE
                else -> null
            }
        }
    }

}