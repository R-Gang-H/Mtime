package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ugc.detail.component.R

/**
 * create by lushan on 2022/3/16
 * des:视频、音频基本信息
 **/
data class UgcMediaInfoViewBean(
    var userId: Long = 0L,//用户id
    var userName: String = "",//用户昵称
    var userImg: String = "",//用户头像
    var userAuthType:Long = 0L,//认证类型
    var fansCount: Long = 0L,//粉丝数量
    var isFollow: Boolean = false,//是否关注了
    var title: String = "",//标题
    var content: String = "",//内容
    var createTime: Long = 0L,//发布时间
    var playCount: Long = 0L//播放量
) : ProguardRule{
    fun getFansNumStr() = getString(R.string.ugc_detail_component_fans_format).format(formatCount(fansCount))

    fun getPublishDateStr():String{
        return "${formatPublishTime(createTime)}・${formatCount(playCount)}${getString(R.string.ugc_detail_component_play)}"
    }

    fun  isAuth() = userAuthType > CommConstant.AUTH_TYPE_PERSONAL

    fun getAuthDrawable() = if (userAuthType == CommConstant.AUTH_TYPE_ORGAN) getDrawable(R.drawable.ic_jigourenzheng) else getDrawable(R.drawable.ic_yingrenrenzheng)
}