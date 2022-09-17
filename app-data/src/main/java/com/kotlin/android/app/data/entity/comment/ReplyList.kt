package com.kotlin.android.app.data.entity.comment

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/1
 * description:评论回复列表
 */
data class ReplyList(
    var hasNext: Boolean = false,//是否有下页
    var items: List<Item>? = mutableListOf(),//当前页记录
    var pageIndex: Long = 0L,
    var pageSize: Long = 0L,
    var totalCount: Long = 0L//总条数
): ProguardRule {
    data class Item(
            var body: String? = "",//富文本正文
            var bodyWords:String? = "",//兼容prd上的老数据，去掉html标签
            var commentId: Long = 0L,//评论id
            var createUser: CommentDetail.CreateUser? = null,//创建人
            var images: List<CommContent.Image>? = mutableListOf(),//回复图片
            var interactive: CommentDetail.Interactive? = null,//交互数据
            var objId: Long = 0L,//评论主体对象ID
            var objType: Long = 0L,//评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
            var reReplyId: Long? = 0L,//被回复回复Id
            var reUser: CommentDetail.CreateUser? = null,//被回复用户
            var replyId: Long = 0L,//回复id
            var userCreateTime: CommContent.UserCreateTime? = null//用户创建时间
    ): ProguardRule {
        fun getFirstPic():String{
            return if (images?.isNotEmpty() == true){
                images?.get(0)?.imageUrl.orEmpty()
            }else{
                ""
            }
        }
    }
}





