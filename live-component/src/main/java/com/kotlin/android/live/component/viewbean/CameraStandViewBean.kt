package com.kotlin.android.live.component.viewbean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.live.component.R
import com.kotlin.android.mtime.ktx.getDimension
import com.kotlin.android.mtime.ktx.getDimensionPixelOffset

/**
 * create by lushan on 2021/3/9
 * description: 机位列表viewBean
 */
data class CameraStandViewBean(
        var cameraId: Long = 0L,
        var img:String = "",//封面图
        var title:String = "",//机位名称
        var isSelected:Boolean = false//是否是选中状态
) : ProguardRule {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CameraStandViewBean

        if (cameraId != other.cameraId) return false
        if (img != other.img) return false
        if (title != other.title) return false
        if (isSelected != other.isSelected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cameraId.hashCode()
        result = 31 * result + img.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + isSelected.hashCode()
        return result
    }

    fun getItemCardCorner(isPortrait: Boolean):Int{
        return if (isPortrait) getDimensionPixelOffset(R.dimen.offset_8px) else getDimensionPixelOffset(R.dimen.offset_12px)
    }

    fun getItemTextSize(isPortrait: Boolean):Float{
        return if (isPortrait) getDimension(R.dimen.font_size_sp_12) else getDimension(R.dimen.font_size_sp_17)
    }

}