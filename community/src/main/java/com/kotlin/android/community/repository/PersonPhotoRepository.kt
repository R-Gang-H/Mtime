package com.kotlin.android.community.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.community.ui.person.bean.CommunityPhotoViewBean
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.app.data.entity.CommHasNextList
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import okhttp3.RequestBody

/**
 * @author WangWei
 * @date 2020/9/23
 *
 * 社区-个人主页-图册 数据仓库
 */
class PersonPhotoRepository : BaseRepository() {

    /**
     * 图片列表
     */
    suspend fun loadData(nextStamp: String?, pageSize: Long, userId: Long):
            ApiResult<CommHasNextList<MultiTypeBinder<*>>> {
        return request(
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.items?.forEach {
                    list.add(CommunityPhotoViewBean.converter2Binder(it))
                }
                CommHasNextList(hasNext = it.hasNext, items = list, nextStamp = it.nextStamp)
            },
            api = {
                apiMTime.getAlbumList(userId, nextStamp, pageSize)
            })
    }

}