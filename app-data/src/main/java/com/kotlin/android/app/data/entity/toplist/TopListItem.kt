package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
- * @date 2020/8/17
- * @desc 榜单元素（影片，影人等）
+ * @date 2020/9/3
+ * @desc 类描述
 */
data class TopListItem(
    var listId: Long ?= 0,          // 榜单ID
    var ordinal: Long ?= 0,         // 序号（如果序号为升序，排名为倒序，说明榜单是倒序展示的）
    var rank: Long ?= 0,            // 排名（前端展示用rank）
    var itemId: String ?= "",       // 榜单元素ID（影片/电视剧/影人ID）
    var itemType: Long ?= 0,        // 榜单元素类型 1电影 2电视剧 3影人
    var title: String ?= "",        // 榜单元素标题
    var coverImg: String ?= "",     // 封面图
    var description: String ?= "",  // 描述
    var movieInfo: TopListMovieInfo ?= null,    // 影片信息实体（itemType=1电影 2电视剧时，有这个实体信息）
    var personInfo: TopListPersonInfo ?= null,  // 影人信息实体（itemType=3影人，有这个实体信息）

        // 自定义
    var expanded: Boolean ?= false,     // （评语）是否已展开
): ProguardRule
