package com.kotlin.android.publish.component.widget.article.sytle

import com.kotlin.android.ktx.ext.dimension.spF

/**
 * 文本字体大小，手机:[size] PC:[pcSize]
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
enum class TextFontSize(
    val level: Int,
    val size: Float,
    val scale: Float,
    val pcSize: Float,
    val clazz: String,
    val clazzBold: String
) {
    /**
     * 小
     */
    SMALL(
        level = 1,
        size = 13.spF,
        scale = 0.867F,
        pcSize = 13.spF,
        clazz = "mini_",
        clazzBold = "mini_ strong"
    ),

    /**
     * 标准
     */
    STANDARD(
        level = 2,
        size = 15.spF,
        scale = 1F,
        pcSize = 16.spF,
        clazz = "standard_",
        clazzBold = "standard_ strong"
    ),

    /**
     * 大
     */
    BIG(
        level = 3,
        size = 17.spF,
        scale = 1.133F,
        pcSize = 17.spF,
        clazz = "medium_",
        clazzBold = "medium_ strong"
    ),

    /**
     * 超大
     */
    LARGER(
        level = 4,
        size = 19.spF,
        scale = 1.267F,
        pcSize = 18.spF,
        clazz = "large_",
        clazzBold = "large_ strong"
    );

    companion object {
        fun obtain(clazz: String): TextFontSize? {
            return when (clazz) {
                "mini_" -> SMALL
                "standard_" -> STANDARD
                "medium_" -> BIG
                "large_" -> LARGER
                else -> null
            }
        }
        fun obtain(scale: Float): TextFontSize? {
            return when (scale) {
                SMALL.scale -> SMALL
                STANDARD.scale -> STANDARD
                BIG.scale -> BIG
                LARGER.scale -> LARGER
                else -> null
            }
        }
    }
}