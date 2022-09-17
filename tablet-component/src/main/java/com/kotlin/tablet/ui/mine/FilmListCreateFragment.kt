package com.kotlin.tablet.ui.mine

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.filmlist.MyCreateFilmList
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.MineMyCreateBinder
import com.kotlin.tablet.databinding.FragMineCreateBinding
import com.kotlin.tablet.view.VerticalItemDecoration

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/28
 * 描述:我创建的片单
 **/
class FilmListCreateFragment : BaseVMFragment<FilmListMineViewModel, FragMineCreateBinding>(),
    MultiStateView.MultiStateListener {
    private lateinit var mAdapter: MultiTypeAdapter
    override fun initView() {
        mBinding?.apply {
            mAdapter =
                createMultiTypeAdapter(mCreateRv, LinearLayoutManager(mContext)).setOnClickListener(
                    ::handleClick
                )
            mCreateRv.addItemDecoration(
                VerticalItemDecoration(
                    edge = 1F,
                    cornerRadius = 1.dpF,
                    edgeColorRes = R.color.color_f2f3f6
                )
            )
            mRefreshLayout.apply {
                setOnRefreshListener {
                    mViewModel?.reqMyCreateData(true)
                }
                setOnLoadMoreListener {
                    mViewModel?.reqMyCreateData(false)
                }
            }
            mMultiStateView.setMultiStateListener(this@FilmListCreateFragment)
        }
    }

    private fun handleClick(view: View, multiTypeBinder: MultiTypeBinder<*>) {
        if (multiTypeBinder is MineMyCreateBinder) {
            when (view.id) {
                R.id.mMyCreateHostLay -> {
                    getProvider(ITabletProvider::class.java)?.startFilmListDetailsActivity(
                        1,
                        filmListId = multiTypeBinder.bean.filmListId ?: 0L
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun startObserve() {
        mViewModel?.myCreateUIState?.observe(this) {
            it?.apply {
                mBinding?.mRefreshLayout?.complete(this)
                mBinding?.mMultiStateView?.complete(this)
                success?.apply {
                    if (isRefresh) {
                        mAdapter.notifyAdapterDataSetChanged(getCreateBinder(this))
                    } else {
                        mAdapter.notifyAdapterAdded(getCreateBinder(this))
                    }
                    mBinding?.mFilmListCountTv?.text = "共${count}个"
                }
            }
        }
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    private fun getCreateBinder(list: MyCreateFilmList): List<MultiTypeBinder<*>> {
        return list.myCreates?.map { MineMyCreateBinder(it) } ?: mutableListOf()
    }

    override fun onMultiStateChanged(viewState: Int) {
        initData()
    }
}