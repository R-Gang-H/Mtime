package com.kotlin.android.home.ui.tashuo

import android.graphics.Rect
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.callParallel
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.entity.CommHasNextList
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.data.entity.home.tashuo.TaShuoRcmdList
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.home.R
import com.kotlin.android.home.repository.TaShuoRepository
import com.kotlin.android.home.ui.bean.BannerItem
import com.kotlin.android.home.ui.tashuo.bean.RcmdFollowUserBean
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.binder.LabelBinder

class TaShuoViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { TaShuoRepository() }

    private val mUIModel = BaseUIModel<CommHasNextList<MultiTypeBinder<*>>>()
    val uiState = mUIModel.uiState

    /**
     * 加载数据
     * banner,推荐内容
     */
    fun loadData() {
        callParallel(
            { repo.loadBanner() },
            { repo.loadRcmdData(null, mUIModel.pageSize) },
            uiModel = mUIModel,
            isShowLoading = false,
            isRefresh = true,
            isEmpty = {
                it.items.isNullOrEmpty()
            },
            pageStamp = {
                it.nextStamp
            },
            hasMore = {
                it.hasNext
            }
        ) {
            val list = mutableListOf<MultiTypeBinder<*>>()
            var nextStamp: String? = null
            var hasNext = false

            (it[0] as? RegionPublish)?.apply {
                //Banner
                BannerItem.converter2Binder(
                    data = this,
                    marginRect = Rect(7.dp, 0, 7.dp, 8.dp)
                )?.apply {
                    list.add(this)
                }
            }
            (it[1] as? TaShuoRcmdList)?.apply {
                rcmdUserList?.apply {
                    if (isNotEmpty()) {
                        list.add(
                            LabelBinder(
                                titleRes = R.string.home_rcmd_follow_user_title,
                                rootMargin = Rect(7.dp, 12.dp, 7.dp, 12.dp)
                            )
                        )
                        list.add(
                            RcmdFollowUserBean.converter2Binder(this)
                        )
                    }
                }
                rcmdContentList?.apply { 
                    nextStamp = this.nextStamp
                    hasNext = this.hasNext
                    items?.apply { 
                        if (isNotEmpty()) {
                            list.add(
                                LabelBinder(
                                    titleRes = R.string.home_ta_shuo_title,
                                    rootMargin = Rect(7.dp, 12.dp, 7.dp, 12.dp)
                                )
                            )
                            list.addAll(
                                map { content ->
                                    CommunityCardItem.converter2Binder(
                                        content = content
                                    )
                                }
                            )
                        }
                    }
                }
            }

            CommHasNextList(
                items = list,
                nextStamp = nextStamp,
                hasNext = hasNext
            )
        }
    }

    /**
     * 加载更多的内容推荐数据
     */
    fun loadMoreData() {
        callParallel(
            { repo.loadRcmdData(mUIModel.pageStamp, mUIModel.pageSize) },
            uiModel = mUIModel,
            isShowLoading = false,
            isRefresh = false,
            isEmpty = {
                false
            },
            pageStamp = {
                it.nextStamp
            },
            hasMore = {
                it.hasNext
            }
        ) {
            val list = mutableListOf<MultiTypeBinder<*>>()
            var nextStamp: String? = null
            var hasNext = true

            (it[0] as? TaShuoRcmdList)?.apply {
                rcmdContentList?.apply {
                    nextStamp = this.nextStamp
                    hasNext = this.hasNext
                    items?.apply {
                        if (isNotEmpty()) {
                            list.addAll(
                                map { content ->
                                    CommunityCardItem.converter2Binder(
                                        content = content
                                    )
                                }
                            )
                        }
                    }
                }
            }

            CommHasNextList(
                items = list,
                nextStamp = nextStamp,
                hasNext = hasNext
            )
        }
    }
    
}