package com.kotlin.android.home.ui.findmovie

import android.graphics.Rect
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.callParallel
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.entity.CommHasNextList
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.data.entity.filmlist.FilmListEntity
import com.kotlin.android.app.data.entity.search.ConditionResult
import com.kotlin.android.app.data.entity.search.MovieSearchResult
import com.kotlin.android.app.data.entity.toplist.IndexTopListQuery
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.repository.FindMovieRepository
import com.kotlin.android.home.repository.TopListRepository
import com.kotlin.android.home.ui.bean.BannerItem
import com.kotlin.android.home.ui.findmovie.adapter.FilmFilterResultBinder
import com.kotlin.android.home.ui.findmovie.adapter.FilmListBinder
import com.kotlin.android.home.ui.findmovie.adapter.TimeListBinder
import com.kotlin.android.home.ui.findmovie.bean.SearchBean
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.binder.LabelBinder

/**
 * @author wangwei
 * @date 2022/4/11
 */
class FindMovieViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { FindMovieRepository() }
    private val topListRepo by lazy { TopListRepository() }

    private val mFindMovieUIModel = BaseUIModel<CommHasNextList<MultiTypeBinder<*>>>()
    val uiState = mFindMovieUIModel.uiState

    private val mFilterConditionUIModel = BaseUIModel<ConditionResult>()
    val filterConditionUiState = mFilterConditionUIModel.uiState


    private val mFiltResultUIModel = BaseUIModel<CommHasNextList<MultiTypeBinder<*>>>()
    val filterResultUiState = mFiltResultUIModel.uiState

    private var pageSize = 20L


    /**
     * 加载数据
     * banner,正在热映和待映推荐，每日佳片，内容推荐数据
     */
    fun loadData() {
        callParallel(
            { repo.loadBanner() },
            { repo.loadFilmList() },
            { topListRepo.getTopListQuery(1, 1, 5) },
//            { repo.loadTrailers(RcmdTrailerList.TYPE_TODAY) },
//            { repo.loadRcmdData(mFindMovieUIModel.pageStamp, pageSize) },
            uiModel = mFindMovieUIModel,
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
            )

            (it[0] as? RegionPublish)?.apply {
                //Banner
                BannerItem.converter2Binder(this, Rect(15.dp, 0, 15.dp, 0))?.apply {
                    result.items?.add(this)
                }
            }

            (it[1] as? FilmListEntity)?.apply {
                //时光甄选片单
                pageRcmds?.let { contents ->
                    result.items?.apply {
                        if (contents.isNotEmpty()) {
                            add(
                                LabelBinder(
                                    titleRes = R.string.home_film_list_tip,
                                    actionTitleRes = R.string.home_content_all,
                                    displayAction = true,
                                    rootMargin = Rect(15.dp, 12.dp, 15.dp, 12.dp)
                                ).apply {
                                    setOnClickListener { view, binder ->
                                        setOnClickListener { view, binder ->
                                            when (view.id) {
                                                R.id.actionView -> {
                                                    // 甄选影片列表
                                                    getProvider(ITabletProvider::class.java)?.startTabletMainActivity()
                                                }
                                            }
                                        }

                                    }
                                }
                            )
                            add(FilmListBinder(contents.toList()))
                        }
                    }
                }
            }


            (it[2] as? IndexTopListQuery)?.apply {
                //榜单电影
//                TrailerItem.converter2Binder(lifecycle, recyclerView, provider, this)?.apply {
//                    result.items?.add(this)
//                }
                this?.let { content ->
                    result.items?.apply {
                        if (content?.items?.isNotEmpty() == true) {
                            add(
                                LabelBinder(
                                    titleRes = R.string.home_time_list_tip,
                                    actionTitleRes = R.string.home_content_all,
                                    displayAction = true,
                                    rootMargin = Rect(15.dp, 12.dp, 15.dp, 12.dp)
                                ).apply {
                                    setOnClickListener { view, binder ->
                                        when (view.id) {
                                            R.id.actionView -> {
                                                getProvider(IHomeProvider::class.java)?.startTopListActivity()
                                            }
                                        }
                                    }
                                }
                            )
                            add(TimeListBinder(content))
                        }
                    }
                }
            }

//            (it[3] as? HomeRcmdContentList)?.apply {
//                //推荐数据
//                result.hasNext = hasNext
//                result.nextStamp = nextStamp
//
//                items?.let { contents ->
//                    result.items?.apply {
//                        add(
//                            LabelBinder(
//                                titleRes = R.string.home_recommend_title,
//                                displayAction = true,
//                            )
//                        )
//                        addAll(
//                            contents.map { rcmdContent ->
//                                CommunityCardItem.converter2Binder(content = rcmdContent)
//                            }
//                        )
//                    }
//                }
//            }
            result
        }
    }

    /**
     * 获取筛选条件
     */
    fun loadFilterCondition() {
        call(
            uiModel = mFilterConditionUIModel,
            isShowLoading = false,
            isRefresh = true
        ) {
            repo.loadFilterCondition()
        }
    }

    /**
     * 加载更多筛选结果数据
     */
    fun loadMoreData(isRefresh: Boolean, condition: SearchBean = SearchBean()) {
        callParallel(
            {
                repo.loadFilmFilterResult(
                    keyword = condition.keyword,
                    page = mFiltResultUIModel.pageIndex.toString(),
                    pageSize = pageSize,
                    year = condition.year,
                    genreTypes = condition.genreTypes,
                    area = condition.area,
                    sortType = condition.sortType
                )
            },
            uiModel = mFiltResultUIModel,
            isShowLoading = true,
            isRefresh = isRefresh,
            isEmpty = {
                it.items?.isEmpty() == true
            },
            hasMore = {
                it.hasNext
            },
            pageStamp = {
                mFiltResultUIModel.pageStamp
            }
        ) {
            val result = CommHasNextList(
                items = mutableListOf<MultiTypeBinder<*>>(),
            )

            (it[0] as? MovieSearchResult)?.apply {
                //搜索结果数据
                result.hasNext = mFiltResultUIModel?.totalCount < this.total.orZero()
                movies?.let { contents ->
                    result.items?.apply {
                        addAll(
                            contents.map {
                                FilmFilterResultBinder(it)
                            }
                        )
                    }
                }
            }

            result
        }
    }

}