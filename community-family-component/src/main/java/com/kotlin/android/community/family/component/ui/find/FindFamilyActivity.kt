package com.kotlin.android.community.family.component.ui.find

import android.content.DialogInterface
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.bonus.scene.component.postJoinFamily
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyFindBinding
import com.kotlin.android.community.family.component.ui.find.binder.FindFamilyCategoryBinder
import com.kotlin.android.community.family.component.ui.find.binder.FindFamilyRecommendBinder
import com.kotlin.android.community.family.component.ui.find.binder.FindFamilyRecommendItemBinder
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.android.widget.titlebar.search
import kotlinx.android.synthetic.main.act_family_find.*

/**
 * @des 找家族页面
 * @author zhangjian
 * @date 2022/3/16 10:35
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_FIND)
class FindFamilyActivity : BaseVMActivity<FindFamilyViewModel, ActFamilyFindBinding>(),
    MultiStateView.MultiStateListener {
    private var mAdapter: MultiTypeAdapter? = null
    private var mListData: ArrayList<MultiTypeBinder<*>> = ArrayList()

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
        //初始化标题
        initTitle()
        //初始化adapter
        initAdapter()
    }

    private fun initAdapter() {
        mBinding?.rvFamily?.apply {
            mAdapter = createMultiTypeAdapter(
                this,
                LinearLayoutManager(this@FindFamilyActivity, LinearLayoutManager.VERTICAL, false)
            )
        }
    }

    private fun initTitle() {
        TitleBarManager.with(this)
            .setTitle(getString(R.string.family_find))
            .back {
                finish()
            }
            .search {
                //跳家族搜索页面
                getProvider(ISearchProvider::class.java)?.startSearchActivity(this)
            }
    }

    override fun initData() {
        mViewModel?.loadFindFamilyData()
    }

    override fun startObserve() {
        mViewModel?.findModelState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
            }
            mBinding?.mMultiStateView?.complete(it)
            it?.success?.apply {
                if (groupCategoryList.isNullOrEmpty() && rcmdGroupList.isNullOrEmpty()) {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    return@observe
                }
                groupCategoryList?.apply {
                    mListData.add(FindFamilyCategoryBinder(this))
                }
                rcmdGroupList?.forEach {
                    mListData.add(FindFamilyRecommendBinder(it, ::joinManager))
                }
                mAdapter?.notifyAdapterAdded(mListData)
            }
        }
        //加入家族
        mViewModel?.mCommJoinFamilyUISate?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    when (result.status ?: 0) {
                        CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINED,
                        CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED -> {
                            extend.data.hasJoin = FamilyConstant.CONSTANT_STATE_1
                            extend.data.memberCount = (extend.data.memberCount ?: 0L) + 1L
                            extend.notifyAdapterSelfChanged()
                            postJoinFamily()
                        }
                        CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINING -> {
                            extend.data.hasJoin = FamilyConstant.CONSTANT_STATE_2
                            extend.notifyAdapterSelfChanged()
                        }
                        CommConstant.JOIN_FAMILY_RESULT_STATUS_BLACKLIST -> {
                            extend.data.hasJoin = FamilyConstant.CONSTANT_STATE_3
                            extend.notifyAdapterSelfChanged()
                        }
                        else -> {
                            showToast(result.failMsg)
                        }
                    }
                }
                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
        //退出家族
        mViewModel?.mCommOutFamilyUISate?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    if (result.status == 1L) {
                        extend.data.hasJoin = FamilyConstant.CONSTANT_STATE_0
                        extend.data.memberCount = (extend.data.memberCount ?: 1) - 1
                        extend.notifyAdapterSelfChanged()
                    } else {
                        showToast(result.failMsg)
                    }
                }
                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    /**
     * 加入或者退出家族
     * @param state 状态 0-未加入，1-已加入成功，2-加入中（待审核），3-黑名单
     * @param id 家族id
     * @param binder 更新binder中的人数
     */
    private fun joinManager(state: Long, id: Long, binder: MultiTypeBinder<*>) {
        when (state) {
            FamilyConstant.CONSTANT_STATE_0 -> {
                if (binder is FindFamilyRecommendItemBinder) {
                    mViewModel?.joinFamily(id, binder)
                }
            }

            FamilyConstant.CONSTANT_STATE_1 -> {
                if (binder is FindFamilyRecommendItemBinder) {
                    val listener = DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                        if (which == -1) {
                            //确定退出家族
                            mViewModel?.outFamily(id, binder)
                        }
                    }
                    BaseDialog.Builder(this)
                        .setContent(R.string.community_exit_family_hint)
                        .setNegativeButton(R.string.widget_cancel, listener)
                        .setPositiveButton(R.string.widget_sure, listener)
                        .create()
                        .show()
                }
            }
        }
    }


    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_NO_NET,
            MultiStateView.VIEW_STATE_ERROR -> {
                mListData.clear()
                mAdapter?.notifyAdapterClear()
                initData()
            }
        }
    }
}