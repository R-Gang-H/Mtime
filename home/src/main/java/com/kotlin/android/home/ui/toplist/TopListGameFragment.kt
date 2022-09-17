package com.kotlin.android.home.ui.toplist

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.toplist.GameTopList
import com.kotlin.android.app.data.entity.toplist.IndexAppGameTopList
import com.kotlin.android.home.databinding.FragToplistGameBinding
import com.kotlin.android.home.ui.toplist.adapter.TopListGameItemBinder
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.frag_toplist_game.*
import kotlinx.android.synthetic.main.frag_toplist_game.mMultiStateView

/**
 * @author vivian.wei
 * @date 2020/7/10
 * @desc 首页_榜单游戏
 */
class TopListGameFragment : BaseVMFragment<TopListGameViewModel, FragToplistGameBinding>(),
        MultiStateView.MultiStateListener {

    companion object {
        fun newInstance() = TopListGameFragment()
    }

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsFirst = true

    override fun initVM(): TopListGameViewModel = viewModels<TopListGameViewModel>().value

    override fun initView() {
        mAdapter = createMultiTypeAdapter(mFragToplistGameRv, LinearLayoutManager(mContext))
        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()

        //首次加载
        if (mIsFirst) {
            mViewModel?.getIndexGameTopList()
            mIsFirst = false
        }
    }

    override fun startObserve() {
        mViewModel?.gameTopListUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.let { indexAppGameTopList ->
                    showGameTopList(indexAppGameTopList)
                }

                error?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mViewModel?.getIndexGameTopList()
            }
        }
    }

    override fun destroyView() {

    }

    /**
     * 显示游戏榜单列表
     */
    private fun showGameTopList(indexAppGameTopList: IndexAppGameTopList) {
        indexAppGameTopList?.list?.let {
            mAdapter.notifyAdapterAdded(getBinderList(it))
        }
    }

    /**
     * 绑定游戏榜单列表
     */
    private fun getBinderList(list: List<GameTopList>): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListGameItemBinder>()
        list.map{
            var binder = TopListGameItemBinder(mContext, it)
            binderList.add(binder)
        }
        return binderList
    }

}