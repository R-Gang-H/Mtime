package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.PublishResult
import com.kotlin.android.app.data.entity.community.content.CheckReleasedResult
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.ContentInit
import com.kotlin.android.app.data.entity.community.group.MyGroupList
import com.kotlin.android.app.data.entity.community.publish.Footmarks
import com.kotlin.android.app.data.entity.community.publish.RecommendTypes
import com.kotlin.android.app.data.entity.community.record.RecordId
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 时光社区前台API文档：
 * 时光社区前台相关API接口具体说明
 * http://apidocs.mt-dev.com/community-front-api/index.html
 *
 * Created on 2020/10/9.
 *
 * @author o.s
 */
interface ApiMTimeCommunity {

    companion object {

        /**
         * 社区内容api
         */
        const val COMMUNITY_CONTENT = "/community/content.api"//社区内容api-获取内容
        const val COMMUNITY_EDIT_CONTENT =
            "/community/edit_content.api" // 社区内容api - 未发布内容-保存记录(/record.api)
        const val COMMUNITY_DELETE_CONTENT =
            "/community/delete_content.api"//社区内容api - 删除内容(/delete_content.api)
        const val COMMUNITY_RECORD_ID =
            "/community/record_id/v2.api" // 社区内容api - 未发布内容-获取我的新记录ID(/record_id.api)
        const val COMMUNITY_POST_RECORD =
            "/community/record.api" // 社区内容api - 未发布内容-保存记录(/record.api)
        const val COMMUNITY_RECORD =
            "/community/record.api"// get请求社区内容api-未发布内容-获取记录  post请求社区内容api-未发布内容-保存记录
        const val COMMUNITY_MY_GROUP_CAN_POST_LIST =
            "/community/group/myGroupCanPostList.api"// 社区家族api - 我加入的可以发帖的家族(/group/myGroupCanPostList.api)
        const val COMMUNITY_CONTENT_INIT = "/community/content_init.api"//发布视频，分类内容
        const val COMMUNITY_CONTENT_CHECK_RELEASED =
            "/community/content/check_released.api" // 检查内容是否为发布状态

        const val COMMUNITY_GROUP_FOOTMARKS =
            "/community/group/zhongcaoGroups.api" // 社区家族api - 【2.0】种草家族列表（/group/zhongcaoGroups.api）
        const val COMMUNITY_RCMD_SUB_TYPES =
            "/community/rcmd/sub_types.api" // 内容推荐api - 【2.0】按推荐类型获取子分类（/rcmd/sub_types.api）

        const val COMMUNITY_TRACK = "/track/batch_track.api"//详情日志埋点

    }

    /**
     * 社区内容api - 获取内容(/content.api)
     * GET ("/community/content.api")
     *
     * @param type            Number  内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
     * @param contentId    Number  内容ID
     * @param locationId  Number  locationId  地区ID 可选
     * @param recId     Number      记录id，本人获取副本时使用
     */
    @GET(COMMUNITY_CONTENT)
    suspend fun getCommunityContent(
        @QueryMap params: HashMap<String, Any>
    ): ApiResponse<CommunityContent>

    /**
     * 社区内容api - 获取我的编辑内容(/edit_content.api)
     * POST (/community/edit_content.api)
     *
     * type	        Number  内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
     * contentId	Number  内容ID
     */
    @POST(COMMUNITY_EDIT_CONTENT)
    @FormUrlEncoded
    suspend fun postEditContent(
        @Field("type")
        @ContentType
        type: Long,
        @Field("contentId")
        contentId: Long
    ): ApiResponse<CommunityContent>

    /**
     * 社区内容api - 删除内容(/delete_content.api)
     * POST (/community/delete_content.api)
     *
     * type         Number  内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");VIDEO(5, "视频"), AUDIO(6, "音频")
     * contentId    Number  内容id
     */
    @POST(COMMUNITY_DELETE_CONTENT)
    @FormUrlEncoded
    suspend fun postDeleteContent(
        @Field("type")
        @ContentType
        type: Long,
        @Field("contentId")
        contentId: Long
    ): ApiResponse<CommBizCodeResult>

    /**
     * 社区内容api - 未发布内容-获取我的新记录ID(/record_id/v2.api))
     * POST ("/community/record_id/v2.api)")
     *
     * type	    Number  内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), VIDEO(5, "视频"), AUDIO(6, "音频");
     */
    @POST(COMMUNITY_RECORD_ID)
    @FormUrlEncoded
    suspend fun postCommunityRecordId(
        @Field("type")
        @ContentType
        type: Long
    ): ApiResponse<RecordId>

