package com.kotlin.android.app.data.entity.bonus

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/12/28
 * description: 开彩蛋
 */
data class BonusScene(
        var prizeType:Long = 0L,//1, 购物劵 2, 购票卷 3, M豆 4, 经验值 5, 无 6, 大富翁金币 7, 实物奖品
        var quantity:Long = 0L,//彩蛋中奖数量
        var prizeName:String? = "",//彩蛋奖品名称
        var msg:String? = "",//code!=0 才输出信息
        var success:Boolean = false//是否中奖，true=中奖 false 未中奖
): ProguardRule