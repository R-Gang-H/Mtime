package com.kotlin.android.community.card.component.item.bean

import android.text.TextUtils
import androidx.core.util.TimeUtils
import com.kotlin.android.community.card.component.item.adapter.*
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.home.Adv
import com.kotlin.android.app.data.entity.home.HomeRcmdContent
import com.kotlin.android.app.data.entity.home.RcmdContent
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.formatTime
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import okhttp3.internal.concurrent.formatDuration

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 *
 * 社区卡片UI实体
 */
data class CommunityCardItem(
    var id: Long = 0,
    var title: String? = "",
    var content: String? = "",
    var pic: String? = "",
    var userId: Long = 0,
    var userAuthType: Long = 1,
    var userName: String? = null,
    var userProfile: String? = null,
    var isLike: Boolean = false,
    var likeCount: Long = 0,
    var topTag: String? = null, //置顶类型的,推荐语
    var movieId: Long? = null,
    var movieName: String? = null,
    var familyId: Long? = null,
    var familyName: String? = null,
    var isPkType: Boolean = false,
    var type: Long = 0, //内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");VIDEO(5, "视频"), AUDIO(6, "音频")
    var praiseObjType: Long = 0L, //点赞类型
    var isPublish: Boolean = false, // 是否发布
    var mediaTime: String = "",
    var applinkData: String? = ""
) : ProguardRule {
    companion object {

        /**
         * 首页推荐实体转换成UI Binder
         * 包括广告内容
         *  
         * @param content 首页推荐内容实体
         */
        fun converter2Binder(content: HomeRcmdContent): MultiTypeBinder<*> {
            if (content.itemType == 1L) {
                content.content?.let {
                    return converter2Binder(it)
                }
            } else {
                content.adv?.let {
                    return CommunityCardAdBinder(
                        CommunityCardItem(
                            pic = it.img,
                            title = it.title,
                            content = it.subTitle,
                            applinkData = it.appLink
                        )
                    )
                }
            }
            //返回空
            return CommunityCardAdBinder(CommunityCardItem())
        }

        /**
         * 根据社区通用实体转换成UI Binder
         *
         * @param content 推荐内容实体
         * @param isPublish 帖子是否已发布(审核通过)
         */
        fun converter2Binder(
            content: RcmdContent,
            isPublish: Boolean? = true
        ): MultiTypeBinder<*> {
            return converter2Binder(
                rcmdText = content.rcmdText,
                rcmdTop = content.rcmdTop,
                commContent = content.commContent,
                isPublish = isPublish
            )
        }

        /**
         * 根据社区通用实体转换成UI Binder
         * 
         * @param rcmdText 推荐语
         * @param rcmdTop 推荐置顶
         * @param commContent 通用内容实体
         * @param isPublish 帖子是否已发布(审核通过)
         */
        fun converter2Binder(
            rcmdText: String? = null,
            rcmdTop: Boolean = false,
            commContent: CommContent,
            isPublish: Boolean? = true
        ): MultiTypeBinder<*> {
            val item = converter(rcmdText, commContent, isPublish)
            return if (rcmdTop) {
                CommunityCardTopBinder(item)
            } else if (commContent.isPkType()) {
                CommunityCardPkPostBinder(item)
            } else if (commContent.isShortReviewType()) {
                CommunityCardShortReviewBinder(item)
            } else if (TextUtils.isEmpty(item.pic)) {
                CommunityCardLongReviewNoPicBinder(item)
            } else {
                CommunityCardPostOrPicLongReviewOrDailyBinder(item)
            }
        }

        /**
         * 根据社区通用实体转换成UIBean
         *
         * @param rcmdText 推荐语
         * @param commContent 通用内容实体
         * @param isPublish 帖子是否已发布(审核通过)
         */
        private fun converter(
            rcmdText: String? = null,
            commContent: CommContent, 
            isPublish: Boolean? = true
        ): CommunityCardItem {
            val id = if (commContent.contentId != 0L) commContent.contentId else commContent.recId
            return CommunityCardItem(
                id = id,
                title = commContent.title ?: "",
                content = commContent.mixWord ?: "",
                pic = commContent.getImg(0),
                userId = commContent.createUser?.userId ?: 0,
                userAuthType = commContent.createUser?.authType ?: 1,
                userName = commContent.createUser?.nikeName,
                userProfile = commContent.createUser?.avatarUrl,
                isLike = commContent.interactive?.userPraised == 1L,
                likeCount = commContent.interactive?.praiseUpCount ?: 0,
                topTag = rcmdText ?: "",
                movieId = commContent.fcMovie?.id,
                movieName = commContent.fcMovie?.name,
                familyId = commContent.group?.id,
                familyName = commContent.group?.name,
                isPkType = commContent.isPkType(),
                type = commContent.type,
                praiseObjType = commContent.getPraiseObjType(),
                isPublish = isPublish ?: true,
                mediaTime = formatTime(commContent.getMediaTime())
            )
        }
    }

    fun isVideoType() = type == CommContent.TYPE_VIDEO
    
    fun isAudioType() = type == CommContent.TYPE_AUDIO

    fun isVideoOrAudio() = (isVideoType() || isAudioType()) && mediaTime.isNotEmpty()

    fun formatLikeCount(): String {
        return formatCount(likeCount)
    }

    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return userAuthType == 4L
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return userAuthType > 1
    }
}