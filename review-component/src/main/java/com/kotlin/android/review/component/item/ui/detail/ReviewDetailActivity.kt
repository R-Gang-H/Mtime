package com.kotlin.android.review.component.item.ui.detail

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.comment.component.DELETE_COMMENT
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.helper.CommentHelper
import com.kotlin.android.comment.component.helper.DetailScrollHelper
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.progressdialog.ProgressDialogFragment
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.review.component.BR
import com.kotlin.android.review.component.R
import com.kotlin.android.review.component.databinding.ActivityReviewComponentDetailBinding
import com.kotlin.android.user.afterLogin

import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.review.IReviewProvider
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.ugc.detail.component.bean.MovieViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleViewBean
import com.kotlin.android.ugc.detail.component.binder.*
import com.kotlin.android.ugc.detail.component.contentCanShare
import com.kotlin.android.ugc.detail.component.isPublished
import com.kotlin.android.ugc.detail.component.observe.WantToSeeObserve
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_NEED_TO_COMMENT
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_REC_ID
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_review_component_detail.*

/**
 * ????????????, ????????????
 */

@Route(path = RouterActivityPath.Review.PAGE_REVIEW_DETAIL_ACTIVITY)
class ReviewDetailActivity :
    BaseVMActivity<ReviewDetailViewModel, ActivityReviewComponentDetailBinding>() {

    companion object {
        const val REVIEW_CONTENT_ID = "review_content_id"
        const val REVIEW_TYPE = "review_type"
    }

    private var contentId: Long = 0L//??????id
    private var recId: Long = 0L//??????id
    private var type: Long = 0L//????????????
    private var contentStatus: Long = -1L//?????????????????????
    private var editBtn: CommunityContent.BtnShow? = null//??????????????????id

    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//????????????
    private val detailBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    private var isShortReview: Boolean = false//??????????????????

    //    ????????????+??????binder??????
    @Volatile
    private var mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    private var isNewComment = false//??????????????????????????????

    private val mainProvider = getProvider(IMainProvider::class.java)

    private var userId: Long = -1L//???????????????id
    private var movieScore: Float = 0f//????????????
    private var reviewMovieId: Long = 0L//??????????????????id
    private var needScrollToComment = false//????????????????????????????????????????????????????????????
    private var commonBarBean: UgcCommonBarBean? = null//????????????reset???????????????????????????
    private var isCommenting: Boolean = false//????????????????????????
    private var movie: MovieViewBean? = null//????????????

    private var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.WEI_BO,
            SharePlatform.QQ,
            SharePlatform.COPY_LINK -> {
                getShareInfoData(platform)
            }
            SharePlatform.DELETE -> {//??????
                showDeleteDialog()
            }
            SharePlatform.POSTER -> {//????????????
                val provider: IReviewProvider? = getProvider(IReviewProvider::class.java)
                provider?.startReviewShare(contentId, contentStatus.isPublished())
                dismissShareDialog()
            }
            SharePlatform.EDIT -> {//??????
                val nameCN = movie?.nameCn
                val nameEN = movie?.nameEn
                val name = if (nameCN.isNullOrEmpty()) {
                    nameEN
                } else {
                    nameCN
                }
                getProvider(IPublishProvider::class.java) {
                    startEditorActivity(
                        type = type,
                        contentId = contentId,
                        recordId = editBtn?.recId.orZero(),
                        movieId = movie?.movieId,
                        movieName = name,
//                        isLongComment = mContentType == CONTENT_TYPE_FILM_COMMENT
                    )
                }
                dismissShareDialog()
            }
        }
    }

    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(reviewRV, LinearLayoutManager(this)).apply {
            setOnClickListener(::handleListAction)
        }
    }


    private val loadFinishListener: ((Any?) -> Unit) = {
        firstInAndScrollToComment()
    }

    private var scrollHelper: DetailScrollHelper? = null

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initVariable() {
        super.initVariable()
        contentId = intent?.getLongExtra(REVIEW_CONTENT_ID, 0L) ?: 0L
        recId = intent?.getLongExtra(UGC_DETAIL_REC_ID, 0L) ?: 0L
        type = intent?.getLongExtra(REVIEW_TYPE, 0L) ?: 0L
        needScrollToComment = intent?.getBooleanExtra(UGC_DETAIL_NEED_TO_COMMENT, false) ?: false

    }

    override fun initVM(): ReviewDetailViewModel = viewModels<ReviewDetailViewModel>().value

    override fun initView() {
        initBarButton()
        initSmartRefreshLayout()
        handlePraisAndCollectionObser()//????????????publishCommenView??????????????????
        stateView.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_ERROR || viewState == MultiStateView.VIEW_STATE_NO_NET) {
                    initData()
                }
            }
        })
        scrollHelper = DetailScrollHelper(reviewRV)
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout?.apply {
            setEnableRefresh(false)
            setEnableLoadMore(true)
            setOnLoadMoreListener {
                loadCommentData(true)
            }
        }
    }

    private fun initBarButton() {
        barButton?.apply {
            isWithoutMovie = true
            style = PublishCommentView.Style.COMMENT
            inputEnable(true)
            action = { barType, isSelected ->
                when (barType) {
                    BarButtonItem.Type.COMMENT -> {
//                        showToast("??????")
                        scrollHelper?.handleScrollToComment(this@ReviewDetailActivity, mBinderList)
                    }
                    BarButtonItem.Type.PRAISE -> {//??????
                        contentStatus.isPublished {
                            handlePraiseUp(
                                getSelectedStatusByType(barType),
                                this@ReviewDetailActivity
                            )
                        }
                    }
                    BarButtonItem.Type.DISPRAISE -> {//??????
                        contentStatus.isPublished {
                            handlePraiseDown(getSelectedStatusByType(barType))
                        }
                    }

                    BarButtonItem.Type.PHOTO -> {//??????
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos =
                            { photos ->
                                photos.e()
                                if (photos.isNotEmpty()) {
                                    commentImgLayout.setPhotoInfo(photos[0])
                                }
                            }
                    }
                    BarButtonItem.Type.SEND -> {//????????????
                        saveComment(text)

                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        this@ReviewDetailActivity.showOrHideSoftInput(this.getEditTextView())

                    }
                    BarButtonItem.Type.FAVORITE -> {//??????
                        contentStatus.isPublished {
                            handleCollection(getSelectedStatusByType(barType))
                        }
                    }

                }
            }
            editAction = {//?????????????????????????????????????????????????????????
                afterLogin {
                    contentStatus.isPublished {
                        setEditStyle()
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    private fun handleCollection(isCancel: Boolean) {
        showInteractiveDialog()
        mViewModel?.collectionOrCancel(
            CommConstant.COLLECTION_OBJ_TYPE_FILM_COMMENT,
            contentId,
            isCancel,
            this
        )
    }


    private fun updateCommentCanComment() {
        hotCommentBinderList.filter { it is CommentListBinder }.forEach {
            (it as CommentListBinder).setCommentPmsn(commonBarBean?.commentPmsn)
        }
    }

    /**
     * ????????????????????????
     */
    private fun initCommonLayout(ugcCommonBarBean: UgcCommonBarBean) {
        this.commonBarBean = ugcCommonBarBean
        mViewModel?.setTitleBar(ugcCommonBarBean.createUser)//????????????
        userId = ugcCommonBarBean.createUser.userId
        contentStatus = ugcCommonBarBean.createUser.contentStatus
        editBtn = ugcCommonBarBean.editBtn
        isShortReview = ugcCommonBarBean.isShortReview
        barButton?.style =
            if (isShortReview) PublishCommentView.Style.COMMENT else PublishCommentView.Style.LONG_COMMENT
        movieScore = when {
            TextUtils.isEmpty(ugcCommonBarBean.createUser.score) -> {
                0f
            }
            ugcCommonBarBean.createUser.score.isDigitsOnly() -> {
                ugcCommonBarBean.createUser.score.toFloat()
            }
            else -> {
                0f
            }
        }
        resetInput(CommentHelper.UpdateBarState.INIT)
    }


    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleView?.apply {
            setTitleBackground(com.kotlin.android.ugc.detail.component.R.color.color_ffffff)
            setTitleColor(false)
            setListener(moreClick = {
                contentStatus.contentCanShare(R.string.film_review_is_checking) {
                    showShareDialog()
                }
            }, back = { onBackPressed() },
                attentionClick = { view, userId ->
                    mViewModel?.follow(userId)
                }
            )
        }

    }

    private fun getShareInfoData(sharePlatform: SharePlatform) {
        mViewModel?.getShareExtendInfo(
            if (isShortReview) CommConstant.SHARE_TYPE_SHORT_REVIEW else CommConstant.SHARE_TYPE_LONG_REVIEW,
            contentId,
            extend = sharePlatform
        )
    }

    override fun initData() {
        mBinding?.setVariable(BR.viewModel, mViewModel)
        mViewModel?.loadReviewDetail(contentId, type, recId)


    }

    private fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, contentId, type, isNewComment, isMore)
    }

    /**
     * ??????????????????
     */
    private fun showDeleteDialog() {
        BaseDialog.Builder(this).setContent(R.string.file_review_delete_content)
            .setPositiveButton(com.kotlin.android.ugc.detail.component.R.string.ok) { dialog, id ->
                mViewModel?.deleteContent(CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT, contentId)
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dismissShareDialog()
                dialog?.dismiss()
            }.create().show()
    }

    override fun startObserve() {
        titleObserve()

//        ????????????
        detailObserve()
//         ??????????????????
        commentObserve()
//         ????????????????????????????????????
        wannaAndShareObserve()
        loginEvent()//??????????????????
        //        ??????????????????
        deleteCommentEventObserve()

    }

    //    ???????????????????????????
    private fun deleteCommentEventObserve() {
        LiveEventBus.get(
            DELETE_COMMENT,
            com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java
        )
            .observe(this, Observer {
                deleteComment(it?.commentId ?: 0L)
            })
    }

    private fun deleteComment(commentId: Long) {
        CommentHelper.deleteComment(commentId, mBinderList, hotCommentBinderList, mAdapter)
        updateCommentTitle(true)
        resetInput(CommentHelper.UpdateBarState.DELETE)
    }

    private fun wannaAndShareObserve() {
        //        ??????
        mViewModel?.followState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (isSuccess()) {
                        showToast(com.kotlin.android.ugc.detail.component.R.string.ugc_follow_success)
                        titleView?.updateFollow(true)
                    } else {
                        showToast(bizMsg.orEmpty())
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })


        mViewModel?.shareExtendUIState?.observe(this, ShareObserver(this))
        //        ?????????????????????
        mViewModel?.wantToSeeState?.observe(this, WantToSeeObserve(this))
    }

    private fun showShareDialog() {
        if (isLogin() && UserManager.instance.userId == userId) {//?????????????????????????????????????????????????????????????????????
            if (isShortReview.not() && editBtn != null) {
                showShareDialog(
                    null,
                    ShareFragment.LaunchMode.ADVANCED,
                    SharePlatform.COPY_LINK,
                    SharePlatform.POSTER,
                    SharePlatform.EDIT,
                    SharePlatform.DELETE,
                    event = shareAction
                )

            } else {
                showShareDialog(
                    null,
                    ShareFragment.LaunchMode.ADVANCED,
                    SharePlatform.COPY_LINK,
                    SharePlatform.POSTER,
                    SharePlatform.DELETE,
                    event = shareAction
                )

            }
        } else {
            showShareDialog(
                null,
                ShareFragment.LaunchMode.ADVANCED,
                SharePlatform.COPY_LINK,
                SharePlatform.POSTER,
                event = shareAction
            )
        }
    }

    private fun commentObserve() {
//        ????????????
        mViewModel?.hotCommentListState?.observe(this, Observer {
            smartRefreshLayout?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    if (hotCommentBinderList.isEmpty()) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mergerData()
                    }
//                    firstInAndScrollToComment()
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    if (loadMore) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mBinderList.addAll(hotCommentBinderList)
                        notifyAdapterData()
                    }
                }
            }
        })

