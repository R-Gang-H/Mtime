package com.kotlin.android.live.component.ui.adapter

import android.view.View
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.live.LiveNews
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ItemLiveDetailNewsBinding

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class LiveDetailNewsItemBinder(val bean: LiveNews) : MultiTypeBinder<ItemLiveDetailNewsBinding>() {

    private val mUgcProvider =
            getProvider(IUgcProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_live_detail_news
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is LiveDetailNewsItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemLiveDetailNewsBinding, position: Int) {
    }

    override fun onClick(view: View) {
        when(bean.contentType) {
            LiveNews.CONTENT_TYPE_ARTICLE -> {
                mUgcProvider?.launchDetail(bean.contentId, CommConstant.PRAISE_OBJ_TYPE_ARTICLE)
            }
            LiveNews.CONTENT_TYPE_POST -> {
                mUgcProvider?.launchDetail(bean.contentId, CommConstant.PRAISE_OBJ_TYPE_POST)
            }
            else -> {
            }
        }
    }
}