package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.annotation.CommentType
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.annotation.CommentPermission

/**
 * 社区内容api - 【废弃】未发布内容-保存记录(/record.api)
 * 社区内容api - 保存内容(/content.api) 注意：POST型
 * POST
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class PostRecord(
        var saveAction: Long = 0L,//保存动作1：草稿 2：发布
        var title: String? = null, // 标题
        var author: String? = null, // 作者
        var editor: String? = null, // 编辑
        var source: String? = null, // 来源
        var summary: String? = null, // 摘要
        @ContentType var type: Long = 0, // 内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
        var recId: Long = 0, // 记录ID 必填 新增调用未发布内容-获取我的新记录ID的API获取，修改获取我的编辑内容API里有recId
        var contentId: Long? = null, // 内容ID 新记录不填,已发布之后再次保存必填
        var groupId: Long? = null, // 群组ID
        var fcPerson: Long? = null, // 影评影人ID
        var fcMovie: Long? = null, // 影评电影ID
        var classifies: MutableList<Long>? = null,//分类
        @CommentType var fcType: Long? = null, // 影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
        @CommentPermission var commentPmsn: Long = 0, // 评论权限 ALLOW_ALL(1, "允许任何人"), FORBID_ALL(2, "禁止所有人");
        var body: String? = null, // 富文本正文 img标签自定义属性 data-mt-fileId:时光图片ID data-mt-format:图片格式，例gif,jpg video标签自定义属性 poster：封面图，可空后端补充 data-video-type:时光视频来源类型1.电影预告片2.自媒体3.媒资 data-video-id:时光视频id 电影自定义标签自定义属性 figure标签上data-mtime-movie-id：时光电影ID
        var keywords: List<String>? = null, // 关键词
        var tags: List<Long>? = null, // 标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
        var vote: Vote? = null, // 投票/PK?
        var images: List<Image>? = null, // 图片集合 图集传入，富文本内图集不要传
        var videos: List<Videos>? = null, // 视频集合 目前无用
        var reObjs: List<ReObjs>? = null, // 关联对象集合 目前只有文字使用
        var covers: List<Image>? = null // 封面集合
) : ProguardRule