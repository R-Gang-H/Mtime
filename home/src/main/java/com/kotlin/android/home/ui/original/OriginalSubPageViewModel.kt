package com.kotlin.android.home.ui.original

import android.graphics.Rect
import android.text.TextUtils
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.callParallel
import com.kotlin.android.app.data.entity.CommHasNextList
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.data.entity.home.OriginalRcmdContentList
import com.kotlin.android.article.component.item.bean.ArticleItem
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.home.repository.OriginalRepository
import com.kotlin.android.home.ui.bean.BannerItem
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 */
class OriginalSubPageViewModel : BaseViewModel() {
    private val repo by lazy { OriginalRepository() }

    private val mOriginalUIModel = BaseUIModel<CommHasNextList<MultiTypeBinder<*>>>()
    val uiState = mOriginalUIModel.uiState

    /**
     * 加载Banner和列表数据
     * rcmdTagsFilter: 推荐标签，逗号分隔，必传：1-电影，2-电视，3-音乐，4-人物，5-产业，6-全球拾趣，7-时光对话，8-时光策划，9-时光快讯，10-超级英雄，11-吐槽大会，12-时光大赏，13-精选，101-华语，102-欧美，103-日韩，104-其他
     */
    fun loadBannerAndContentData(rcmdTagsFilter: String) {
        if (TextUtils.equals("13", rcmdTagsFilter)) {
            // 只有精选tab下需要加载Banner
            callParallel(
                { repo.loadBanner() },
                { repo.loadData(rcmdTagsFilter, mOriginalUIModel.pageStamp, mOriginalUIModel.pageSize) },
                mainApiIndex = 1,
                uiModel = mOriginalUIModel,
                isShowLoading = false,
                isRefresh = true,
                hasMore = {
                    it.hasNext
                },
                isEmpty = {
                    it.items.isNullOrEmpty()
                },
                pageStamp = {
                    it.nextStamp
                }
            ) {
                val binders = arrayListOf<MultiTypeBinder<*>>()
                var hasMore = false
                var pageStamp = ""
                (it[1] as? OriginalRcmdContentList)?.apply {
                    this.rcmdContentList?.apply {
                        hasMore = this.hasNext
                        pageStamp = this.nextStamp
                        this.items?.forEach { item ->
                            binders.add(ArticleItem.converter2ArticleBinder(item.commContent))
                        }
                    }
                }
                (it[0] as? RegionPublish)?.apply {
                    if (binders.isNotEmpty()) {
                        BannerItem.converter2Binder(
                            this, Rect(15.dp, 0, 15.dp, 15.dp)
                        )?.apply {
                            binders.add(0, this)
                        }
                    }
                }
                CommHasNextList(
                    hasNext = hasMore,
                    items = binders,
                    nextStamp = pageStamp
                )
            }
        } else {
            loadContentData(true, rcmdTagsFilter)
        }
    }

    /**
     * 加载列表数据
     * rcmdTagsFilter: 推荐标签，逗号分隔，必传：1-电影，2-电视，3-音乐，4-人物，5-产业，6-全球拾趣，7-时光对话，8-时光策划，9-时光快讯，10-超级英雄，11-吐槽大会，12-时光大赏，13-精选，101-华语，102-欧美，103-日韩，104-其他
     */
    fun loadContentData(isRefresh: Boolean, rcmdTagsFilter: String) {
        callParallel(
            { repo.loadData(rcmdTagsFilter, mOriginalUIModel.pageStamp, mOriginalUIModel.pageSize) },
            mainApiIndex = 0,
            uiModel = mOriginalUIModel,
            isShowLoading = false,
            isRefresh = isRefresh,
            hasMore = {
                it.hasNext
            },
            isEmpty = {
                isRefresh && it.items.isNullOrEmpty()
            },
            pageStamp = {
                it.nextStamp
            }
        ) {
            val binders = arrayListOf<MultiTypeBinder<*>>()
            var hasMore = false
            var pageStamp = ""
            (it[0] as? OriginalRcmdContentList)?.apply {
                this.rcmdContentList?.apply {
                    hasMore = this.hasNext
                    pageStamp = this.nextStamp
                    this.items?.forEach { item ->
                        binders.add(ArticleItem.converter2ArticleBinder(item.commContent))
                    }
                }
            }
            CommHasNextList(
                hasNext = hasMore,
                items = binders,
                nextStamp = pageStamp
            )
        }
    }
}