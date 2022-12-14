package com.kotlin.android.home.ui.toplist

import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.app.data.entity.toplist.TopListItem
import com.kotlin.android.home.BR
import com.kotlin.android.home.R
import com.kotlin.android.home.ui.toplist.adapter.TopListDetailMovieItemBinder
import com.kotlin.android.home.ui.toplist.adapter.TopListDetailPersonItemBinder
import com.kotlin.android.home.ui.toplist.constant.TopListConstant
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast

import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.act_toplist_detail.*
import kotlinx.android.synthetic.main.layout_toplist_detail_head.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_TOPIC_LIST
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState
import com.kotlin.android.home.databinding.ActToplistDetailBinding
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.copyToClipboard
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.user.afterLogin
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.user.UserManager
import kotlinx.android.synthetic.main.act_toplist_detail.commentImgLayout
import kotlinx.android.synthetic.main.act_toplist_detail.publishCommentView
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.isShowSoftInput
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.heightValue
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.router.ext.getProvider

/**
 * @author vivian.wei
 * @date 2020/7/15
 * @desc ??????/?????????????????????
 */
@Route(path = RouterActivityPath.Home.PAGER_TOPLIST_DETAIL_ACTIVITY)
class TopListDetailActivity : BaseVMActivity<TopListDetailViewModel, ActToplistDetailBinding>(),
        MultiStateView.MultiStateListener {

    companion object {
        const val PAGE_SIZE = 10
        val TITLE_HEIGHT = 44.dp                    // px
        const val HEAD_COVER_ALPHA = 0.2f           // ?????????

        const val WANT_SEE_ACTION_POSITIVE = 1L     // ??????
        const val WANT_SEE_ACTION_CANCEL = 2L       // ????????????

        const val COMMENT_TITLE_TOP_MARGIN = 70     // ????????????title??????item????????????  px

        const val DES_MINE_LINE = 2                 // ???????????????
        const val DES_MAX_LINE = 1000               // ???????????????
    }

    private val mMainProvider = getProvider(IMainProvider::class.java)

    private var mTopListId: Long = 0
    private var mTopListType: Long = TopListConstant.TOPLIST_TYPE_MOVIE
    private lateinit var mAdapter: MultiTypeAdapter
    private var mPageIndex = 1
    private var mWantBean: TopListItem? = null
    private var mWantBinder: MultiTypeBinder<*>? = null
    private var mIsDesExpand = false
    private var shareUrl: String = ""//??????url

    // ??????
    private lateinit var mCommentAdapter: MultiTypeAdapter
    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf() // ????????????
    private var isNewComment = false    // ??????????????????????????????
    private var isCommenting: Boolean = false//????????????????????????
    private var commentTitleBinder: CommentListTitleBinder? = null//??????title
    private var commentTotalCount = 0L//???????????????

    override fun initVM(): TopListDetailViewModel {
        mTopListId = intent.getLongExtra(TopListConstant.KEY_TOPLIST_ID, 0)

        return viewModels<TopListDetailViewModel>().value
    }

    override fun initView() {

        // ?????????title
        initTitle()
        // ??????
        mBinding?.setVariable(BR.topListDetailViewModel, mViewModel)
        // Item??????
        mAdapter = createMultiTypeAdapter(mToplistDetailRv, LinearLayoutManager(this))
        // ????????????
        createCommentAdapter()
        // ??????????????????"??????"??????
        mToplistMovieDetailHeadFollowBtn.isGone = true
        // "????????????"??????
        mToplistDetailMoreTv.isGone = true
        mToplistDetailMoreGradientLayout.isGone = true
        // "????????????"?????????
        ShapeExt.setGradientColor(mToplistDetailMoreGradientLayout,
                GradientDrawable.Orientation.BOTTOM_TOP,
                R.color.color_ffffff,
                R.color.color_0dffffff,
                0)

        initEvent()
        // ??????????????????
        initBarButton()
        // ?????????????????????
        observePraise()
        // ????????????
        initRefreshLayout()
    }

    /**
     * ?????????title
     */
    private fun initTitle() {
        val barHeight = statusBarHeight
        val titleBgHeight = barHeight + TITLE_HEIGHT // px
        mToplistDetailToolbar.heightValue = titleBgHeight
        mToplistDetailTitleCl.heightValue = titleBgHeight
        mToplistDetailTitleCl.setPadding(0, barHeight, 0, 0)
        mToplistDetailHeadInfoTitleTv.heightValue = titleBgHeight
//        //
//        var params1 = mToplistDetailToolbar.layoutParams
//        params1.height = titleBgHeight
//        mToplistDetailToolbar.layoutParams = params1
//        //
//        val params2 = mToplistDetailTitleCl.layoutParams as ConstraintLayout.LayoutParams
//        params2.topMargin = barHeight
//        mToplistDetailTitleCl.layoutParams = params2
//        //
//        val params3 = mToplistDetailHeadInfoTitleTv.layoutParams as ConstraintLayout.LayoutParams
//        params3.topMargin = titleBgHeight
//        mToplistDetailHeadInfoTitleTv.layoutParams = params3

        // ??????????????????
        ShapeExt.setGradientColor(mToplistDetailHeadCoverView,
                GradientDrawable.Orientation.RIGHT_LEFT,
                R.color.color_4e6382,
                R.color.color_1d2736,
                0)
        mToplistDetailHeadCoverView.alpha = HEAD_COVER_ALPHA

        mToplistDetailAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            val statusBarColor = 0x00ffffff
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                val height = appBarLayout.totalScrollRange
                var pos = abs(verticalOffset)

                //??????????????????title????????????
                if (pos < 0) {
                    pos = 0
                } else if (pos > height) {
                    pos = height
                }
                val ratio = pos / height.toFloat()
                val alpha = (255 * ratio).toInt()
                val color = statusBarColor and 0x00ffffff or (alpha shl 24)
                // ???????????????
//                mToplistDetailTitleBgCl.setBackgroundColor(color)
                mToplistDetailTitleCl.setBackgroundColor(color)
                // ???????????????Icon
                if (ratio < 0.5) {
                    mToplistDetailHeadBackIv.setImageResource(R.drawable.ic_back_light)
                    mToplistDetailHeadShareIv.setImageResource(R.drawable.ic_more_light)
                } else {
                    mToplistDetailHeadBackIv.setImageResource(R.drawable.icon_back)
                    mToplistDetailHeadShareIv.setImageResource(R.drawable.ic_ver_more)
                    mToplistDetailHeadBackIv.alpha = ratio
                    mToplistDetailHeadShareIv.alpha = ratio
                }
                // ???????????????????????????
                mToplistDetailHeadTitleTv.alpha = ratio
            }
        })
    }

    /**
     * ????????????Adapter
     */
    private fun createCommentAdapter() {
        mCommentAdapter = createMultiTypeAdapter(mToplistDetailCommentRv, LinearLayoutManager(this))
        mCommentAdapter.setOnClickListener(::handleListAction)
    }

    /**
     * ????????????????????????
     */
    private fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            // ????????????
            com.kotlin.android.comment.component.R.id.likeTv -> {
                val isCancel = (binder as CommentListBinder).bean.isLike()
                handlePraiseUp(isCancel, binder)
            }
            // ????????????
            com.kotlin.android.comment.component.R.id.deleteTv -> {
                deleteComment(binder)
            }
            R.id.hotTv -> {//????????????
                isNewComment = false
                handleCommentChange()
            }
            R.id.newTv -> {//????????????
                isNewComment = true
                handleCommentChange()
            }

        }
    }

    private fun handleCommentChange() {
        mCommentAdapter.notifyAdapterRemoved(hotCommentBinderList.filter { it is CommentListBinder || it is CommentListEmptyBinder }.toMutableList()) {
            hotCommentBinderList.removeIf { it is CommentListBinder || it is CommentListEmptyBinder }
            hotCommentBinderList.clear()
            "?????????????????????$hotCommentBinderList".d()
//            mAdapter.notifyAdapterChanged(mBinderList)
            loadCommentData(false)
        }
    }

    private fun initEvent() {
        // ??????????????????
        mToplistDetailHeadBackCl.onClick {
            onBackPressed()
        }

        // ????????????
        mToplistDetailHeadShareIv.onClick {
            // ??????????????????
            mViewModel?.getShareInfo(CommConstant.SHARE_TYPE_SUBJECT, mTopListId)
        }

        // ????????????
        mToplistDetailHeadInfoIntroTv.onClick {
            it.maxLines = if (mIsDesExpand) DES_MINE_LINE else DES_MAX_LINE
            mIsDesExpand = !mIsDesExpand
        }

        mMultiStateView.setMultiStateListener(this)
    }

    /**
     * ?????????????????????
     */
    private fun initRefreshLayout() {
        mRefreshLayout?.apply {
            setEnableRefresh(false)
            setEnableLoadMore(true)
            setOnLoadMoreListener {
                loadCommentData(true)
            }
        }
    }

    override fun initData() {
        // ??????????????????
        mViewModel?.getTopListDetail(mTopListId)
        // ???????????????????????????????????????????????????????????????
        mViewModel?.getPraiseState(CommConstant.PRAISE_OBJ_TYPE_TOPIC_LIST, mTopListId)
    }

    override fun startObserve() {
        // ??????????????????
        observeTopListDetail()
        // ??????
        observeWantSee()
        // ????????????
        observeShareInfo()
        // ??????????????????????????????
        observeCurUserPraise()
        // ???????????????????????????
        observeComment()
        // ????????????
        observeDeleteComment()
        // ????????????
        observeSaveComment()

    }

    private fun observeDeleteComment() {
        //        ??????????????????
        mViewModel?.deleteCommentState?.observe(this) {
            it?.apply {
                success?.apply {
                    dismissProgressDialog()
                    if (extend is MultiTypeBinder<*>) {
                        (extend as? MultiTypeBinder<*>)?.notifyAdapterSelfRemoved()
                        hotCommentBinderList.remove(extend)
                        if (hotCommentBinderList.isEmpty()) {
                            hotCommentBinderList.add(CommentListEmptyBinder())
                            commentTitleBinder?.apply {
                                hotCommentBinderList.add(0,this)
                            }
                            mCommentAdapter.notifyAdapterDataSetChanged(hotCommentBinderList)
                        }
                        updateCommentTitle(true)
                        updateCommentTotal(true)
                        initBarButtonItem(BarButtonItem.Type.COMMENT, commentTotalCount)
                    }
                }
                netError?.apply {
                    dismissProgressDialog()
                    showToast(this)
                }
                error?.apply {
                    dismissProgressDialog()
                    showToast(this)
                }
            }
        }
    }

    private fun observeComment() {
//        ????????????
        mViewModel?.hotCommentListState?.observe(this) {
            it?.apply {
                val isLoadMore = this.loadMore
                success?.apply {
                    mRefreshLayout?.finishLoadMore()
                    hotCommentBinderList.addAll(this.commentBinderList)
                    mCommentAdapter.notifyAdapterAdded(hotCommentBinderList)
                    mRefreshLayout?.setNoMoreData(noMoreData)

                    // ?????????, ??????????????????title?????????????????????????????????
                    if(isLoadMore.not()) {
                        commentTotalCount = this.totalCount
                        if(commentTotalCount > 0) {
                            commentTitleBinder?.let { binder->
                                binder.bean.totalCount = commentTotalCount
                                commentTitleBinder?.notifyAdapterSelfChanged()
                            }
                            initBarButtonItem(BarButtonItem.Type.COMMENT, commentTotalCount)
                        }
                    }
                }
                error?.apply {
                    mRefreshLayout?.finishLoadMore()
                    this.showToast()
                }
                netError?.apply {
                    mRefreshLayout?.finishLoadMore()
                    this.showToast()
                }
            }
        }

//        ????????????
        mViewModel?.newCommentListState?.observe(this) {
            it?.apply {
                success?.apply {
                    mRefreshLayout?.finishLoadMore()
                    hotCommentBinderList.addAll(this.commentBinderList)
                    if (isRefresh) {
                        commentTitleBinder?.apply {
                            hotCommentBinderList.add(0, this)
                        }
                    }
                    mCommentAdapter.notifyAdapterDataSetChanged(hotCommentBinderList)
                    mRefreshLayout?.setNoMoreData(noMoreData)

                }
                error?.apply {
                    mRefreshLayout?.finishLoadMore()
                    this.showToast()
                }
                netError?.apply {
                    mRefreshLayout?.finishLoadMore()
                    this.showToast()
                }
            }

        }
    }

    /**
     * observe ??????????????????
     */
    private fun observeTopListDetail() {
        mViewModel?.topListUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    showTopList(this)
                    /**
                     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
                     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                     * ?????????????????????????????????
                     */
                    mRefreshLayout?.setEnableLoadMore(this.enableComment == true)
                    commentTitleBinder = CommentListTitleBinder(CommentTitleViewBean(commentTotalCount))
                    commentTitleBinder?.apply {
                        mCommentAdapter.notifyAdapterAdded(this)
                    }
                    if (this.enableComment == true) {
                        loadCommentData(false)
                    } else {
                        publishCommentView?.inputEnable(false)
                    }

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
     * observe ??????
     */
    private fun observeWantSee() {
        mViewModel?.wantSeeUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (this.status == 1L) {
                        // ??????
                        updateWantSeeBtn()
                    } else {
                        var msg = this.statusMsg ?: ""
                        if (msg.isNotEmpty()) {
                            showToast(msg)
                        }
                    }
                }

                error?.showToast()
                netError?.showToast()
            }
        }
    }

    /**
     * observe ????????????
     */
    private fun observeShareInfo() {
        mViewModel?.shareUIState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // ??????????????????
                    shareUrl = url.orEmpty()
                    showShareDialog(ShareEntity.build(this), ShareFragment.LaunchMode.ADVANCED,
                            SharePlatform.COPY_LINK, event = shareAction)
                }
                error?.showToast()
                netError?.showToast()

            }
        }
    }

    /**
     * observe ????????????
     */
    private fun observeCurUserPraise() {
        mViewModel?.praiseUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    // ?????????????????????
                    initPraiseLayout(this)
                }
            }
        }
    }

    /**
     * observe ????????????
     */
    private fun observeSaveComment() {
        // ????????????
        mViewModel?.saveCommentState?.observe(this) {
            it?.apply {
                isCommenting = showLoading
                if (showLoading.not()) {
                    dismissProgressDialog()
                }

                success?.apply {
                    showToast(R.string.comment_component_publish_success)

                    sendMessage(publishCommentView?.text.orEmpty(), this)
                }

                netError?.showToast()
                error?.showToast()
            }
        }
    }

    /**
     * observe ?????????????????????
     */
    private fun observePraise() {
        // ?????????????????????
        mViewModel?.praiseUpState?.observe(this,
                CommonObserver(this,
                        CommonObserver.TYPE_PRAISE_UP,
                        publishCommentView,
                        BarButtonItem.Type.PRAISE))
    }

    /**
     * ????????????????????????"??????/??????"???????????????
     */
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                // ??????????????????
                mViewModel?.getTopListDetail(mTopListId)
            }
        }
    }

    override fun onBackPressed() {
        if (publishCommentView?.style == PublishCommentView.Style.EDIT_WITHOUT_MOVIE) {
            publishCommentView?.resetInput()
            return
        }
        super.onBackPressed()
    }

    /**
     * ????????????
     */
    private fun showTopList(topListInfo: TopListInfo) {
        topListInfo?.let {
            mTopListType = it.type ?: TopListConstant.TOPLIST_TYPE_MOVIE

            // item???
            mToplistDetailHeadInfoCountTv.text = String.format("%d?????????", it.itemsTotalCount)
            // ???????????????
            var url = it.coverImg ?: ""
            if(url.isEmpty()) {
                // ????????????item????????????
                url = getFirstItemCoverImg(it.items)
            }
            // ????????????
            val height = mToplistDetailHeadBgIv.height
            mToplistDetailHeadBgIv.loadImage(
                data = url,
                width = screenWidth,
                height = height,
                blurRadius = 25F,
                blurSampling = 4F
            )
            // items
            it.items?.let { items ->
                // ????????????/????????????
                showItems(items)
                // ??????"????????????"
                mToplistDetailMoreTv.setOnClickListener {
                    showItems(items)
                }
            }
        }
    }

    /**
     * ???????????????item????????????
     */
    private fun getFirstItemCoverImg(items: List<TopListItem> ?): String {
        var url = ""
        items?.let {
            it.firstOrNull()?.let { item ->
                url = item.coverImg ?: ""
            }
        }
        return url
    }

    /**
     * ????????????/????????????
     */
    private fun showItems(items: List<TopListItem>) {
        items.let {
            var start = (mPageIndex - 1) * PAGE_SIZE
            var end = Math.min(items.size, mPageIndex * PAGE_SIZE)
            var pageItems = items.subList(start, end)
            if (mTopListType == TopListConstant.TOPLIST_TYPE_MOVIE || mTopListType == TopListConstant.TOPLIST_TYPE_TV) {
                mAdapter.notifyAdapterAdded(getMovieBinderList(pageItems))
            } else if (mTopListType == TopListConstant.TOPLIST_TYPE_PERSON) {
                mAdapter.notifyAdapterAdded(getPersonBinderList(pageItems))
            }
            var hasMore = end < items.size
            mToplistDetailMoreTv.isVisible = hasMore
            mToplistDetailMoreGradientLayout.isVisible = hasMore
            if (hasMore) {
                mPageIndex++
            } else {
                // ??????????????????title??????item????????????
                val params = mToplistDetailCommentRv.layoutParams as ConstraintLayout.LayoutParams
                params.topMargin = COMMENT_TITLE_TOP_MARGIN
                mToplistDetailCommentRv.layoutParams = params
            }
        }
    }

    private fun getMovieBinderList(items: List<TopListItem>): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListDetailMovieItemBinder>()
        items.map {
            var binder = TopListDetailMovieItemBinder(it, ::onClickWantSee)
            binderList.add(binder)
        }
        return binderList
    }

    private fun getPersonBinderList(items: List<TopListItem>): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListDetailPersonItemBinder>()
        items.map {
            var binder = TopListDetailPersonItemBinder(it)
            binderList.add(binder)
        }
        return binderList
    }

    /**
     * ????????????????????????
     */
    private var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when(platform){
            SharePlatform.COPY_LINK -> {    //????????????
                copyToClipboard(shareUrl)
                showToast(R.string.share_copy_link_success)
                dismissShareDialog()
            }
        }

    }

    /**
     * ??????"??????"??????
     */
    private fun onClickWantSee(bean: TopListItem, binder: MultiTypeBinder<*>) {
        if (isLogin()) {
            bean.movieInfo?.let {
                it.movieId?.let { movieId ->
                    mWantBean = bean
                    mWantBinder = binder
                    // ?????????
                    mViewModel?.setWantSee(movieId,
                            if (it.currentUserWantSee == true) WANT_SEE_ACTION_CANCEL else WANT_SEE_ACTION_POSITIVE,
                            getCurYear())
                }
            }
        } else {
            // ????????????
            mMainProvider?.startLoginActivity(null)
        }
    }

    /**
     * ??????"??????"??????
     */
    private fun updateWantSeeBtn() {
        mWantBean?.movieInfo?.let {
            it.currentUserWantSee = !(it.currentUserWantSee ?: false)
            mWantBinder?.notifyAdapterSelfChanged()
        }
    }

    /**
     * ??????????????????
     */
    private fun getCurYear(): Long {
        return Calendar.getInstance().get(Calendar.YEAR).toLong()
    }

    /*------------------- ???????????? -------------------*/

    /**
     * ???????????????????????????
     */
    private fun initBarButton() {
        publishCommentView?.apply {
            style = PublishCommentView.Style.TOPLIST
            isWithoutMovie = true
            inputEnable(true)
            action = { barType, isSelected ->
                when (barType) {
                    BarButtonItem.Type.COMMENT -> {
                        // ???????????????????????????????????????  ????????????
                        handleScrollToComment()
                    }
                    BarButtonItem.Type.PRAISE -> {  //??????
                        handlePraiseUp(getSelectedStatusByType(barType), this@TopListDetailActivity)
                    }
                    BarButtonItem.Type.SEND -> {    //????????????
                        saveComment(text)
                    }
                    BarButtonItem.Type.PHOTO -> {   //??????
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos = { photos ->
                            photos.e()
                            if (photos.isNotEmpty()) {
                                commentImgLayout.setPhotoInfo(photos[0])
                            }
                        }
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        showOrHideSoftInput(this.getEditTextView())
                    }
                }
            }
            editAction = {//?????????????????????????????????????????????????????????
                afterLogin {
                    setEditStyle()
                }
            }
        }
        KeyBoard.assistActivity(this, true)
    }

    /**
     * ??????????????????????????????????????????????????????
     * ???????????????????????????
     */
    private fun handleScrollToComment() {
        val linearLayoutManager = mToplistDetailCommentRv?.layoutManager as? LinearLayoutManager
        val findFirstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition() ?: 0
        mToplistDetailCommentRv?.moveToPosition(if (findFirstVisibleItemPosition >= 1) 0 else 1)
    }

    /**
     * ????????????????????????????????????????????????
     */
    private fun initPraiseLayout(praiseState: PraiseState) {
        publishCommentView?.apply {
            val userPraise = praiseState.currentUserPraise ?: 0L//??????????????????
            isSelectedByType(BarButtonItem.Type.PRAISE, userPraise == 1L)//???????????????
            initBarButtonItem(BarButtonItem.Type.PRAISE, praiseState.upCount)
        }
    }

    // ?????????????????????Icon??????/??????
    private fun initBarButtonItem(type: BarButtonItem.Type, count: Long) {
        publishCommentView?.apply {
            try {
                setTipsByType(type, count)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        if (extend is CommentListBinder) {
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(CommConstant.getPraiseUpType(PRAISE_OBJ_TYPE_TOPIC_LIST, CommConstant.COMMENT_PRAISE_ACTION_COMMENT), commentId, isCancel, extend)
        } else {
            mViewModel?.praiseUpOrCancel(PRAISE_OBJ_TYPE_TOPIC_LIST, mTopListId, isCancel, extend)
        }
    }

    /**
     * ??????loading
     */
    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    /**
     * ????????????
     */
    private fun saveComment(text: String) {
        if (TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(commentImgLayout?.getImagePath())) {
            showToast(com.kotlin.android.comment.component.R.string.comment_detail_cannt_send_comment)
            return
        }
        if (isCommenting) return
        isCommenting = true
        showInteractiveDialog()
        mViewModel?.saveComment(PRAISE_OBJ_TYPE_TOPIC_LIST, mTopListId,
                path = commentImgLayout?.getImagePath().orEmpty(), content = text)
    }

    private fun sendMessage(text: String, commentId: Long) {
//        ???????????????commentId
        var binderList = hotCommentBinderList
        val index = binderList.indexOfFirst { it is CommentListBinder }
        val commentListBinder = CommentListBinder(this, CommentViewBean(commentId = commentId,
                userName = UserManager.instance.nickname,
                userId = UserManager.instance.userId,
                userPic = UserManager.instance.userAvatar.orEmpty(),
                commentContent = text,
                publishDate = formatPublishTime(System.currentTimeMillis()),
                type = PRAISE_OBJ_TYPE_TOPIC_LIST,
                objId = mTopListId, commentPic = mViewModel?.getUpLoadImageUrl(commentImgLayout?.getImagePath().orEmpty()).orEmpty())
        )

        commentImgLayout?.gone()
        commentImgLayout?.clearImagePath()

        if (index < 0) {
            binderList.removeIf { it is CommentListEmptyBinder }
            binderList.add(commentListBinder)
        } else {
            binderList.add(index, commentListBinder)
        }
        commentTitleBinder?.apply {
            bean.totalCount = commentTotalCount
            binderList.add(0, this)
        }
        mCommentAdapter.notifyAdapterDataSetChanged(binderList)
        updateCommentTitle(false)
        updateCommentTotal(false)

        publishCommentView?.apply {
            style = PublishCommentView.Style.TOPLIST
            if (isShowSoftInput()){
                getEditTextView()?.clearFocus()
                getEditTextView()?.hideSoftInput()
            }
        }
        initBarButtonItem(BarButtonItem.Type.COMMENT, commentTotalCount)
    }

    private fun updateCommentTotal(isDelete: Boolean) {
        if (isDelete) {
            if (commentTotalCount > 0) commentTotalCount-- else 0
        } else {
            commentTotalCount++
        }
    }

    /**
     * ????????????
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(CommConstant.PRAISE_OBJ_TYPE_TOPIC_LIST, (binder as CommentListBinder).bean.commentId, binder)
    }

    /**
     * ??????????????????
     */
    private fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, mTopListId, CommConstant.PRAISE_OBJ_TYPE_TOPIC_LIST, isNewComment, isMore)
    }


    /**
     * ??????????????????
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        commentTitleBinder?.apply {
            if (isDelete) {
                if (bean.totalCount > 0) bean.totalCount-- else 0L
            } else {
                bean.totalCount++
            }
        }?.also {
//            mCommentAdapter.notifyAdapterAdded(it)
            it.notifyAdapterSelfChanged()
        }
    }

    /**
     * ??????????????????
     */
    private fun updateCommentTitleList(list: MutableList<MultiTypeBinder<*>>, isDelete: Boolean) {
        list.filter { it is CommentListTitleBinder }.forEach {
            (it as CommentListTitleBinder).bean.totalCount = if (isDelete) {
                if (it.bean.totalCount - 1 >= 0) it.bean.totalCount - 1 else 0
            } else {
                it.bean.totalCount + 1
            }
        }
        mCommentAdapter.notifyAdapterAdded(list)
    }

    /*------------------- ???????????? End -------------------*/

}