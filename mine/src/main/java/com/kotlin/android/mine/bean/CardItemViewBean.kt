package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by WangWei on 2020/10/25
 * description:礼品卡条目
 */
data class CardItemViewBean(var membershipCardId: Long = 0L,
                            var status: Long = 0L,
                            var timeDes: String = "",
                            var balance: Long = 0L,
                            var type: Long = 0L,
                            var balancePoint: String = "",
                            var name: String = "",
                            var unitDes:String = "",
                            var count:String = "",
                            var cNum:String = ""

) : ProguardRule