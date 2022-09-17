package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by WangWei on 2020/10/24
 * description:优惠券条目
 */
data class CouponItemViewBean(var description: String ="",
                              var endTime: Long=0L,
                              var id: Long =0L,
                              var movieId: Long=0L,
                              var movieImg: String="",
                              var name: String="",
                              var orderId: Long=0L,
                              var startTime: Long=0L,
                              var status: String="",
                              var useTime: Long=0L,
                              var timeDes:String=""
) : ProguardRule