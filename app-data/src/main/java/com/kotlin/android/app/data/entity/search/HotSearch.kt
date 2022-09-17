package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/31
 *
 * 搜索热门整合接口
 */
data class HotSearch(
    var hotMovies: List<HotSearchItem>?,
    var hotPersons: List<HotSearchItem>?,
    var hotGroups: List<HotSearchItem>?,
    var hotFcRcmds: List<CommContent>?
): ProguardRule

data class HotSearchItem(
        var type: Long?, //类型 1：电影 2：影人 3：文章 4：群组
        var relateId: Long?,
        var name: String?,
        var rating: String?,
        var compare: Long? //排名对比(之前排名-当前排名) <0:下降 =0：持平 >0:上升 null：没有
): ProguardRule
