package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2021/11/23
 * des: 导播台内容信息
 **/
data class DirectorUnits(
    var liveId: Long = 0L,//直播id
    var timestamp: Long = 0L,//更新时间(Long型时间戳)
    var caption: Caption? = null,//字幕
    var layerImages: MutableList<LayerImage>? = null//图层数据列表
) : ProguardRule, Serializable {
    data class Caption(
        var key: Long = 0L,//字幕id
        var value: String? = "",//字幕
        var color: String? = ""//字幕颜色(16进制格式)
    ) : ProguardRule, Serializable

    data class LayerImage(
        var key: Long = 0L,//图片id
        var value: String? = "",//图片filed
        var url: String? = "",//图片url
        var xPos: Long = 0L,//x坐标轴，整数值，以宽的比例值（千分之N）后端返回（左上为原点）
        var yPos: Long = 0L,//y坐标轴，整数值，以高的比例值（千分之N）后端返回（左上为原点）
        var width: Long = 0L,//图片原始宽
        var height: Long = 0L,//图片原始高
        var proportion: Long = 0L//图片显示比例，整数值。以图片宽和画布宽的比例值（千分之N）后端返回
    ) : ProguardRule, Serializable
}