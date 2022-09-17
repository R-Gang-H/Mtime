package com.kotlin.android.home.ui.recommend

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.callParallel
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.entity.CommHasNextList
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.data.entity.home.HomeRcmdContentList
import com.kotlin.android.app.data.entity.home.HomeShowingComingMovies
import com.kotlin.android.app.data.entity.home.RcmdTrailerList
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.home.R
import com.kotlin.android.home.repository.RecommendRepository
import com.kotlin.android.home.ui.adapter.BannerBinder
import com.kotlin.android.home.ui.bean.BannerItem
import com.kotlin.android.home.ui.recommend.adapter.FunctionBallBinder
import com.kotlin.android.home.ui.recommend.adapter.TrailerBinder
import com.kotlin.android.home.ui.recommend.bean.ShowingComingCategoryItem
import com.kotlin.android.home.ui.recommend.bean.TrailerItem
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.binder.LabelBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/7
 */
class RecommendViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { RecommendRepository() }

    private val mRecommendUIModel = BaseUIModel<CommHasNextList<MultiTypeBinder<*>>>()
    val uiState = mRecommendUIModel.uiState

    // 视频播放地址
    private val videoPlayUrlUIModel = BaseUIModel<VideoPlayList>()
    val videoPlayUrlState = videoPlayUrlUIModel.uiState

    var mTrailerBinder: TrailerBinder? = null

    /**
     * 获取视频URL
     */
    fun getVideoPlayUrl(videoId: Long, source: Long, scheme: String) {
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.getPlayUrlList(videoId, source, scheme)
            }
            videoPlayUrlUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 加载数据
     * banner,正在热映和待映推荐，每日佳片，内容推荐数据
     */
    fun loadData(
        lifecycle: Lifecycle,
        recyclerView: RecyclerView,
        provider: MTimeDataProvider
    ) {
        callParallel(
            { repo.loadBanner() },
            { repo.loadShowingComingMovies() },
            { repo.loadTrailers(RcmdTrailerList.TYPE_TODAY) },
            { repo.loadRcmdData(mRecommendUIModel.pageStamp, mRecommendUIModel.pageSize) },
            uiModel = mRecommendUIModel,
            isShowLoading = false,
            isRefresh = true,
            isEmpty = {
                it.items.isNullOrEmpty()
            },
            hasMore = {
                it.hasNext
            },
            pageStamp = {
                it.nextStamp
            }
        ) {
            val result = CommHasNextList(
                items = mutableListOf<MultiTypeBinder<*>>(),
                nextStamp = null,
                hasNext = false
            )

            (it[0] as? RegionPublish)?.apply {
                //Banner
                BannerItem.converter2Binder(this)?.apply {
                    result.items?.add(this)
                }
            }

            (it[1] as? HomeShowingComingMovies)?.apply {
                //正在热映
                ShowingComingCategoryItem.converter2Binder(this)?.apply {
                    result.items?.add(this)
                }
            }

            (it[2] as? RcmdTrailerList)?.apply {
                //预告片
                TrailerItem.converter2Binder(lifecycle, recyclerView, provider, this)?.apply {
                    result.items?.add(this)
                    mTrailerBinder = this
                }
            }

            (it[3] as? HomeRcmdContentList)?.apply {
                //推荐数据
                result.hasNext = hasNext
                result.nextStamp = nextStamp

                items?.let { contents ->
                    result.items?.apply {
                        add(
                            LabelBinder(
                                titleRes = R.string.home_recommend_title
                            )
                        )
                        addAll(
                            contents.map { rcmdContent ->
                                CommunityCardItem.converter2Binder(content = rcmdContent)
                            }
                        )
                    }
                }
            }

            //功能球
            result.items?.apply {
                if (isNotEmpty() && this[0] is BannerBinder) {
                    add(1, FunctionBallBinder())
                } else {
                    add(0, FunctionBallBinder())
                }
            }

            result
        }
    }

    /**
     * 加载更多内容推荐数据
     */
    fun loadMoreData() {
        callParallel(
            { repo.loadRcmdData(mRecommendUIModel.pageStamp, mRecommendUIModel.pageSize) },
            uiModel = mRecommendUIModel,
            isShowLoading = false,
            isRefresh = false,
            isEmpty = {
                false
            },
            hasMore = {
                it.hasNext
            },
            pageStamp = {
                it.nextStamp
            }
        ) {
            val result = CommHasNextList(
                items = mutableListOf<MultiTypeBinder<*>>(),
                nextStamp = null,
                hasNext = false
            )

            (it[0] as? HomeRcmdContentList)?.apply {
                //推荐数据
                result.hasNext = hasNext
                result.nextStamp = nextStamp

                items?.let { contents ->
                    result.items?.apply {
                        addAll(
                            contents.map { rcmdContent ->
                                CommunityCardItem.converter2Binder(content = rcmdContent)
                            }
                        )
                    }
                }
            }

            result
        }
    }

}