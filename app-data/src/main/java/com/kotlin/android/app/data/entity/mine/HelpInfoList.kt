package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.app.data.entity.mine
 * @ClassName:      HelpInfos
 * @Description:    获取帮助中心-等级相关问题
 * @Author:         haoruigang
 * @CreateDate:     2022/4/1 14:22
 */
data class HelpInfoList(
    var helpInfos: List<HelpInfos>? = mutableListOf(),
) : ProguardRule {
    data class HelpInfos(val helpId: Long?, val helpName: String?, val details: String?) :
        ProguardRule
}
