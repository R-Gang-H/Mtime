package com.kotlin.android.community.family.component.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.FragPostContentFamilyBinding
import com.kotlin.android.community.post.component.item.adapter.CommunityPostBinder
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.core.copyToClipboard
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 * @des 家族帖子列表页面
 * @author zhangjian
 * @date 2022/3/29 14:40
 */
class FamilyDetailFragment : BaseVMFragment<FamilyDetailViewModel, FragPostContentFamilyBinding>(),
    OnLoadMoreListener, OnRefreshListener, MultiStateView.MultiStateListener {

    private var mAdapter: MultiTypeAdapter? = null
    private var mCommViewModel: CommViewModel<CommunityPostBinder>? = null

    var groupId = 0L
    var sectionId = 0L
    var sort = 0L
    var essence = false

    companion object {
        val KEY_GROUP_ID = "family_post_group_ID"
        val KEY_SECTION_ID = "family_post_section"
        val KEY_SORT = "family_post_sort"
        val KEY_ESSENCE = "family_post_essence"

        fun getPostListFragment(
            groupId: Long,
            sectionId: Long,
            essence: Boolean,
            sort: Long
        ): FamilyDetailFragment {
            val bundle = Bundle()
            bundle.putLong(KEY_GROUP_ID, groupId)
            bundle.putLong(KEY_SECTION_ID, sectionId)
            bundle.putLong(KEY_SORT, sort)
            bundle.putBoolean(KEY_ESSENCE, essence)
            val fragment = FamilyDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun initView() {
        groupId = arguments?.getLong(KEY_GROUP_ID) ?: 0L
        sectionId = arguments?.getLong(KEY_SECTION_ID) ?: 0L
        sort = arguments?.getLong(KEY_SORT) ?: 0L
        essence = arguments?.getBoolean(KEY_ESSENCE) ?: false


        mBinding?.mRefreshLayout?.setOnLoadMoreListener(this)
        mBinding?.mRefreshLayout?.setOnRefreshListener(this)
        mBinding?.mPostListMultiStateView?.setMultiStateListener(this)
        mBinding?.mFamilyDetailPostRv?.apply {
            mAdapter = createMultiTypeAdapter(this)
            mAdapter?.setOnClickListener(::onBinderClick)
        }
    }

    override fun initVM(): FamilyDetailViewModel? {
        mCommViewModel = viewModels<CommViewModel<CommunityPostBinder>>().value
        return super.initVM()
    }

    override fun startObserve() {
        //帖子列表数据
        registerPostListObserve()
        //想看、已想看操作结果监听
        registerWantToSeeObserve()
        //点赞、取消点赞操作结果监听
        registerPraiseUpObserve()
        //点踩、取消点踩操作结果监听
        registerPraiseDownObserve()
        //社区pk帖子投票结果监听
        registerVoteObserve()
        //注册获取分享接口监听
        registerShareObserve()
    }

    override fun initData() {
    }

    override fun onRefresh(any: Any?) {
        super.onRefresh(any)
        if(any is Long){
            sort = any
            mAdapter?.notifyAdapterClear()
            mBinding?.mRefreshLayout?.resetNoMoreData()
            mViewModel?.loadPostListByStamp(isRefresh = true, any, sectionId, groupId)
        }
    }

    //注册帖子列表回调监听
    private fun registerPostListObserve() {
        mViewModel?.uiListStampState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)
                binders?.apply {
                    showPostListData(this, isRefresh, noMoreData)
                }
                error?.apply {
                    if (!isRefresh) {
                        showToast(this)
                        mBinding?.mRefreshLayout?.finishLoadMore(false)
                    } else {
                        mBinding?.mPostListMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }
                netError?.apply {
                    if (!isRefresh) {
                        showToast(this)
                        mBinding?.mRefreshLayout?.finishLoadMore(false)
                    } else {
                        mBinding?.mPostListMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        }
    }

    //想看、已想看操作结果监听
    private fun registerWantToSeeObserve() {
        mCommViewModel?.mCommWantToSeeUIState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        extend.wantToSeeChanged()
                    } else {
                        showToast(result.statusMsg)
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(netError)
                }
            }
        }
    }

    //点赞、取消点赞
    private fun registerPraiseUpObserve() {
        mCommViewModel?.mCommPraiseUpUIState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        if (addedValue > 100L) {
                            //评论类型的点赞
                            extend.commentPraiseUpChanged()
                        } else {
                            extend.praiseUpChanged()
                        }
                    } else {
                        showToast(result.bizMsg.orEmpty())
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(netError)
                }
            }
        }
    }

    //点踩、取消点踩操作结果监听
    private fun registerPraiseDownObserve() {
        mCommViewModel?.mCommPraiseDownUIState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        if (addedValue > 100L) {
                            //评论类型的点踩
                            extend.commentPraiseDownChanged()
                        }
                    } else {
                        showToast(result.bizMsg.orEmpty())
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(netError)
                }
            }
        }
    }

    //社区pk帖子投票结果监听
    private fun registerVoteObserve() {
        mCommViewModel?.mCommVoteUIState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        extend.voteChanged(addedValue)
                    } else {
                        showToast(result.bizMsg.orEmpty())
                    }
                }
                error?.apply {
                    showToast(this)
                }
                netError?.apply {
                    showToast(netError)
                }
            }
        }
    }

    //注册获取分享接口监听
    private fun registerShareObserve(){
        mCommViewModel?.mCommShareUIState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    showShare(this)
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(netError)
                }
            }
        }
    }

    private fun showShare(share: CommonShare) {
        activity?.apply {
            showShareDialog(
                ShareEntity.build(share),
                ShareFragment.LaunchMode.ADVANCED,
                SharePlatform.COPY_LINK
            ) {
                when (it) {
                    SharePlatform.COPY_LINK -> {//复制粘贴
                        copyToClipboard(share.url.orEmpty())
                        showToast(R.string.share_copy_link_success)
                        dismissShareDialog()
                    }
                }
            }
        }
    }


    //显示帖子列表数据
    private fun showPostListData(
        data: List<MultiTypeBinder<*>>,
        isRefresh: Boolean,
        noMoreData: Boolean
    ) {
        if (isRefresh) {
            if (data.isNullOrEmpty()) {
//                mBinding?.mPostListMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                //帖子为空
                mBinding?.mPostListMultiStateView?.setEmptyResource(resid = R.string.community_detail_family_post_empty)
                mBinding?.mPostListMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                return
            }
            mBinding?.mPostListMultiStateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
            mAdapter?.notifyAdapterDataSetChanged(data) {
                if (noMoreData) {
                    mBinding?.mRefreshLayout?.finishRefreshWithNoMoreData()
                } else {
                    mBinding?.mRefreshLayout?.finishRefresh()
                }
            }
        } else {
            mAdapter?.notifyAdapterAdded(data) {
                if (noMoreData) {
                    mBinding?.mRefreshLayout?.finishLoadMoreWithNoMoreData()
                } else {
                    mBinding?.mRefreshLayout?.finishLoadMore()
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadPostListByStamp(isRefresh = false, sort, sectionId, groupId)
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is CommunityPostBinder -> {
                //社区帖子相关点击事件
                onCommunityPostBinderClickListener(view, binder)
            }
        }
    }

    //社区帖子相关点击事件
    private fun onCommunityPostBinderClickListener(view: View, binder: CommunityPostBinder) {
        when (view.id) {
            R.id.mCommunityPostLikeTv -> {
                //帖子内容的点赞、取消点赞
                onPraiseUpBtnClick(
                    isLike = binder.post.isLike,
                    objType = binder.post.praiseObjType,
                    objId = binder.post.id,
                    binder = binder
                )
            }
            R.id.mCommunityPostMovieBtnFl -> {
                //已想看 //想看
                onWanToSeeBtnClick(
                    movieId = binder.post.movieId,
                    btnState = binder.post.movieBtnState,
                    binder = binder
                )
            }
            R.id.mCommunityPostCommentLikeTv -> {
                //帖子的热门评论点赞、取消点赞
                binder.mCurClickComment?.apply {
                    onPraiseUpBtnClick(
                        isLike = isLike,
                        objType = getPraiseType(binder.post.type),
                        objId = id,
                        binder = binder
                    )
                }
            }
            R.id.mCommunityPostCommentDislikeTv -> {
                //帖子的热门评论点踩、取消点踩
                binder.mCurClickComment?.apply {
                    onPraiseDownBtnClick(
                        isDislike = isDislike,
                        objType = getPraiseType(binder.post.type),
                        objId = id,
                        binder = binder
                    )
                }
            }
            R.id.mCommunityPostPkPositiveBtn -> {
                //社区PK帖子投票
                onVoteClick(
                    objId = binder.post.id,
                    voteId = binder.post.opinions?.get(0)?.id ?: 0,
                    binder = binder
                )
            }
            R.id.mCommunityPostPkNegativeBtn -> {
                //社区PK帖子投票
                onVoteClick(
                    objId = binder.post.id,
                    voteId = binder.post.opinions?.get(1)?.id ?: 0,
                    binder = binder
                )
            }
            R.id.mCommunityPostShareIv -> {
                binder.post.let {
                    mCommViewModel?.getShareInfo(
                        type = it.getShareType(),
                        relateId = it.id
                    )
                }
            }
        }
    }

    /**
     * 社区PK帖子投票
     */
    private fun onVoteClick(objId: Long, voteId: Long, binder: CommunityPostBinder) {
        afterLogin {
            mCommViewModel?.communityVote(
                objType = 1L,
                objId = objId,
                voteId = voteId,
                extend = binder
            )
        }
    }

    /**
     * 点踩、取消点踩
     */
    private fun onPraiseDownBtnClick(
        isDislike: Boolean,
        objType: Long,
        objId: Long,
        binder: CommunityPostBinder
    ) {
        afterLogin {
            mCommViewModel?.praiseDown(
                action = if (isDislike) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
                objType = objType,
                objId = objId,
                extend = binder
            )
        }
    }

    /**
     * 点赞、取消点赞
     */
    private fun onPraiseUpBtnClick(
        isLike: Boolean,
        objType: Long,
        objId: Long,
        binder: CommunityPostBinder
    ) {
        afterLogin {
            mCommViewModel?.praiseUp(
                action = if (isLike) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
                objType = objType,
                objId = objId,
                extend = binder
            )
        }
    }

    /**
     * 已想看/想看按钮的点击事件
     */
    private fun onWanToSeeBtnClick(movieId: Long, btnState: Long, binder: CommunityPostBinder) {
        afterLogin {
            mCommViewModel?.getMovieWantToSee(
                movieId = movieId,
                flag = if (btnState == CommConstant.MOVIE_BTN_STATE_WANT_SEE)
                    CommConstant.MOVIE_WANT_SEE_FLAG else CommConstant.MOVIE_CANCEL_WANT_SEE_FLAG,
                extend = binder
            )
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_NO_NET,
            MultiStateView.VIEW_STATE_ERROR -> {
                mViewModel?.loadPostListByStamp(
                    isRefresh = false,
                    type = sort,
                    sectionId = sectionId,
                    groupID = groupId
                )
            }
        }
    }

}