package com.kotlin.android.publish.component.ui.editor

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommContentList
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.PublishResult
import com.kotlin.android.app.data.entity.common.StatusResult
import com.kotlin.android.app.data.entity.community.content.CheckReleasedResult
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.group.GroupSectionList
import com.kotlin.android.app.data.entity.community.publish.Footmarks
import com.kotlin.android.app.data.entity.community.publish.Group
import com.kotlin.android.app.data.entity.community.publish.RecommendTypes
import com.kotlin.android.app.data.entity.community.record.PostContent
import com.kotlin.android.app.data.entity.movie.LatestComment
import com.kotlin.android.app.data.entity.upload.ApplyUpload
import com.kotlin.android.publish.component.repo.EditorRepository

/**
 * 发布社区内容：
 *
 * Created on 2020/10/9.
 *
 * @author o.s
 */
class EditorViewModel : BaseViewModel() {

    private val repo by lazy { EditorRepository() }

    private val contentUIModel by lazy { BaseUIModel<CommunityContent>() }
    private val postContentUIModel by lazy { BaseUIModel<PublishResult>() }
    private val movieRatingUIModel by lazy { BaseUIModel<StatusResult>() }
    private val latestCommentUIModel by lazy { BaseUIModel<LatestComment>() }
    private val deleteCommentUIModel by lazy { BaseUIModel<CommBizCodeResult>() }
    private val familyListUIModel by lazy { BaseUIModel<ArrayList<Group>>() }
    private val footmarksUIModel by lazy { BaseUIModel<Footmarks>() }
    private val recommendTypesUIModel by lazy { BaseUIModel<RecommendTypes>() }
    private val sectionListUIModel by lazy { BaseUIModel<GroupSectionList>() }
    private val applyUploadUIModel by lazy { BaseUIModel<ApplyUpload>() } //发起上传视频
    private val completeUIModel by lazy { BaseUIModel<CommBizCodeResult>() } //上传完成
    private val checkReleasedUIModel by lazy { BaseUIModel<CheckReleasedResult>() }
    private val relationArticlesUIModel by lazy { BaseUIModel<CommContentList>() }
    private val movieUIModel by lazy { BaseUIModel<String>() }

    val contentUIState = contentUIModel.uiState
    val postContentUIState = postContentUIModel.uiState
    val movieRatingUIState = movieRatingUIModel.uiState
    val latestCommentUIState = latestCommentUIModel.uiState
    val deleteCommentUIState = deleteCommentUIModel.uiState
    val familyListUIState = familyListUIModel.uiState
    val footmarksUIState = footmarksUIModel.uiState
    val recommendTypesUIState = recommendTypesUIModel.uiState
    val sectionListUIState = sectionListUIModel.uiState
    val applyUploadUIState = applyUploadUIModel.uiState
    val completeUIState = completeUIModel.uiState
    val checkReleasedUIState = checkReleasedUIModel.uiState
    val relationArticlesUIState = relationArticlesUIModel.uiState
    val movieUIState = movieUIModel.uiState

    fun getContent(
        @ContentType type: Long,
        contentId: Long,
        recId: Long?
    ) {
        call(
            uiModel = contentUIModel,
        ) {
            repo.getContent(
                type = type,
                contentId = contentId,
                recId = recId
            )
        }
    }

    fun publishContent(content: PostContent) {
        call(
            uiModel = postContentUIModel,
        ) {
            repo.postContent(content)
        }
    }

    fun movieRating(
        movieId: Long,
        rating: Double,
        subRatingDesc: String
    ) {
        call(
            uiModel = movieRatingUIModel
        ) {
            repo.getMovieRating(movieId, rating, subRatingDesc)
        }
    }

    fun getMovieRating(movieId: Long) {
        call(
            uiModel = latestCommentUIModel
        ) {
            repo.getMovieLatestComment(movieId)
        }
    }

    fun postDeleteContent(@ContentType type: Long, contentId: Long) {
        call(
            uiModel = deleteCommentUIModel
        ) {
            repo.postDeleteContent(type, contentId)
        }
    }

    /**
     * 检查内容是否为发布状态
     */
    fun checkReleased(
        type: Long = CommConstant.CHECK_RELEASED_CONTENT_TYPE_ARTICLE,
        articleId: Long
    ) {
        call(
            uiModel = checkReleasedUIModel,
        ) {
            repo.checkReleased(
                type = type,
                contentId = articleId
            )
        }
    }

    /**
     * 加载家族列表
     */
    fun loadFamilyList() {
        call(
            uiModel = familyListUIModel,
        ) {
            repo.loadFamilyList()
        }
    }

    /**
     * 获取家族分区列表
     */
    fun getGroupSectionList(groupId: Long) {
        call(
            uiModel = sectionListUIModel
        ) {
            repo.getGroupSectionList(groupId = groupId)
        }
    }

    /**
     * 种草家族列表
     */
    fun getGroupFootmarks() {
        call(
            uiModel = footmarksUIModel
        ) {
            repo.getGroupFootmarks()
        }
    }

    /**
     * 按推荐类型获取子分类
     */
    fun getGroupSubTypes() {
        call(
            uiModel = recommendTypesUIModel
        ) {
            repo.getGroupSubTypes()
        }
    }

    /**
     * 发起视频上传获取一次性token
     */
    fun applyUpload(filName: String) {
        call(
            uiModel = applyUploadUIModel
        ) {
            repo.applyUpload(fileName = filName)
        }
    }

    /**
     * 视频在腾讯云点播上传完成后同步给服务器
     */
    fun completeUpload(
        videoId: Long,
        mediaId: String,
        mediaUrl: String
    ) {
        call(uiModel = completeUIModel) {
            repo.completeUpload(
                videoId = videoId,
                mediaId = mediaId,
                mediaUrl = mediaUrl
            )
        }
    }

    /**
     * 获取关联文章列表
     */
    fun getRelationArticles(
        contentId: Long? = null,
        recId: Long? = null,
    ) {
        call(
            uiModel = relationArticlesUIModel,
            hasMore = {
                it.hasNext
            }
        ) {
            repo.getRelationArticles(
                contentId = contentId,
                recId = recId,
                pageIndex = relationArticlesUIModel.pageIndex,
                pageSize = relationArticlesUIModel.pageSize
            )
        }
    }

    /**
     * 获取电影名称
     */
    fun getMovieName(movieId: Long) {
        call(
            uiModel = movieUIModel,
        ) {
            repo.getMovieName(movieId = movieId)
        }
    }
}