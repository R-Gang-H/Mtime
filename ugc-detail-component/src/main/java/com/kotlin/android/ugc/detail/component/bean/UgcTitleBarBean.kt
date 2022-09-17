package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.entity.community.album.AlbumInfo
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.constant.CommConstant.AUTH_TYPE_ORGAN
import com.kotlin.android.app.data.constant.CommConstant.AUTH_TYPE_PERSONAL


/**
 * create by lushan on 2020/8/10
 * description: ugctitleBar对应viewBean
 */
data class UgcTitleBarBean(
        var userId: Long = 0L,//用户id
        var userName: String = "",
        var headPic: String = "",//用户头像
        var publishTime:String = "",//发布时间
        var isAttention: Boolean = false,//是否是关注
        var isReview: Boolean = false,//是否是影评
        var score: String = "",//用户对影片的评分
        var isAlbumTitle:Boolean = false,//是否是图集标题
        var userAuthType:Long = 0L//认证类型
) : ProguardRule {
    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return userAuthType == AUTH_TYPE_ORGAN
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return userAuthType > AUTH_TYPE_PERSONAL
    }

    companion object{
        fun convert(userHomePage: AlbumInfo.UserHomePage,time:String):UgcTitleBarBean{
            return UgcTitleBarBean(userId = userHomePage.userId,
                    userName = "${userHomePage.nikeName.orEmpty()}  发布的相册",
                    headPic = userHomePage.avatarUrl.orEmpty(),
                    publishTime = time,
                    userAuthType = userHomePage.authType?:0L,
                    isAttention = userHomePage.followed?:false
                    )
        }
    }
}