//        ????????????
        mViewModel?.newCommentListState?.observe(this, Observer {
            smartRefreshLayout?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    hotCommentBinderList.addAll(this.commentBinderList)
                    updateCommentCanComment()
                    mBinderList.addAll(hotCommentBinderList)
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    "???????????????????????????$mBinderList".d()
                    notifyAdapterData()
                    "??????????????????????????????$mBinderList".d()

                }
            }

        })


        //        ????????????
        mViewModel?.saveCommentState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                isCommenting = showLoading
                success?.apply {
                    showToast(R.string.comment_component_publish_success)

                    sendMessage(barButton?.text.orEmpty(), this)
                }

                netError?.apply {
                    showToast(this)
                }
                error?.apply {
                    showToast(this)
                }
            }
        })
        //        ??????????????????
        mViewModel?.deleteCommentState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    dismissProgressDialog()
                    if (extend is MultiTypeBinder<*>) {
                        (extend as? MultiTypeBinder<*>)?.notifyAdapterSelfRemoved()
                        mBinderList.remove(extend)
                        hotCommentBinderList.remove(extend)
                        if (hotCommentBinderList.size == 0) {
                            val emptyBinder = CommentListEmptyBinder()
                            hotCommentBinderList.add(emptyBinder)
                            mBinderList.add(emptyBinder)
                            notifyAdapterData()
                        }

                        updateCommentTitle(true)
                        resetInput(CommentHelper.UpdateBarState.DELETE)
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
        })
    }

    private fun detailObserve() {
        mViewModel?.reviewDetailState?.observe(this, Observer {
            it?.apply {
                if (showLoading) {
                    showProgressDialog(behavior = ProgressDialogFragment.Behavior.MULTIPART)
                }
                success?.apply {
                    movie = movieViewBean
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
//                    ???????????????????????????
                    val titleBinder = UgcTitleBinder(UgcTitleViewBean(title))
                    detailBinderList.add(titleBinder)
                    if (TextUtils.isEmpty(spoiler).not()) {
                        val ugcMovieSpoilerBinder = UgcMovieSpoilerBinder(spoiler)
                        detailBinderList.add(ugcMovieSpoilerBinder)
                    }
                    movieViewBean?.run {
                        val movieBinder = MovieBinder(this)
                        detailBinderList.add(movieBinder)
                        reviewMovieId = movieId
                    }

                    ugcWebViewBean?.run {
                        val ugcWebViewBinder = UgcWebViewBinder(
                            this,
                            reviewRV,
                            loadFinishListener = loadFinishListener
                        )
                        detailBinderList.add(ugcWebViewBinder)

                    }
                    if (TextUtils.isEmpty(copyRight).not()) {
                        detailBinderList.add(UgcCopyRightBinder(copyRight))
                    }
                    commonBar?.run {
                        initCommonLayout(this)
                    }
                    detailBinderList.add(
                        CommentListTitleBinder(
                            CommentTitleViewBean(
                                commonBar?.commentSupport?.commentCount
                                    ?: 0L
                            )
                        )
                    )
                    mergerData()
//                    firstInAndScrollToComment()
                    loadCommentData(false)
                    mViewModel?.submitContentTrace(type,contentId.toString(),userId,isShortReview)
                }
                error?.apply {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }
                netError?.apply {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }

                if (isEmpty) {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
            }

        })

        //????????????
        mViewModel?.deleteContent?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        showToast(R.string.delete_success)
                        finish()
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })
    }

    /**
     * ??????????????????????????????????????????
     */
    private fun firstInAndScrollToComment() {
        needScrollToComment = scrollHelper?.firstInAndScrollToComment(
            this,
            hotCommentBinderList,
            mBinderList,
            needScrollToComment
        )
            ?: false

    }

    private fun titleObserve() {
        mViewModel?.titleBar?.observe(this, Observer {
            it?.run {
                titleView?.setData(this)
            }
        })
    }

    private fun loginEvent() {
        LiveEventBus.get(
            LOGIN_STATE,
            com.kotlin.android.app.router.liveevent.event.LoginState::class.java
        ).observe(this, Observer {
            if (it?.isLogin == true) {//????????????
                hotCommentBinderList.clear()
                detailBinderList.clear()
                isNewComment = false

                mAdapter.notifyAdapterRemoved(mBinderList) {
                    mBinderList.clear()
                    initData()
                }
            }
        })

    }

    /**
     * ??????????????????
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        CommentHelper.updateCommentTitleList(mBinderList, isDelete)
    }

    private fun handlePraisAndCollectionObser() {
//        ?????????????????????
        mViewModel?.praiseUpState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_PRAISE_UP,
                barButton,
                BarButtonItem.Type.PRAISE
            )
        )

//        ?????????????????????
        mViewModel?.praiseDownState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_PRAISE_DOWN,
                barButton,
                BarButtonItem.Type.DISPRAISE
            )
        )

//        ?????????????????????
        mViewModel?.collectionState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_COLLECTIN,
                barButton,
                BarButtonItem.Type.FAVORITE
            )
        )


    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
        val show = state == MultiStateView.VIEW_STATE_CONTENT
        barButton?.visible(show)
        mBinding?.titleView?.setMoreVisible(show)
    }


    private fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            com.kotlin.android.comment.component.R.id.likeTv -> {//????????????
                val isCancel = (binder as CommentListBinder).bean.isLike()
                handlePraiseUp(isCancel, binder)
            }
            com.kotlin.android.comment.component.R.id.deleteTv -> {//????????????
                deleteComment(binder)
            }
            R.id.movieBtnFl -> {//????????????
                handleWann(binder)//??????????????????
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
        mAdapter.notifyAdapterRemoved(mBinderList.filter { it is CommentListBinder || it is CommentListEmptyBinder }
            .toMutableList()) {
            mBinderList.removeIf { it is CommentListBinder || it is CommentListEmptyBinder }
            hotCommentBinderList.clear()
            "?????????????????????$mBinderList".d()
//            mAdapter.notifyAdapterChanged(mBinderList)
            loadCommentData(false)
        }
    }

    /**
     * ??????????????????
     */
    private fun handleWann(binder: MultiTypeBinder<*>) {
        if (binder is MovieBinder) {
            if (binder.movieBean.isWanna()) {//???????????????????????????
                mViewModel?.wantToSee(
                    binder.movieBean.movieId,
                    DetailBaseViewModel.ACTION_CANCEL,
                    binder
                )
            } else {//??????????????????????????????
                mViewModel?.wantToSee(
                    binder.movieBean.movieId,
                    DetailBaseViewModel.ACTION_POSITIVE,
                    binder
                )
            }
        }
    }

    /**
     * ????????????
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(type, (binder as CommentListBinder).bean.commentId, binder)
    }

    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    private fun mergerData() {
        synchronized(this) {
            val isNotContainsDetailBinder = isContainsDetailBinder().not()
            if (isNotContainsDetailBinder) {
                mBinderList.addAll(0, detailBinderList)
            }
            mBinderList.removeAll { it is CommentListBinder || it is CommentListEmptyBinder }

            mBinderList.addAll(hotCommentBinderList)
            notifyAdapterData()

        }
    }

    private fun notifyAdapterData() {
        mAdapter.notifyAdapterDataSetChanged(mBinderList, false)
    }

    private fun isContainsDetailBinder(): Boolean {
        synchronized(this) {
            val titleIndex = mBinderList.indexOfFirst { it is UgcTitleBinder }
            val webIndex = mBinderList.indexOfFirst { it is UgcWebViewBinder }
            val movieIndex = mBinderList.indexOfFirst { it is MovieBinder }
            return titleIndex >= 0 || webIndex >= 0 || movieIndex >= 0
        }
    }


    /**
     * ?????????????????????????????????
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        if (isLogin()) {
            showProgressDialog()
        }
        if (extend is CommentListBinder) {
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(
                CommConstant.getPraiseUpType(
                    type,
                    CommConstant.COMMENT_PRAISE_ACTION_COMMENT
                ), commentId, isCancel, extend
            )
        } else {
            mViewModel?.praiseUpOrCancel(type, contentId, isCancel, extend)
        }
    }

    /**
     * ?????????????????????????????????
     */
    private fun handlePraiseDown(isCancel: Boolean) {
        if (isLogin()) {
            showProgressDialog()
        }
        mViewModel?.praiseDownOrCancel(type, contentId, isCancel, this)
    }

    private fun saveComment(text: String) {
        if (TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(commentImgLayout?.getImagePath())) {
            showToast(com.kotlin.android.comment.component.R.string.comment_detail_cannt_send_comment)
            return
        }
        if (isCommenting) {
            return
        }
        mViewModel?.saveComment(
            type,
            contentId,
            path = commentImgLayout?.getImagePath().orEmpty(),
            content = text
        )
    }

    private fun sendMessage(text: String, commentId: Long) {
        val index = mBinderList.indexOfFirst { it is CommentListBinder }
        val commentListBinder = CommentListBinder(
            this, CommentViewBean(
                commentId = commentId,
                userName = UserManager.instance.nickname,
                userId = UserManager.instance.userId,
                userPic = UserManager.instance.userAvatar.orEmpty(),
                commentContent = text,
                publishDate = formatPublishTime(System.currentTimeMillis()),
                type = type,
                objId = contentId,
                commentPic = mViewModel?.getUpLoadImageUrl(
                    commentImgLayout?.getImagePath().orEmpty()
                ).orEmpty()
            )
        )
        commentImgLayout?.gone()
        commentImgLayout?.clearImagePath()
        val filter = mBinderList.filter { it is CommentListEmptyBinder }.toMutableList()
        mAdapter.notifyAdapterRemoved(filter) {
            mBinderList.removeAll(filter)

            if (index < 0) {
                mBinderList.add(commentListBinder)
            } else {
                mBinderList.add(index, commentListBinder)
            }
            CommentHelper.addCommentBinder(hotCommentBinderList, commentListBinder)
            updateCommentTitle(false)
            mAdapter.notifyAdapterInsert(index, commentListBinder)
            mBinderList.filter { it is CommentListTitleBinder }.forEach {
                it.notifyAdapterSelfChanged()
            }
//            reviewRV?.scrollToPosition(if (index < 0) 0 else index)
            resetInput(CommentHelper.UpdateBarState.ADD)
        }

    }

    /**
     * ???????????????
     *  INIT,//?????????
     *  ADD,//??????item
     *  DELETE//??????item
     */
    private fun resetInput(updateBarState: CommentHelper.UpdateBarState) {
        CommentHelper.resetInput(commonBarBean, barButton, updateBarState, isShortReview.not())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (barButton?.style == PublishCommentView.Style.EDIT_WITHOUT_MOVIE) {
            resetInput(CommentHelper.UpdateBarState.INIT)
            return
        }
        super.onBackPressed()

    }
}