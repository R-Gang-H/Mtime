package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/17
 * @desc 榜单详情
 */
data class TopListInfo(
    var id: Long ?= 0,              // 榜单ID
    var type: Long ?= 0,            // 类型 1电影 2电视剧 3影人
    var title:	String ?= "",       // 标题
    var subTitle: String ?= "",     // 副标题
    var year: Long ?= 0,            // 年度
    var description: String ?= "",       // 介绍
    var coverImg: String ?= "",     // 封面图
    var publishTime: Long ?= 0,     // 发布时间
    var enableComment: Boolean ?= false,    // 是否允许评论
    var enableCopyright: Boolean ?= false,  // 是否允许显示版权信息
    var editUserId: Long ?= 0,              // 编辑用户ID
    var editUserName: String ?= "",         // 编辑用户姓名
    var items: List<TopListItem> ?= null,   // 榜单元素列表（影片，影人等）。没有展示需要则为空
    var itemsTotalCount: Long ?= 0          // items总条数

): ProguardRule {

    /**
     * 获取指定序号的item名称
     */
    fun itemNameByIndex(index: Int): String {
        var size = items?.size?:0
        if(index > size - 1) {
            return ""
        }
        var name: String ?= ""
        items?.let {
            name = if(type == TYPE_PERSON) it[index].personInfo?.personName else it[index].movieInfo?.movieName
            name?.let {
                var rank = index + 1
                return "$rank.$name"  // 格式:  1.名称
            }
        }
        return name?:""
    }

    /**
     * 获取指定序号的item显示评分
     */
    fun itemShowScoreByIndex(index: Int): String {
        var size = items?.size?:0
        if(index > size - 1) {
            return ""
        }
        var showScore: String ?= ""
        items?.let {
            showScore = if(type == TYPE_PERSON) it[index].personInfo?.score else it[index].movieInfo?.showScore
        }
        return showScore?:""
    }

    /**
     * 指定序号的item是否有数据
     */
    fun hasItemByIndex(index: Int): Boolean {
        var size = items?.size?:0
        return index <= size - 1
    }

    /**
     * 获取指定序号的item
     */
    fun getItemByIndex(index: Int): TopListItem?  {
        var size = items?.size?:0
        if(index <= size - 1) {
            items?.let {
                return it[index]
            }
        }
        return null
    }

    /**
     * 获取榜单封面图，如果为空，则获取第一个item的封面图
     */
    fun getCoverImgOrFristItemImg(): String {
        var url = ""
        coverImg?.let {
            url = it
        }
        if (url.isEmpty()) {
            items?.let {
                if(it.isNotEmpty()) {
                    if (type == TYPE_PERSON) {
                        it[0].personInfo?.img?.let { img ->
                            url = img
                        }
                    } else {
                        it[0].movieInfo?.img?.let {img ->
                            url = img
                        }
                    }

                }
            }
        }
        return url
    }

    companion object {
        val TYPE_MOVIE = 1L
        val TYPE_TV = 2L
        val TYPE_PERSON = 3L
    }
}