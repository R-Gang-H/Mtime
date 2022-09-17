package com.kotlin.android.comment.component.widget

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * create by lushan on 2020/9/27
 * description:
 */
class CommentImageDrawable(@ColorInt color:Int):Drawable() {
    var paint:Paint
    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
    }
    override fun draw(p0: Canvas) {
        val rect = bounds
        p0.drawRoundRect(
                rect.left.toFloat(),
                rect.top + 5.dpF,
                rect.right - 5.dpF,
                rect.bottom - 10.dpF,
                4.dpF,
                4.dpF,
                paint
        )
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(rect.right.toFloat() - 20.dp, rect.bottom.toFloat() - 10.dp)//右点
        path.lineTo(rect.right.toFloat() - 25.dp, rect.bottom.toFloat())
        path.lineTo(rect.right.toFloat() - 31.dp, rect.bottom.toFloat() - 10.dp)
        p0.drawPath(path, paint)


    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
        invalidateSelf()

    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}