    /**
     * 社区内容api - 未发布内容-保存记录(/record.api)
     * POST ("/community/record.api")
     *
     * recId	    Number  记录ID 必填 新增调用未发布内容-获取我的新记录ID的API获取，修改获取我的编辑内容API里有recId
     * contentId	Number  内容ID 新记录不填,已发布之后再次保存必填
     * type	        Number  内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), VIDEO(5, "视频"), AUDIO(6, "音频");
     * title	    String  标题
     * author	    String  作者
     * editor	    String  编辑
     * source	    String  来源
     * summary	    String  摘要
     * keywords	    Array   关键词
     * tags	        Array   标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
     * vote	        Object  投票
     *  opts	    Array   选项列表 必填
     *      optId	Number  选项Id 必填
     *      optDesc	String  选项描述 必填
     *  multiple	Boolean 是否多选 必填
     * commentPmsn	Number  评论权限 ALLOW_ALL(1, "允许任何人"), FORBID_ALL(2, "禁止所有人");
     * covers	    Array   封面集合
     *  imageId	    String  图片ID 必填
     *  imageUrl	String  图片URL
     *  imageFormat	String  图片格式 例：gif,jpg,png
     *  imageDesc	String  图片描述
     * images	    Array   图片集合 图集传入，富文本内图集不要传
     *  imageId	    String  图片ID 必填
     *  imageUrl    String  图片URL
     *  imageFormat	String  图片格式 例：gif,jpg,png
     *  imageDesc	String  图片描述
     * videos	    Array   视频集合 目前无用
     *  vSource	    Number  视频来源 必填 MOVIE_VIDEO(1, "电影预告片"), SELF_MEDIA_VIDEO(2, "自媒体"), MEDIA_VIDEO(3, "媒资");
     *  vId	        Number  视频ID 必填
     * audio        Object  音频
     *  aId         Number  音频id 必填
     *  aUrl        String  音频URL 必填
     *  aSec        Number  音频时长 单位秒 必填
     *  fileSize    Number  文件大小，单位字节 必填
     * reObjs	    Array   关联对象集合 目前只有文字使用
     *  roType	    Number  关联对象类型 必填 MOVIE(1, "电影"), PERSON(2, "影人"), ARTICLE(3, "文章");
     *  roId        Number  关联对象ID 必填
     * body	        String  富文本正文 img标签自定义属性 data-mt-fileId:时光图片ID data-mt-format:图片格式，例gif,jpg video标签自定义属性 poster：封面图，可空后端补充 data-video-type:时光视频来源类型1.电影预告片2.自媒体3.媒资 data-video-id:时光视频id 电影自定义标签自定义属性 figure标签上data-mtime-movie-id：时光电影ID
     * groupId	    Number  群组ID
     * fcMovie	    Number  影评电影ID
     * fcPerson	    Number  影评影人ID
     * fcType	    Number  影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
     */
    @POST(COMMUNITY_POST_RECORD)
    suspend fun postCommunityRecord(
        @Body
        body: RequestBody
    ): ApiResponse<CommBizCodeResult>

    /**
     * 社区内容api - 保存内容
     * POST ("/community/content.api")
     * saveAction   Number  保存动作1：草稿 2：发布
     * contentId	Number  内容ID 新记录不填,已发布之后再次保存必填
     * type	        Number  内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), VIDEO(5, "视频"), AUDIO(6, "音频");
     * title	    String  标题
     * author	    String  作者
     * editor	    String  编辑
     * source	    String  来源
     * summary	    String  摘要
     * keywords	    Array   关键词
     * tags	        Array   标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
     * vote	        Object  投票
     *  opts	    Array   选项列表 必填
     *      optId	Number  选项Id 必填
     *      optDesc	String  选项描述 必填
     *  multiple	Boolean 是否多选 必填
     * commentPmsn	Number  评论权限 ALLOW_ALL(1, "允许任何人"), FORBID_ALL(2, "禁止所有人");
     * covers	    Array   封面集合
     *  imageId	    String  图片ID 必填
     *  imageUrl	String  图片URL
     *  imageFormat	String  图片格式 例：gif,jpg,png
     *  imageDesc	String  图片描述
     * images	    Array   图片集合 图集传入，富文本内图集不要传
     *  imageId	    String  图片ID 必填
     *  imageUrl    String  图片URL
     *  imageFormat	String  图片格式 例：gif,jpg,png
     *  imageDesc	String  图片描述
     * video	    Array   视频集合 目前无用
     *  vSource	    Number  视频来源 必填 MOVIE_VIDEO(1, "电影预告片"), SELF_MEDIA_VIDEO(2, "自媒体"), MEDIA_VIDEO(3, "媒资");
     *  vId	        Number  视频ID 必填
     * audio        Object  音频
     *  aId         Number  音频id 必填
     *  aUrl        String  音频URL 必填
     *  aSec        Number  音频时长 单位秒 必填
     *  fileSize    Number  文件大小，单位字节 必填
     * reObjs	    Array   关联对象集合 目前只有文字使用
     *  roType	    Number  关联对象类型 必填 MOVIE(1, "电影"), PERSON(2, "影人"), ARTICLE(3, "文章");
     *  roId        Number  关联对象ID 必填
     * body	        String  富文本正文 img标签自定义属性 data-mt-fileId:时光图片ID data-mt-format:图片格式，例gif,jpg video标签自定义属性 poster：封面图，可空后端补充 data-video-type:时光视频来源类型1.电影预告片2.自媒体3.媒资 data-video-id:时光视频id 电影自定义标签自定义属性 figure标签上data-mtime-movie-id：时光电影ID
     * groupId	    Number  群组ID
     * fcMovie	    Number  影评电影ID
     * fcPerson	    Number  影评影人ID
     * fcType	    Number  影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
     */
    @POST(COMMUNITY_CONTENT)
    suspend fun postCommunityContent(
        @Body
        body: RequestBody
    ): ApiResponse<PublishResult>

