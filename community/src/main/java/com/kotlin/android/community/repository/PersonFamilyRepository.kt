package com.kotlin.android.community.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.community.ui.person.bean.PersonFamilyViewBean
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import okhttp3.RequestBody

/**
 * @author WangWei
 * @date 2020/9/23
 *
 * 社区-个人主页-家族 数据仓库
 */
class PersonFamilyRepository : BaseRepository() {

    /**
     * 加载家族内容
     */
    suspend fun loadData(pageIndex: Long, pageSize: Long, userId: Long):
            ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.managedGroupList.forEach {
                    list.add(PersonFamilyViewBean.converter2Binder(it))
                }
                it.joinedGroupList.forEach {
                    list.add(PersonFamilyViewBean.converter2Binder(it))
                }

                CommHasMoreList(hasMore = it.joinedGroupHasMore, list = list)
            },
            api = {
                apiMTime.getCommunityPersonFamily(userId, pageIndex, pageSize)
            })
    }

    /**
     * 获取请求体
     */
    private fun getRequestBody(
        pageIndex: Long,
        pageSize: Long,
        type: Long,
        userId: Long
    ): RequestBody {
        val params = arrayMapOf<String, Any>(
            "userId" to userId,
            "type" to type,
            "pageIndex" to pageIndex,
            "pageIndex" to pageIndex,
            "pageSize" to pageSize
        )
        return getRequestBody(params)
    }
}