package com.kotlin.tablet.ui.mycreate

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.filmlist.MyCreateFilmList
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.bus.ext.post
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.tablet.KEY_CONTRIBUTE_ACTIVITY_ID
import com.kotlin.tablet.PAGE_CONTRIBUTE_ACTIVITY
import com.kotlin.tablet.PAGE_SUCCESS_ACTIVITY
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.ContributeMyCreateBinder
import com.kotlin.tablet.databinding.ActivityMyCreateBinding
import com.kotlin.tablet.event.FilmListPageCloseEvent
import com.kotlin.tablet.view.showContributeResultDialog
import kotlinx.android.synthetic.main.item_contribute_my_create.*

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/27
 * 描述:我的片单-投稿
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_CONTRIBUTE_MY_CREATE)
class MyCreateActivity : BaseVMActivity<MyCreateViewModel, ActivityMyCreateBinding>(),
    MultiStateView.MultiStateListener {
    private lateinit var mAdapter: MultiTypeAdapter
    private var activityId: Long? = 0L
    override fun getIntentData(intent: Intent?) {
        activityId = intent?.getLongExtra(KEY_CONTRIBUTE_ACTIVITY_ID, 0L)
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.tablet_main_mine)
            .back { finish() }
    }

    override fun initView() {
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(
                mMyCreateRv,
                LinearLayoutManager(this@MyCreateActivity)
            ).setOnClickListener(::handleClick)
            mRefreshLayout.apply {
                setOnRefreshListener {
                    mViewModel?.reqMyCreateData(true, activityId)
                }
                setOnLoadMoreListener {
                    mViewModel?.reqMyCreateData(false, activityId)
                }
            }
            mNewCreateBtn.onClick {
                getProvider(ITabletProvider::class.java)?.startFilmListCreateActivity(false)
            }
            mMultiStateView.setMultiStateListener(this@MyCreateActivity)
        }

    }

    private fun handleClick(view: View, multiTypeBinder: MultiTypeBinder<*>) {
        if (multiTypeBinder is ContributeMyCreateBinder) {
            if (view.id == R.id.mContributeCreateTv) {
                mViewModel?.contribute(activityId ?: 0L, multiTypeBinder.bean.filmListId ?: 0L)
            }
        }
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    override fun startObserve() {
        observeCreateInfo()
        observeContribute()
    }

    private fun observeContribute() {
        mViewModel?.resultUIState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    if (bizCode == 0L) {
                        showContributeResultDialog(true).back {
                            //返回片单页
                            FilmListPageCloseEvent(PAGE_CONTRIBUTE_ACTIVITY).post()
                            finish()
                        }.goOn {
                            //返回投稿活动页
                            finish()
                        }
                    } else {
                        showToast(bizMsg)
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

    private fun observeCreateInfo() {
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

    private fun getCreateBinder(list: MyCreateFilmList): List<MultiTypeBinder<*>> {
        return list.myManuscriptsFilmLists?.map { ContributeMyCreateBinder(it) } ?: mutableListOf()
    }

    override fun onMultiStateChanged(viewState: Int) {
        mBinding?.mRefreshLayout?.autoRefresh()
    }
}