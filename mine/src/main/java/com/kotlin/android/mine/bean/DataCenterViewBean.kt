package com.kotlin.android.mine.bean

import androidx.annotation.ColorRes
import com.kotlin.android.app.data.ProguardRule

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.ui.datacenter.data
 * @ClassName:      Tags
 * @Description:    创作者数据中心 ViewBean
 * @Author:         haoruigang
 * @CreateDate:     2022/3/11 15:28
 * @UpdateUser:     更新者：
 * @UpdateDate:     2022/3/11 15:28
 * @UpdateRemark:   更新说明：
 * @Version:
 */
class DataCenterViewBean : ProguardRule {

    data class Tags(
        val index: Long,
        var isSelect: Boolean,
        val tagName: String,
    ) : ProguardRule

    data class Tabs(
        val index: Long,
        var isSelect: Boolean,
        var tabName: String,
        var tabNum: String?,
    ) : ProguardRule

    data class DataAmount(
        @ColorRes val bgColor: Int,
        @ColorRes val frameColor: Int,
        val fontColor: Int,
        var amount: String?,
        val desText: String,
    ) : ProguardRule

}
