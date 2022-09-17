package com.kotlin.tablet.ui.mine

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.common.CollectionResult
import com.kotlin.android.app.data.entity.filmlist.MyCollectionFilmList
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.showDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.MineMyCollectionBinder
import com.kotlin.tablet.databinding.FragMineCreateBinding
import com.kotlin.tablet.view.RecyclerViewNoBugLinearLayoutManager
import com.kotlin.tablet.view.VerticalItemDecoration

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/28
 * 描述:我收藏的片单
 **/
class FilmListCollectionFragment : BaseVMFragment<FilmListMineViewModel, FragMineCreateBinding>(),
    MultiStateView.MultiStateListener {
    private lateinit var mAdapter: MultiTypeAdapter
    private var mCount = 0L
    override fun initView() {
        mBinding?.apply {
            mAdapter =
                createMultiTypeAdapter(
                    mCreateRv,
                    RecyclerViewNoBugLinearLayoutManager(mContext)
                ).setOnClickListener(
                    ::handleClick
                )
            mRefreshLayout.apply {
                setOnRefreshListener {
                    mViewModel?.reqMyCollectionData(true)
                }
                setOnLoadMoreListener {
                    mViewModel?.reqMyCollectionData(false)
                }
            }
            mCreateRv.addItemDecoration(
                VerticalItemDecoration(
                    edge = 1F,
                    cornerRadius = 1.dpF,
                    edgeColorRes = R.color.color_f2f3f6
                )
            )
            mMultiStateView.setMultiStateListener(this@FilmListCollectionFragment)
        }
    }

    private fun handleClick(view: View, multiTypeBinder: MultiTypeBinder<*>) {
        if (multiTypeBinder is MineMyCollectionBinder) {
            when (view.id) {
                R.id.mMyCollectionHostLay -> {
                    if (multiTypeBinder.bean.status != 100L) {
                        getProvider(ITabletProvider::class.java)?.startFilmListDetailsActivity(
                            filmListId = multiTypeBinder.bean.filmListId
                        )
                    }
                }
                R.id.mCollectionMoreIv -> {
                    showDialog(
                        context,
                        content = R.string.tablet_film_list_cancel_collection,
                        negative = R.string.cancel
                    ) {
                        mViewModel?.cancelCollect(
                            multiTypeBinder.bean.filmListId ?: 0L,
                            multiTypeBinder.mPosition
                        )
                    }
                }
            }
        }

    }

    override fun startObserve() {
        observeCollectList()
        observeCancelCollect()
    }

    /**
     * 取消收藏监听
     */
    @SuppressLint("SetTextI18n")
    private fun observeCancelCollect() {
        mViewModel?.cancelCollectUIState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    val bean = result as? CollectionResult
                    bean?.apply {
                        if (bizCode == 0L) {
                            mAdapter.notifyAdapterRemoved(mAdapter.getList()[position])
                            --mCount
                            updateCount()
                        } else {
                            bizMsg?.showToast()
                        }
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }

    /**
     * 收藏列表监听
     */
    @SuppressLint("SetTextI18n")
    private fun observeCollectList() {
        mViewModel?.myCollectionUIState?.observe(this) {
            it?.apply {
                mBinding?.mRefreshLayout?.complete(this)
                mBinding?.mMultiStateView?.complete(this)
                success?.apply {
                    if (isRefresh) {
                        mAdapter.notifyAdapterDataSetChanged(getCollectionBinder(this))
                    } else {
                        mAdapter.notifyAdapterAdded(getCollectionBinder(this))
                    }
                    mCount = count ?: 0L
                    updateCount()
                }
            }
        }
    }

    /**
     * 更新片单数量
     */
    @SuppressLint("SetTextI18n")
    fun updateCount(){
        mBinding?.mFilmListCountTv?.text = "共${mCount}个"
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    private fun getCollectionBinder(list: MyCollectionFilmList): List<MultiTypeBinder<*>> {
        return list.myFavorites?.map { MineMyCollectionBinder(it) } ?: mutableListOf()
    }

    override fun onMultiStateChanged(viewState: Int) {
        initData()
    }
}