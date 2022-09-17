package com.kotlin.android.community.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.person.PersonMyFriendList
import com.kotlin.android.community.ui.person.bean.MyFriendViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author WangWei
 * @date 2020/9/30
 *
 * 社区-个人主页-我的好友
 */
class PersonMyFriendRepository : BaseRepository() {

    suspend fun loadDataFollow(nextStamp: String?, pageSize: Long, userId: Long):ApiResult<PersonMyFriendList>{
        return request {
            apiMTime.getCommunityPersonFollow(userId, pageSize, nextStamp)
        }
    }
    suspend fun loadDataFan(nextStamp: String?, pageSize: Long, userId: Long):ApiResult<PersonMyFriendList>{
        return request {
            apiMTime.getCommunityPersonFan(userId, pageSize, nextStamp)
        }
    }
    /**
     * 关注、取关用户
     */
    suspend fun followUser(action: Long, userId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.followUser(action, userId) }
    }

}