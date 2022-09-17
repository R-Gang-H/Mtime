package com.kotlin.android.search.newcomponent.provider

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.router.RouterManager
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.router.ext.put
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.search.newcomponent.Search.KEY_SEARCH_DATA_GROUP
import com.kotlin.android.search.newcomponent.Search.PUBLISH_SEARCH_BUNDLE_KEY_FROM
import com.kotlin.android.search.newcomponent.Search.PUBLISH_SEARCH_BUNDLE_KEY_TYPE

/**
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_SEARCH)
class SearchProvider : ISearchProvider {

    override fun startSearchActivity(context: Context?) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Search.PAGE_SEARCH_ACTIVITY,
                context = context
        )
//        context?.startActivity(SearchActivity::class.java)
    }

    /**
     * 发布组件-搜索影片/影人
     */
    override fun startPublishSearchActivity(activity: Activity?, searchType: Long, from: Long) {
        activity?.apply {
            val bundle = Bundle()
                    .put(PUBLISH_SEARCH_BUNDLE_KEY_TYPE, searchType)
                    .put(PUBLISH_SEARCH_BUNDLE_KEY_FROM, from)
            RouterManager.instance.navigation(
                    context = this,
                    bundle = bundle,
                    path = RouterActivityPath.Search.PAGE_PUBLISH_SEARCH_ACTIVITY,
                    requestCode = Search.SEARCH_REQUEST_CODE
            )
        }
    }

    override fun startSearchGroupActivity(activity: Activity?, groupId:Long?) {
        activity?.apply {
            val bundle = Bundle().put(KEY_SEARCH_DATA_GROUP, groupId)
            RouterManager.instance.navigation(
                bundle = bundle,
                path = RouterActivityPath.Search.PAGE_SEARCH_POST_GROUP_ACTIVITY)
        }
    }

    override fun init(context: Context?) {

    }
}