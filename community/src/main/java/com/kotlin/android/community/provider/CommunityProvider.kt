package com.kotlin.android.community.provider

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.community.ui.person.KEY_TYPE
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put

/**
 * @author created by WangWei
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_COMMUNITY)
class CommunityProvider : ICommunityPersonProvider {
    private var mContext: Context? = null

    override fun init(context: Context?) {
        mContext = context
    }

    override fun startPerson(userId: Long, type: Long?) {
        val bundle = Bundle()
        bundle.put(KEY_USER_ID, userId)
        bundle.put(KEY_TYPE, type)
        RouterManager.instance.navigation(RouterActivityPath.Community.PAGER_PERSON, bundle)
//        RouterManager.instance.navigation(RouterActivityPath.Community.PAGER_PERSON_COLLECTION, bundle)
    }

    override fun startTimeLinePage() {
        val bundle = Bundle()
        RouterManager.instance.navigation(RouterActivityPath.Community.PAGER_TIME_LINE, bundle)
    }

    override fun startMyFriend(userId: Long, type: Long, isPublish: Boolean?, activity: Activity?) {
        if (activity != null) {//此时需要进行登录之后跳转指定页面
            val bundle = Bundle()
            bundle.put(KEY_USER_ID, userId)
            bundle.put(KEY_TYPE, type)
            RouterManager.instance.navigation(
                path = RouterActivityPath.Community.PAGER_FRIEND,
                bundle = bundle,
                context = activity
            )
        } else {
            val bundle = Bundle()
            bundle.put(KEY_USER_ID, userId)
            bundle.put(KEY_TYPE, type)
            RouterManager.instance.navigation(RouterActivityPath.Community.PAGER_FRIEND, bundle)
        }
    }

    override fun startPersonCollection(userId: Long, type: Long?) {
        val bundle = Bundle()
        bundle.put(KEY_USER_ID, userId)
        bundle.put(KEY_TYPE, type)
        RouterManager.instance.navigation(
            RouterActivityPath.Community.PAGER_PERSON_COLLECTION,
            bundle
        )
    }
}