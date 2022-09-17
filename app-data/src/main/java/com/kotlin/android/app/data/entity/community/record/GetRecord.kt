package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.annotation.CommentPermission
import com.kotlin.android.app.data.annotation.CommentType
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.ProguardRule

/**
 * 社区内容api - 未发布内容-获取我的记录(/record.api)
 * GET
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class GetRecord(
        var title: String? = null, // 标题
        var author: String? = null, // 作者
        var editor: String? = null, // 编辑
        var source: String? = null, // 来源
        var summary: String? = null, // 摘要
        @ContentType var type: Long = 0, // 内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
        var recId: Long = 0, // 记录ID 必填
//        var fcPerson: Long = 0, // 影评影人ID
//        var fcMovie: Long = 0, // 影评电影ID
        var fcPerson: FcPerson, // 影评影人
        var fcMovie: FcMovie, // 影评电影

        @CommentType var fcType: Long = 0, // 影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
        @CommentPermission var commentPmsn: Long = 0, // 评论权限 ALLOW_ALL(1, "允许任何人"), FORBID_ALL(2, "禁止所有人");
        var body: String? = null, // 富文本正文
        var keywords: List<String>? = null, // 关键词
        var tags: List<Int>? = null, // 标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
        var vote: Vote? = null, // 投票
        var images: List<Image>? = null, // 图片集合 图集传入，富文本内图集不要传
        var videos: List<Videos>? = null, // 视频集合 目前无用
        var reObjs: List<ReObjs>? = null, // 关联对象集合 目前只有电影影人返回 关联文章请调用单独api获取
        var covers: List<Image>? = null, // 封面集合

        var group: Group,
        var createUser: CreateUser, // 创建人
        var userCreateTime: UserCreateTime, // 用户创建时间
        var fcRating: String, // 影评发表者评分

) : ProguardRule {
    data class UserCreateTime(
            var show: String,
            var stamp: Int
    ) : ProguardRule


    data class ReObjs(
            var roPerson: FcPerson,
            var roMovie: FcMovie
    ) : ProguardRule

}