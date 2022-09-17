package com.kotlin.android.share

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * 分享平台：微信，朋友圈，微博，QQ，QZone，...
 * 更多操作：置顶，加精，删除，投诉，复制链接，生成海报，...
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
enum class SharePlatform(@StringRes val title: Int, @DrawableRes val icon: Int) {
    WE_CHAT(R.string.we_chat, R.mipmap.ic_wechat),
    WE_CHAT_TIMELINE(R.string.we_chat_timeline, R.mipmap.ic_wechat_timeline),
    WEI_BO(R.string.wei_bo, R.mipmap.ic_sina),
    QQ(R.string.qq, R.mipmap.ic_qq),
//    Q_ZONE(R.string.q_zone, R.mipmap.ic_wechat),
    TOP(R.string.top, R.mipmap.ic_top),
    TOP_CANCEL(R.string.top_cancel, R.mipmap.ic_top),
    FINE(R.string.fine, R.mipmap.ic_fine),
    FINE_CANCEL(R.string.fine_cancel, R.mipmap.ic_fine),
    DELETE(R.string.delete, R.mipmap.ic_delete),
    REPORT(R.string.report, R.mipmap.ic_report),
    COPY_LINK(R.string.copy_link, R.mipmap.ic_link),
    POSTER(R.string.generate_poster, R.mipmap.ic_poster),
//    ADD_POINTS(R.string.add_points, R.mipmap.ic_wechat),
    EDIT(R.string.edit,R.drawable.ic_bianji)
}