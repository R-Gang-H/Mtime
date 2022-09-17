package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/6/1
 *
 * 接口定义：http://wiki.inc-mtime.com/pages/viewpage.action?pageId=286360275
 */

data class SearchSuggest(
    var suggestions: List<SearchSuggestItem>? = emptyList()
) : ProguardRule

data class SearchSuggestItem(
    var movieId: Long?,
    var type: Long?, //搜索类型 1 表示影片、2 表示影院、3影人 4 文章
    var contentType: String?, //"影片",    //内容类型
    var movieType: String?, //"动作",      //影片类型
    var isFilter: Boolean?, //是否过滤影片封面
    var titleCn: String?,
    var titleEn: String?,
    var locationName: String?,
    var year: String?,
    var director: String?,
    var cover: String?,
    var releaseStatus: Long?, //地区影片的上映状态 0表示下线、1表示正在热映、 2 表示即将上映。 使用该字段必须传城市ID参数
    var canPlay: Long?, //是否可播放  0不可播放  1可播放
    var saleType: Long?, //是否可售票 0不可售票 1可预售 2可售票
    var rating: Double?,

    var cinemaId: Long?, //影院ID
    var name: String?,
    var cityName: String?,
    var district: String?, //城区
    var business: String?, //商圈
    var address: String?, //地址
    var distance: Double?, //当前位置到该影院的距离 单位 公里 传入 longitude 和 latitude 两个参数 而且 影院已采集 经度 纬度 显示 否则 显示 0.0
    var featureInfos: String?, //"停车场,有Wi-Fi,4D厅,4K放映厅"      //影设备设施

    var personId: Long?, //影人Id
    var sex: String?, //性别
    var birthLocation: String?, //出生地
    var profession: String?, //职业
    var birth: String?, //出生日期
    var loveDeep: Double?, //喜爱度
    var personMovies: List<PersonMovie>? = null,

    var href: String? = "", //文章链接
    var articleId: Long? = 0, //文章ID
    var articleTitle: String? = "", //文章标题
    var articleSummary: String? = "", //文章简介
    var createTime: String? = "", //时间
    var keyWord: List<String>? = emptyList(), //["院线","万达"], //关键词
    var commentCount: Long? = 0, //评论数

    var fansCount: Long? = 0, //用户的粉丝数
    var headUrl: String? = "", //头像地址
    var isAuth: Long? = 0, //是否经过认证
    var nickName: String? = "", //昵称
    var sign: String? = "", //签名
    var userId: Long? = 0,
    var authType: Long? = 0, //认证分类 1、个人 2、影评人 3、电影人 4、机构
    var isFocus: Long? = 0, //是否关注 1、关注 0、未关注

    var filmCommentId: Long? = 0, //影评id
    var filmCommentTitle: String? = "", //影评标题
    var likeDown: Long? = 0, //点踩数
//    var likeUp: Long? = 0, //点赞数（与视频的是否点赞字段重名，暂时注释此字段）

    var postId: Long? = 0, //帖子ID
    var postTitle: String? = "", //帖子标题

    var content: String? = "", //日志文本
    var logId: Long? = 0, //日志ID
    var logTitle: String? = "", //日志标题

    var familyId: Long? = 0, //家族ID
    var imageUrl: String? = "", //家族图片
    var memberNum: Long? = 0, //家族成员数
    var summary: String? = "", //简介

    var id: Long? = 0, //片单Id
    var title: String? = "", //名称
    var authorImg: String? = "", //发布人头像
    var authorId: Long? = 0, //发布人id
    var authorName: String? = "", //发布人昵称
    var films: String? = "", //影片列表
    var collectNum: Long? = 0, //收藏数
    var authTag: Long? = 0, //认证标识

    //视频类型
    var likeNum: Long? = 0, //点赞数
    var commentNum: Long? = 0, //评论数
//    var isLikeUp: Boolean? = false //是否点赞

    //播客(音频内容),字段基本与视频类型相同

) : ProguardRule
