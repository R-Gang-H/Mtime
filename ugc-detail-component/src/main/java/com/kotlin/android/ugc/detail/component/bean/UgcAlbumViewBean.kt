package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.binder.UgcAlbumItemBinder

/**
 * Created by lushan on 2020/8/6
 * 相册内容
 */
data class UgcAlbumViewBean(var pageView: Long = 0L,//相册浏览量
                            var totalCount: Long = 0,//相册图片总量，
                            var albumList: MutableList<UgcAlbumItemBinder> = mutableListOf(),//相册内容
                            var isLoading: Boolean = false//是否是加载更多中
) : ProguardRule {
    //    没有更多数据
    private fun isNoMoreData(): Boolean = totalCount == albumList.size.toLong()

    //    相册图片是否是空的
    private fun isEmpty(): Boolean = totalCount == 0L

    /**
     * 获取加载更多文案
     */
    fun getMoreContent(): String {
        return when {
            isLoading-> "正在加载中..."
            isEmpty() -> ""
            isNoMoreData() -> ""
            else -> getString(R.string.ugc_detail_load_more)
        }
    }

    /**
     * 是否可以点击加载更多
     */
    fun isCanClickLoadMore():Boolean{
        return  (isLoading || isEmpty() || isNoMoreData()).not()
    }

}