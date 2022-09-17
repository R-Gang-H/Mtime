package com.kotlin.android.home.ui.recommend

import android.view.View
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.community.card.component.item.BaseCardFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.FagCommonCardListBinding
import com.kotlin.android.home.ui.recommend.adapter.ShowingComingMovieItemBinder
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/7
 *
 * 首页-推荐
 */
class RecommendFragment : BaseCardFragment<RecommendViewModel, FagCommonCardListBinding>(),
    OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {
    private var mAdapter: MultiTypeAdapter? = null

    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider = MTimeDataProvider {
//        "加载播放地址回调-".e()
        mViewModel?.getVideoPlayUrl(it.videoId, it.source, "http")
    }

    override fun initView() {
        mBinding?.apply {
            mRefreshLayout.setOnRefreshLoadMoreListener(this@RecommendFragment)
            mMultiStateView.setMultiStateListener(this@RecommendFragment)
            mAdapter = createMultiTypeAdapter(mRecyclerView)
                .setOnClickListener(::onBinderClick)
        }
    }

    override fun show() {
        super.show()
        // 此处的作用是main tab切换后不走生命周期方法，所以在此主动恢复播放
        mViewModel?.mTrailerBinder?.resume()
    }

    override fun hide() {
        super.hide()
        // 此处的作用是main tab切换后不走生命周期方法，所以在此主动暂停播放
        mViewModel?.mTrailerBinder?.pause()
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    override fun onLoginStateChanged() {
        initData()
    }

    override fun startObserve() {
        super.startObserve()

        // 视频播放地址获取结果监听
        registerPlayUrlObserve()

        // banner、正在热映和即将上映、预告片、feed流数据
        registerUIStateObserve()

        // 想看、已想看操作结果监听
        registerWantToSeeObserve()
    }

    // 视频播放地址获取结果监听
    private fun registerPlayUrlObserve() {
        observeLiveData(mViewModel?.videoPlayUrlState) {
            it?.apply {
                success?.apply {
                    videoDataProvider.setVideoPlayUrlList(this)
                }

                netError?.apply {
                    videoDataProvider.setVideoPlayUrlError()
                }
                error?.apply {
                    videoDataProvider.setVideoPlayUrlError()
                }
            }
        }
    }

    //banner、正在热映和即将上映、预告片、feed流数据
    private fun registerUIStateObserve() {
        observeLiveData(mViewModel?.uiState) {
            mBinding?.apply {
                it.apply {
                    mRefreshLayout.complete(this)
                    mMultiStateView.complete(this)

                    success?.apply {
                        items?.apply {
                            if (isRefresh) {
                                mAdapter?.notifyAdapterDataSetChanged(this)
                            } else {
                                mAdapter?.notifyAdapterAdded(this)
                            }
                        }
                    }
                }
            }
        }
    }

    //想看、已想看操作结果监听
    private fun registerWantToSeeObserve() {
        observeLiveData(mViewModel?.mCommWantToSeeUIState) {
            it.apply {
                showOrHideProgressDialog(showLoading)
                showToast(error)
                showToast(netError)

                success?.apply {
                    if (result.isSuccess()) {
                        when (extend) {
                            is ShowingComingMovieItemBinder -> {
                                (extend as ShowingComingMovieItemBinder).wantToSeeChanged()
                            }
                        }
                    } else {
                        showToast(result.statusMsg)
                    }
                }
            }
        }
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    override fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is ShowingComingMovieItemBinder -> {
                //正在热映和即将上映相关点击事件
                onShowingComingMovieItemBinderClickListener(view, binder)
            }
            else -> {
                super.onBinderClick(view, binder)
            }
        }
    }

    //正在热映和即将上映相关点击事件
    private fun onShowingComingMovieItemBinderClickListener(
        view: View,
        binder: ShowingComingMovieItemBinder
    ) {
        when (view.id) {
            R.id.mMovieBtnFl -> {
                //已想看 //想看
                onWanToSeeBtnClick(
                    movieId = binder.movieItem.id,
                    btnState = binder.movieItem.btnState,
                    binder = binder
                )
            }
        }
    }

    /**
     * 已想看/想看按钮的点击事件
     */
    private fun onWanToSeeBtnClick(
        movieId: Long,
        btnState: Long,
        binder: MultiTypeBinder<*>
    ) {
        afterLogin {
            mViewModel?.getMovieWantToSee(
                movieId = movieId,
                flag = if (btnState == CommConstant.MOVIE_BTN_STATE_WANT_SEE)
                    CommConstant.MOVIE_WANT_SEE_FLAG else CommConstant.MOVIE_CANCEL_WANT_SEE_FLAG,
                extend = binder
            )
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadMoreData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mBinding?.apply {
            mViewModel?.loadData(
                lifecycle = lifecycle,
                recyclerView = mRecyclerView,
                provider = videoDataProvider
            )
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mBinding?.mRefreshLayout?.autoRefresh()
            }
        }
    }
}