package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/4/13
 *
 */
data class CmdHuanxin(
    var cmdCode: Int,
    var data: NotifyCmd?,
) : ProguardRule {
    //消息通知
    data class NotifyCmd(
        //评论数
        var commentReply: Long = 0L,
        //点赞数
        var praise: Long = 0L,
        //新增粉丝数
        var userFollow: Long = 0L,
        //上映提醒数
        var movieRelease: Long = 0L,
        //上映提醒影片名
        var movieName: String?
    ) : ProguardRule

    fun totalCount(): Long {
        if (this.data == null) {
            return 0L
        }

        val notify: NotifyCmd = this.data!!
        return notify.commentReply + notify.praise + notify.userFollow + notify.movieRelease
    }
}