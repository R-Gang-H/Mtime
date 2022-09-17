package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/17
 * des:
 **/
data class UgcMediaViewBean(
    var bgImg: String = "",//背景图
    var videoShowType: Long = 0L,//视频展示类型 1.横版 2.竖版
    var videoBean: VideoPlayList? = null,//视频信息
    var mediaInfoBean: UgcMediaInfoViewBean? = null,//用户基础信息
    var commonBar: UgcCommonBarBean? = null,//底部，点赞、点踩，是否可以评论等信息
    var audioViewBean: UgcAudioViewBean? = null,//音频内容
    var ugcLinkBinderList:MutableList<MultiTypeBinder<*>> = mutableListOf()//关联内容binderList
) : ProguardRule{
    companion object{
        const val VIDEO_TYPE_LAND = 1L//横版
        const val VIDEO_TYPE_PORTRAIT = 2L//竖版
    }
}