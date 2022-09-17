package com.kotlin.android.mine.ui.collection.fragment

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_ARTICLE
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_CINEMA
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_MOVIE
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_PERSON
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_POST
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mine.*
import com.kotlin.android.mine.databinding.FragCollectionBinding
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.router.liveevent.COLLECTION_OR_CANCEL
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.frag_collection.*

/**
 * create by lushan on 2020/9/11
 * description:电影收藏
 */
class CollectionFragment : BaseVMFragment<CollectionViewModel, FragCollectionBinding>() {
    companion object {
        fun newInstance(): CollectionFragment = CollectionFragment()
    }

    private var mIsInitData: Boolean = false
    private var mCollectionType: Long = 0L//收藏分类
    private var mAdapter: MultiTypeAdapter? = null

    override fun initVM(): CollectionViewModel = viewModels<CollectionViewModel>().value

    override fun initView() {
        refreshLayout?.setOnLoadMoreListener {
            loadCollectionData(true)
        }
        refreshLayout?.setOnRefreshListener {
            loadCollectionData(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mIsInitData.not()) {
            refreshLayout?.autoRefresh()
        }
    }

    override fun initData() {
        mCollectionType = arguments?.getLong(KEY_COLLECTION_TYPE) ?: 0L
        mBinding?.setVariable(BR.viewModel, mViewModel)
        mAdapter = createMultiTypeAdapter(collectRv, LinearLayoutManager(context))
        mAdapter?.setOnClickListener { view, binder ->

        }
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    loadCollectionData(false)
                }
            }

        })
    }

    private fun loadCollectionData(isMore: Boolean) {
        mViewModel?.getCollectionData(mCollectionType, isMore)
    }

    private fun loadComplete() {
        refreshLayout?.finishRefresh()
        refreshLayout?.finishLoadMore()
    }

    override fun startObserve() {
        mViewModel?.collectionState?.observe(this, Observer {
            mIsInitData = true
            it?.apply {
                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadComplete()
                    if (loadMore) {
                        mAdapter?.notifyAdapterAdded(this.list)
                    } else {
                        mAdapter?.notifyAdapterDataSetChanged(this.list)
                        if (this.list.isEmpty()) {
                            setContentState(MultiStateView.VIEW_STATE_EMPTY)
                        }
                    }

                    refreshLayout?.setNoMoreData(noMoreData)
                    setCountTv(this.totalCount)
                }

                netError?.run {
                    loadComplete()
                    if (mAdapter?.itemCount ?: 0 == 0) {//如果没有数据是显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
                error?.run {
                    loadComplete()
                    if (mAdapter?.itemCount ?: 0 == 0) {//如果页面上没有显示数据，需要显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }
            }
        })

        LiveEventBus.get(COLLECTION_OR_CANCEL, com.kotlin.android.app.router.liveevent.event.CollectionState::class.java).observe(this, Observer {
            if (mCollectionType == it?.collectionType) {//只有相等的才是当前页面
                refreshLayout?.autoRefresh()
            }
        })
    }


    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
    }

    private fun setCountTv(totalNum: Long) {
        SpannableString(getTotalContent(totalNum)).apply {
            val startIndex = 2//从数字开始位置
            setSpan(ForegroundColorSpan(getColor(R.color.color_20a0da)), startIndex, totalNum.toString().length + startIndex, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
        }.also {
            countTv?.visible()
            countTv?.text = it
        }
    }

    private fun getTotalContent(totalNum: Long): String = when (mCollectionType) {
        COLLECTION_TYPE_MOVIE -> getString(R.string.mine_collection_total_movie_format, totalNum)
        COLLECTION_TYPE_CINEMA -> getString(R.string.mine_collection_total_cinema_format, totalNum)
        COLLECTION_TYPE_PERSON -> getString(R.string.mine_collection_total_person_format, totalNum)
        COLLECTION_TYPE_ARTICLE -> getString(R.string.mine_collection_total_article_format, totalNum)
        COLLECTION_TYPE_POST -> getString(R.string.mine_collection_total_post_format, totalNum)
        else -> getString(R.string.mine_collection_total_movie_format, totalNum)

    }

    override fun destroyView() {
    }

}