    /**
     * 社区内容api - 未发布内容-获取记录(/record.api)
     * GET ("/community/record.api")
     *
     * type	    Number  内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
     * recId	Number  记录ID
     * locationId   Number      地区ID 可选
     */
    @GET(COMMUNITY_RECORD)
    suspend fun getCommunityRecord(
        @Query("locationId") locationId: Long,
        @Query("type")
        @ContentType
        type: Long,
        @Query("recId")
        recId: Long
    ): ApiResponse<CommunityContent>

    /**
     * 社区家族api - 我加入的可以发帖的家族(/group/myGroupCanPostList.api)
     * GET ("/community/group/myGroupCanPostList.api")
     *
     * pageIndex	Number  joinedGroupList的分页参数
     * pageSize	    Number  由于条件和数据库的原因，此处分页可能显示不全，建议pageSize>30,并且使用joinedGroupHasMore 来判断是否有下一页
     */
    @GET(COMMUNITY_MY_GROUP_CAN_POST_LIST)
    suspend fun getCommunityMyFamilyCanPostList(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long
    ): ApiResponse<MyGroupList>


    /**
     * 社区内容api - 保存内容初始化(/content_init.api)
     * POST (/community/content_init.api)
     *
     * type	    Number  内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),
     * include	Array   查询包含 CLASSIFIES("分类"), FC_MOVIE("影评电影"), FC_PERSON("影评影人"), GROUP("家族"),
     * fcMovie	Number  影评电影ID，include需包含FC_MOVIE
     * fcPerson	Number  影评影人ID，include需包含FC_PERSON
     * groupId	Number  家族ID，include需包含GROUP
     */
    @POST(COMMUNITY_CONTENT_INIT)
    suspend fun postContentInit(@Body body: RequestBody): ApiResponse<ContentInit>

    /**
     * 检查内容是否为发布状态
     * @param type          Number  内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频")
     * @param contentId    Number  内容ID
     */
    @GET(COMMUNITY_CONTENT_CHECK_RELEASED)
    suspend fun checkReleased(
        @Query("type") type: Long,
        @Query("contentId") contentId: Long
    ): ApiResponse<CheckReleasedResult>

    /**
     *  社区家族api - 【2.0】种草家族列表
     *  GET (/group/zhongcaoGroups.api)
     */
    @GET(COMMUNITY_GROUP_FOOTMARKS)
    suspend fun getGroupFootmarks(): ApiResponse<Footmarks>

    /**
     *  内容推荐api - 【2.0】按推荐类型获取子分类
     *  GET （/rcmd/sub_types.api）
     *
     *  rcmdType    推荐类型：7种草内容推荐，9找家族推荐，10片单推荐
     */
    @GET(COMMUNITY_RCMD_SUB_TYPES)
    suspend fun getGroupSubTypes(
        @Query("rcmdType") rcmdType: Long
    ): ApiResponse<RecommendTypes>

    /**
     * 埋点api - 批量日志埋点(/batch_track.api)
     *  tracks  Array   批量埋点
     *      mark:String     埋点标识 必填 JOURNAL_DETAIL_VIEW("日志详情页浏览"), POST_DETAIL_VIEW("帖子详情页浏览"),
     *                      LONG_FILM_COMMENT_DETAIL_VIEW("长影评详情页浏览"), SHORT_FILM_COMMENT_DETAIL_VIEW("短影评详情页浏览"),
     *                      ARTICLE_DETAIL_VIEW("文章详情页浏览"), VIDEO_DETAIL_VIEW("视频详情页浏览"), AUDIO_DETAIL_VIEW("音频详情页浏览"),
     *      objId:String    对象id
     *      objCreator:Long   对象创作者用户ID
     *      trackTs:Long        埋点时间 Unix时间戳:毫秒 必填
     *
     */
    @POST(COMMUNITY_TRACK)
    suspend fun submitTrack(@Body body:RequestBody):ApiResponse<Any>

}