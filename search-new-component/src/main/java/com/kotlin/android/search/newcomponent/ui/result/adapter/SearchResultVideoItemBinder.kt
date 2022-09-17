package com.kotlin.android.search.newcomponent.ui.result.adapter

import android.view.View
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.mtime.ktx.setUserAuthType
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultVideoBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.VideoViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/30
 * 描述: 搜索结果_视频ItemBinder
 */
class SearchResultVideoItemBinder(
        val keyword: String,
        val viewBean : VideoViewBean,
        private val isFirstItem: Boolean? = false,
        val firstMarginTop: Int? = 0,               // 第一个item marginTop 单位:px
): MultiTypeBinder<ItemSearchResultVideoBinding>() {

    override fun layoutId(): Int = R.layout.item_search_result_video

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultVideoItemBinder && other.viewBean == viewBean
    }

    override fun onBindViewHolder(binding: ItemSearchResultVideoBinding, position: Int) {
        binding.apply {
            // position在全部搜索中是整个列表的位置，不是模块列表的位置，所以无法用position判断第1个
            firstMarginTop?.let {
                root.marginTop = if (isFirstItem.orFalse()) it else 0
            }
            // 搜索关键词标红
            videoTitleTv.convertToHtml(
                    title = viewBean.title,
                    color = SearchResultConstant.MATCH_KEYWORD_COLOR,
                    keyword = keyword
            )
            // 用户认证类型
            videoAuthorAuthTypeIv.setUserAuthType(viewBean.authTag)
        }
    }

    override fun onClick(view: View) {
        // 跳转视频详情页
        getProvider(IUgcProvider::class.java) {
            launchDetail(
                    contentId = viewBean.id,
                    type = CONTENT_TYPE_VIDEO,
                    recId = 0L,
                    needToComment = false,
            )
        }
    }
}