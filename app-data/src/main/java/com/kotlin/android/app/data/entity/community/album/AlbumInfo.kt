package com.kotlin.android.app.data.entity.community.album

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/23
 * description:相册详情
 */
data class AlbumInfo(
        var commentCount: Long = 0,//评论数
        var coverUserImageId: Long = 0L,//相册封面选用的图片ID，如果为0，则为默认相册封面
        var coverUserImageUrl: String? = "",//相册封面选用的图片Url，如果为0，则为默认相册封面
        var description: String? = "",//相册描述
        var fileName: String = "",
        var id: Long = 0L,
        var memory: Long = 0L,//相册目前占用空间（单位：字节）
        var name: String? = "",//相册名
        var photoNumber: Long = 0L,//相册中的照片数量 缺省为0
        var praiseCount: Long = 0L,//点赞数
        var enterTime: String? = null,//相册创建时间
        var userHomePage: UserHomePage? = null,//用户信息
        var userPraised: Long? = 0L,//当前用户赞：1.点赞 2.点踩 null无(未操作或当前用户未登录)
        var enterTimeLong: Long? = 0L//创建相册时间
) : ProguardRule {
    data class UserHomePage(var userId: Long = 0L,//用户id
                            var nikeName: String? = "",//用户昵称
                            var avatarUrl: String? = "",//头像
                            var authType: Long? = 0L,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
                            var followed: Boolean? = false//当前用户是否关注该用户 true:已关注 false:未关注 null:用户本人
    ) : ProguardRule
}