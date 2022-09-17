package com.kotlin.android.mine.bean

import android.content.Context
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.mine.AccountStatisticsInfo
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/24
 * 描述:
 */
data class AccountStatisticsInfoViewBean(
        var followCount: Long = 0L,             // 关注数
        var fansCount: Long = 0L,               // 粉丝数
        var wantSeeCount:Long = 0L,             // 想看总数
        var hotFilmWantSeeCount:Long = 0L,      // 想看电影中正在热映的电影总数
        var watchedCount:Long = 0L,             // 看过电影总数
        var ratingWaitCount:Long = 0L,          // 看过电影中为评分的电影总数
        var favoriteCount:Long = 0L,            // 收藏数
        var creator: Boolean = false,           // 是否是创作者标签
        var creatorLevelId: String = "",        // 创作中心-等级id
        var creatorLevelName: String = "",      // 创作中心-等级名称
        var creatorAppLogoUrl: String = "",     // 创作中心-app等级图标url
        var contentCount: Long = 0L,            // 已创作内容数
        var sevenDayBrowseCount: Long = 0L,     // 7天浏览数
        var unreadMedals: MutableList<UnreadMedalViewBean> = mutableListOf(), // 创作中心-未读勋章提示列表
        var activityCount: Long = 0L,           // 活动总数量
        var activities: MutableList<MultiTypeBinder<*>> = mutableListOf(),    // 活动列表
): ProguardRule {

    data class UnreadMedalViewBean(
            var unreadCount: Long = 0L,	 // 同类型勋章未读数
            var medalName: String = "",  // 勋章名称
    ): ProguardRule


    companion object {

        private fun convertToUnreadMedalViewBean(unreadMedals: List<AccountStatisticsInfo.UnreadMedal>?)
        : MutableList<UnreadMedalViewBean> {
            val viewBeans: MutableList<UnreadMedalViewBean> = mutableListOf()
            unreadMedals?.let {
                it.map { bean ->
                    viewBeans.add(
                            UnreadMedalViewBean(
                                    unreadCount = bean.unreadCount.orZero(),
                                    medalName = bean.medalName.orEmpty(),
                            )
                    )
                }
            }
            return viewBeans
        }

        /**
         * 转换ViewBean
         */
        fun objectToViewBean(context: Context, bean: AccountStatisticsInfo): AccountStatisticsInfoViewBean {
            return AccountStatisticsInfoViewBean(
                        followCount = bean.myCommunity?.followCount.orZero(),
                        fansCount = bean.myCommunity?.fansCount.orZero(),
                        wantSeeCount = bean.myRecords?.wantSeeCount.orZero(),
                        hotFilmWantSeeCount = bean.myRecords?.hotFilmWantSeeCount.orZero(),
                        watchedCount = bean.myRecords?.watchedCount.orZero(),
                        ratingWaitCount = bean.myRecords?.ratingWaitCount.orZero(),
                        favoriteCount = bean.myRecords?.favoriteCount.orZero(),
                        creator = bean.myRecords?.creator.orFalse(),
                        creatorAppLogoUrl = bean.myRecords?.creatorAppLogoUrl.orEmpty(),
                        unreadMedals = convertToUnreadMedalViewBean(bean.myRecords?.unreadMedals),
                        contentCount = bean.myRecords?.contentCount.orZero(),
                        sevenDayBrowseCount = bean.myRecords?.sevenDayBrowseCount.orZero(),
                        activityCount = bean.myActivity?.activityCount.orZero(),
                        activities = ActivityViewBean.build(context, bean.myActivity?.items, true)
                    )
        }

    }

}
