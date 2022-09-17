package com.kotlin.android.player.bean

import com.kk.taurus.playerbase.entity.DataSource

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/15
 *
 * 时光视频实体
 */
class MTimeVideoData(
        data: String = "",
        var videoId: Long,
        var source: Long,
        var fileSize: Long = 0L
) : DataSource(data)