package com.kotlin.android.mine.ui.collection

import android.os.Bundle
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_ARTICLE
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_CINEMA
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_MOVIE
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_PERSON
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_POST
import com.kotlin.android.mine.*
import com.kotlin.android.mine.ui.collection.fragment.CollectionFragment
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.put
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

/**
 * create by lushan on 2020/9/11
 * description:我的收藏页面
 */
class MyCollectionViewModel : BaseViewModel() {
    /**
     * 设置我的收藏tab对应fragment
     */
    fun getPageItem(creator: FragPagerItems): FragPagerItems {
        creator.add(titleRes = R.string.mine_collection_movie, clazz = CollectionFragment::class.java, args = getBundle(COLLECTION_TYPE_MOVIE))
        creator.add(titleRes = R.string.mine_collection_cinema, clazz = CollectionFragment::class.java, args = getBundle(COLLECTION_TYPE_CINEMA))
        creator.add(titleRes = R.string.mine_collection_person, clazz = CollectionFragment::class.java, args = getBundle(COLLECTION_TYPE_PERSON))
        creator.add(titleRes = R.string.mine_collection_article, clazz = CollectionFragment::class.java, args = getBundle(COLLECTION_TYPE_ARTICLE))
        creator.add(titleRes = R.string.mine_collection_post, clazz = CollectionFragment::class.java, args = getBundle(COLLECTION_TYPE_POST))
        return creator
    }

    /**
     * 获取对应bundle
     */
    private fun getBundle(type:Long):Bundle{
        return Bundle().put(KEY_COLLECTION_TYPE,type)
    }

}