package com.kotlin.android.community.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/10
 *
 * 社区-精选-数据仓库
 */
class SelectionRepository : BaseRepository() {

    /**
     * 加载推荐数据
     */
    suspend fun loadData(pageIndex: Long, pageSize: Long):
            ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.items?.forEach {item ->
                    list.add(CommunityCardItem.converter2Binder(
                        item.rcmdText,
                        item.rcmdTop,
                        item.commContent))
                }
                CommHasMoreList(hasMore = it.hasNext, list = list)
            },
            api = {
                apiMTime.getCommunityHomeRcmdSelection(pageIndex, pageSize)
            }
        )
    }

//    suspend fun loadData(): ApiResult<List<MultiTypeBinder<*>>> {
//        return ApiResult.Success(getCardBinder())
//    }
//
//    suspend fun loadMoreData(): ApiResult<List<MultiTypeBinder<*>>> {
//        return ApiResult.Success(getCardBinder())
//    }
//
//    private fun getCardBinder(): List<MultiTypeBinder<*>> {
//        return listOf(
//                CommunityCardTopBinder(
//                        CommunityCardItem(
//                                id = 1,
//                                content = "让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切战争的办法让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切到终结一也不切到",
//                                pic = "http://img5.mtime.cn/mg/2020/07/02/150952.61926540.jpg",
//                                userId = 1,
//                                userName = "格兰芬多斯内格兰芬多斯内",
//                                userProfile = "http://img32.mtime.cn/up/2015/10/18/170744.68531851_64X64.jpg",
//                                isLike = true,
//                                likeCount = 9999,
//                                topTag = "时光精选 唐顿庄园",
//                                type = CommContent.TYPE_REVIEW
//                        )
//                ),
//                CommunityCardPostOrPicLongReviewOrDailyBinder(
//                        CommunityCardItem(
//                                id = 1,
//                                content = "让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切战争的办法让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切到终结一也不切到",
//                                pic = "http://img5.mtime.cn/mg/2020/07/02/150952.61926540.jpg",
//                                userId = 1,
//                                userName = "格兰芬多斯内格兰芬多斯内",
//                                userProfile = "http://img32.mtime.cn/up/2015/10/18/170744.68531851_64X64.jpg",
//                                isLike = true,
//                                likeCount = 9999,
//                                movieId = 1,
//                                movieName = "唐顿庄园",
//                                familyId = 1,
//                                familyName = "哈迷小组家族",
//                                type = CommContent.TYPE_POST
//                        )
//                ),
//                CommunityCardPostOrPicLongReviewOrDailyBinder(
//                        CommunityCardItem(
//                                id = 1,
//                                content = "让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切战争的办法让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切到终结一也不切到",
//                                pic = "http://img5.mtime.cn/mg/2020/07/02/150952.61926540.jpg",
//                                userId = 1,
//                                userName = "格兰芬多斯内格兰芬多斯内",
//                                userProfile = "http://img32.mtime.cn/up/2015/10/18/170744.68531851_64X64.jpg",
//                                isLike = true,
//                                likeCount = 9999,
//                                type = CommContent.TYPE_POST
//                        )
//                ),
//                CommunityCardPkPostBinder(
//                        CommunityCardItem(
//                                id = 1,
//                                title = "人类仍然找不人类仍然找不",
//                                content = "",
//                                pic = "http://img5.mtime.cn/mg/2020/07/02/150952.61926540.jpg",
//                                userId = 1,
//                                userName = "格兰芬多斯内格兰芬多斯内",
//                                userProfile = "http://img32.mtime.cn/up/2015/10/18/170744.68531851_64X64.jpg",
//                                isLike = true,
//                                likeCount = 9999,
//                                topTag = "时光精选 唐顿庄园",
//                                type = CommContent.TYPE_POST
//                        )
//                ),
//                CommunityCardShortReviewBinder(
//                        CommunityCardItem(
//                                id = 1,
//                                content = "让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切战争的办法让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切到终结一也不切到",
//                                userId = 1,
//                                userName = "格兰芬多斯内格兰芬多斯内",
//                                userProfile = "http://img32.mtime.cn/up/2015/10/18/170744.68531851_64X64.jpg",
//                                isLike = true,
//                                likeCount = 9999,
//                                type = CommContent.TYPE_REVIEW
//                        )
//                ),
//                CommunityCardLongReviewNoPicBinder(
//                        CommunityCardItem(
//                                id = 1,
//                                title = "人类仍然找不人类仍然找不",
//                                content = "让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切战争的办法让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切让人悲哀的是，到目前为止，人类仍然找不出，也不愿去找到终结一切到终结一也不切到",
//                                userId = 1,
//                                userName = "格兰芬多斯内格兰芬多斯内",
//                                userProfile = "http://img32.mtime.cn/up/2015/10/18/170744.68531851_64X64.jpg",
//                                isLike = true,
//                                likeCount = 9999,
//                                type = CommContent.TYPE_REVIEW
//                        )
//                )
//        )
//    }
}