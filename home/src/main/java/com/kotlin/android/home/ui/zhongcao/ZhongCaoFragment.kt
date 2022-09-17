package com.kotlin.android.home.ui.zhongcao

import android.os.Bundle
import com.kotlin.android.app.data.entity.common.CommSubType
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.home.databinding.FagZhongCaoBinding
import com.kotlin.android.home.ui.adapter.BannerAdapter
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.widget.banner.setCommIndicator
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

/**
 * 首页-推荐-种草页面
 */
class ZhongCaoFragment : BaseVMFragment<ZhongCaoViewModel, FagZhongCaoBinding>(),
    MultiStateView.MultiStateListener {
    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    override fun startObserve() {
        // banner数据回调
        observeLiveData(mViewModel?.bannerUIState) {
            mBinding?.apply {
                it.apply {
                    success?.apply {
                        mBannerViewCl.visible()
                        mBannerView
                            .setRoundCorners(12.dpF)
                            .setCommIndicator()
                            .adapter = BannerAdapter(this)
                    }

                    error?.apply {
                        mBannerViewCl.gone()
                    }
                    netError?.apply {
                        mBannerViewCl.gone()
                    }
                }
            }
        }

        // 子分类数据回调
        observeLiveData(mViewModel?.subTypeUIState) {
            mBinding?.apply {
                it.apply {
                    showOrHideProgressDialog(showLoading)

                    error?.apply {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }

                    netError?.apply {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }

                    success?.subTypes?.apply {
                        setNavData(this)
                    }
                }
            }
        }
    }

    //设置导航数据
    private fun setNavData(navList: List<CommSubType>) {
        mBinding?.apply {
            val creator = FragPagerItems(mContext)
            navList.forEach {
                val args = Bundle()
                args.putLong(ZhongCaoSubFragment.KEY_SUB_TYPE_ID, it.subTypeId)
                creator.add(title = it.name, clazz = ZhongCaoSubFragment::class.java, args = args)
            }

            val adapter = FragPagerItemAdapter(childFragmentManager, creator)
            mViewPager.adapter = adapter
            mViewPager.offscreenPageLimit = 8
            mTabLayout.setViewPager(mViewPager)
        }
    }

    override fun initData() {
        mViewModel?.apply {
            loadBanner()
            loadSubTypes()
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                initData()
            }
        }
    }
}