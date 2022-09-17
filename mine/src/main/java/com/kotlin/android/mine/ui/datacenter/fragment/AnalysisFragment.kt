package com.kotlin.android.mine.ui.datacenter.fragment

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mine.databinding.MineFragAnalysisBinding
import com.kotlin.android.mine.ui.datacenter.adapter.TagsListAdapter
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.frag_collection.*

/**
 *
 * @Package:        com.kotlin.android.mine.ui.datacenter.fragment
 * @ClassName:      AnalysisFragment
 * @Description:    单篇分析
 * @Author:         haoruigang
 * @CreateDate:     2022/3/21 09:57
 */
class AnalysisFragment : BaseVMFragment<AnalysisViewModel, MineFragAnalysisBinding>() {

    private var mAnalysisAdapter: MultiTypeAdapter? = null

    private var mIsInitData: Boolean = false
    private var isRefresh: Boolean = false
    private var nextStamp: String? = null

    companion object {
        fun newInstance() = AnalysisFragment()
    }

    override fun initVM(): AnalysisViewModel = viewModels<AnalysisViewModel>().value

    override fun initView() {
        mBinding?.apply {
            tagRv.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = RecyclerView.HORIZONTAL
                }
                adapter = mTagAdapter
            }

            mAnalysisAdapter = createMultiTypeAdapter(analysisRv, LinearLayoutManager(context))

            refreshLayout.setOnRefreshListener {
                loadContentData(true)
            }
            refreshLayout.setOnLoadMoreListener {
                loadContentData(false)
            }

            stateView.setMultiStateListener(object : MultiStateView.MultiStateListener {
                override fun onMultiStateChanged(viewState: Int) {
                    if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                        loadContentData(false)
                    }
                }

            })
        }
    }

    private fun loadContentData(isMore: Boolean) {
        isRefresh = isMore
        mViewModel?.apply {
            activity?.apply {
                getAnalysis(
                    type = cententType,
                    nextStamp = if (isMore) "" else nextStamp,
                    isRefresh = isMore)
            }
        }
    }

    private fun loadComplete() {
        refreshLayout?.finishRefresh()
        refreshLayout?.finishLoadMore()
    }

    // 内容分类切换 adapter
    private val mTagAdapter by lazy {
        TagsListAdapter(fun(data: DataCenterViewBean.Tags, pos: Int, adapter: TagsListAdapter) {
            data.isSelect = true
            mViewModel?.apply {
                when (data.index) {
                    0L -> { // 文章
                        cententType = 4L
                    }
                    1L -> {   // 长影评
                        cententType = 3L
                    }
                    2L -> {   // 帖子
                        cententType = 2L
                    }
                    3L -> {   // 视频
                        cententType = 5L
                    }
                    4L -> {   // 音频
                        cententType = 6L
                    }
                    5L -> {   // 日志
                        cententType = 1L
                    }
                }

                tagNames.value?.forEachIndexed { index, tags ->
                    if (pos != index) {
                        tags.isSelect = false
                    }
                }
                adapter.notifyDataSetChanged()

                mBinding?.refreshLayout?.autoRefresh()
            }
        })
    }


    override fun initData() {
        mViewModel?.getQueryArticleUser()
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        mBinding?.stateView?.setViewState(state)
    }

    override fun onResume() {
        super.onResume()
        if (mIsInitData.not()) {
            mBinding?.refreshLayout?.autoRefresh()
        }
    }

    override fun startObserve() {
        getArticleUserObserve()
        observeLiveData(mViewModel?.analysisStatic) {
            this.mIsInitData = true
            it?.apply {
                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadComplete()
                    this@AnalysisFragment.nextStamp = this.nextStamp
                    if (!isRefresh) {
                        mAnalysisAdapter?.notifyAdapterAdded(this.list)
                    } else {
                        mAnalysisAdapter?.notifyAdapterDataSetChanged(this.list)
                        if (this.list.isEmpty()) {
                            setContentState(MultiStateView.VIEW_STATE_EMPTY)
                        }
                    }
                    mBinding?.refreshLayout?.setNoMoreData(noMoreData)

                }
                netError?.run {
                    loadComplete()
                    if (mAnalysisAdapter?.itemCount ?: 0 == 0) {//如果没有数据是显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
                error?.run {
                    loadComplete()
                    if (mAnalysisAdapter?.itemCount ?: 0 == 0) {//如果页面上没有显示数据，需要显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }
            }
        }
    }

    /**
     * 当前文章用户信息Observe
     */
    private fun getArticleUserObserve() {
        mViewModel?.queryArticleUserState?.observe(this) {
            it.apply {
                if (isEmpty) {
                    // 单篇分析Tab
                    switchTab()
                }
                success.apply {
                    // 音频：如该账号不能发布音频该模块不可点击
                    mViewModel?.mCannotPublishAudio = this != null && this.type != null

                    // 单篇分析Tab
                    switchTab()

                }
                netError?.run {
                    showToast(this)
                    // 单篇分析Tab
                    switchTab()
                }
                error?.run {
                    showToast(this)
                    // 单篇分析Tab
                    switchTab()
                }
            }
        }
    }

    private fun switchTab() {
        mViewModel?.apply {
            var tagNameUi = arrayListOf<DataCenterViewBean.Tags>()
            // if (tagNames.value == null) {
                tagNameUi.add(DataCenterViewBean.Tags(0, true, "文章"))
                tagNameUi.add(DataCenterViewBean.Tags(1, false, "长影评"))
                tagNameUi.add(DataCenterViewBean.Tags(2, false, "帖子"))
                tagNameUi.add(DataCenterViewBean.Tags(3, false, "视频"))
                if (mCannotPublishAudio) { // 能发布音频展示该模块
                    tagNameUi.add(DataCenterViewBean.Tags(4, false, "音频"))
                }
                tagNameUi.add(DataCenterViewBean.Tags(5, false, "日志"))
                setTagNames(tagNameUi)
            /*} else {
                tagNameUi = tagNames.value as ArrayList<DataCenterViewBean.Tags>
            }*/
            mTagAdapter.setData(tagNameUi)

        }
    }

}