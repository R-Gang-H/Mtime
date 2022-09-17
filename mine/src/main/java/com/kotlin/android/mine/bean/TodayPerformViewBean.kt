package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.ui.datacenter.datacenter
 * @ClassName:      TodayPerforms
 * @Description:     java类作用描述
 * @Author:         haoruigang
 * @CreateDate:     2022/3/17 18:15
 * @UpdateUser:     更新者：
 * @UpdateDate:     2022/3/17 18:15
 * @UpdateRemark:   更新说明：
 * @Version:
 */
class TodayPerformViewBean(
    var date: String,
    var playback: String,
    var reading: String?="",
    var praise: String,
    var comment: String,
    var collect: String,
) : ProguardRule