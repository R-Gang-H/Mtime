package com.kotlin.android.community.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.mtime.ktx.GlobalDimensionExt.getCurrentCityId
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/10
 */
class FollowRepository : BaseRepository() {

    /**
     * 加载社区关注-分页查询动态
     */
    suspend fun loadData(
        pageStamp: Long,
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = { contentList ->
                val list = mutableListOf<MultiTypeBinder<*>>()
                contentList.items?.forEach {
                    it.commContent?.let { post ->
                        list.add(CommunityPostItem.converter2Binder(post))
                    }
                }
                CommHasMoreList(contentList.pageStamp, contentList.hasNext, list)
            },
            api = {
                apiMTime.getCommunityHomeFollowDynamic(
                    pageStamp,
                    pageIndex,
                    pageSize,
                    getCurrentCityId()
                )
            }
        )
    }

}