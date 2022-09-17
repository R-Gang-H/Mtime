package com.kotlin.android.community.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.community.person.WantSeeInfo
import com.kotlin.android.community.ui.person.PERSON_TYPE_HAS_SEEN
import com.kotlin.android.community.ui.person.PERSON_TYPE_WANT_SEE
import com.kotlin.android.community.ui.person.bean.WantSeeHasMoreList
import com.kotlin.android.community.ui.person.bean.WantSeeViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author WangWei
 * @date 2020/10/13
 *
 * 社区-个人主页-想看、看过
 */
class PersonWantSeeRepository : BaseRepository() {

    /**
     * 想看
     */
    suspend fun loadCommunityWantSee(userId: Long, nextStamp: String?, pageSize: Long):
            ApiResult<WantSeeInfo> {
        return request(
                api = {
                    apiMTime.getCommunityWantSee(userId, nextStamp, pageSize)
                })
    }

    /**
     * 看过
     */
    suspend fun loadCommunityHasSeen(userId: Long, nextStamp: String?, pageSize: Long):
            ApiResult<WantSeeInfo> {
        return request(
                api = {
                    apiMTime.getCommunityHasSeen(userId, nextStamp, pageSize)
                })
    }
}