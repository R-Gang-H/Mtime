package com.kotlin.android.article.component.item.ui.detail

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler
import com.kk.taurus.playerbase.event.OnErrorEventListener
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_OBJ_TYPE_ARTICLE
import com.kotlin.android.app.data.entity.common.WantToSeeResult
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.article.component.BR
import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.databinding.ActivityArticleDetailBinding
import com.kotlin.android.comment.component.DELETE_COMMENT
import com.kotlin.android.comment.component.DetailBaseViewModel.Companion.ACTION_CANCEL
import com.kotlin.android.comment.component.DetailBaseViewModel.Companion.ACTION_POSITIVE
import com.kotlin.android.comment.component.DetailBaseViewModel.Companion.getLongValue
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.helper.CommentHelper
import com.kotlin.android.comment.component.helper.DetailScrollHelper
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.js.sdk.entity.JsEntity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.isShow
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.ReceiverGroupManager
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.isSelf
import com.kotlin.android.router.liveevent.COLLECTION_OR_CANCEL
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.ugc.detail.component.bean.UgcTitleViewBean
import com.kotlin.android.ugc.detail.component.binder.*
import com.kotlin.android.ugc.detail.component.contentCanShare
import com.kotlin.android.ugc.detail.component.isPublished
import com.kotlin.android.ugc.detail.component.listener.IAlbumScrollListener
import com.kotlin.android.ugc.detail.component.observe.WantToSeeObserve
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_NEED_TO_COMMENT
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_REC_ID
import com.kotlin.android.ugc.detail.component.ui.link.dismissUgcLinkDialog
import com.kotlin.android.ugc.detail.component.ui.link.showUgcLinkDialog
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_article_detail.*

/**
 * create by lushan on 2020/8/11
 * description: ????????????
 */
@Route(path = RouterActivityPath.Article.PAGE_ARTICLE_DETAIL_ACTIVITY)
class ArticleDetailActivity :
    BaseVMActivity<ArticleDetailViewModel, ActivityArticleDetailBinding>(),
    OnPlayerEventListener, OnErrorEventListener, OnReceiverEventListener {

    companion object {
        const val ARTICLE_CONTENT_ID = "article_content_id"
        const val ARTICLE_TYPE = "article_type"
    }

    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(articleRv, LinearLayoutManager(this)).apply {
            setOnClickListener(::handleListAction)
        }
    }

    private var type: Long = 0L
    private var contentId: Long = 0L
    private var recId: Long = 0L
    private var contentStatus: Long = -1L
    private var editBtn: CommunityContent.BtnShow? = null//??????????????????id
    private var orientationHelper: OrientationHelper? = null
    private var isNewComment = false//??????????????????????????????

    @Volatile
    private var mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//????????????
    private val recommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    private val detailBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    private var authUserId: Long = -1L//????????????id

    //    ??????????????????source
    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)
    private var scrollHelper: DetailScrollHelper? = null//????????????????????????
    private var needScrollToComment = false//????????????????????????????????????????????????????????????
    private var commonBarBean: UgcCommonBarBean? = null//????????????reset???????????????????????????
    private var isCommenting: Boolean = false//????????????????????????
    private var mUgcLinkBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    private var isWebViewLoadFinished = false

    /**
     * ???????????????????????????
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        mDataSource = it
        "????????????????????????-".e()
        mViewModel?.getVideoPlayUrl(videoId, videoSourceType, "http")
    }

    private var videoId: Long = 0L//????????????id
    private var videoSourceType = 0L//???????????? 1-????????? 2 ????????? 3-??????
    private var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.WEI_BO,
            SharePlatform.QQ,
            SharePlatform.COPY_LINK -> {
                getShareInfo(platform)
            }
            SharePlatform.DELETE -> {//??????
                showDeleteDialog()
            }
            SharePlatform.EDIT -> {//??????
                getProvider(IPublishProvider::class.java) {
                    startEditorActivity(type, contentId, editBtn?.recId.orZero())
                }
                dismissShareDialog()
            }
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initVariable() {
        super.initVariable()
        type = intent?.getLongExtra(ARTICLE_TYPE, 0L) ?: 0L
        contentId = intent?.getLongExtra(ARTICLE_CONTENT_ID, 0L) ?: 0L
        recId = intent?.getLongExtra(UGC_DETAIL_REC_ID, 0L) ?: 0L
        needScrollToComment = intent?.getBooleanExtra(UGC_DETAIL_NEED_TO_COMMENT, false) ?: false
    }

    /**
     * jssdk????????????
     */
    private val videoListener: ((JsEntity.VideoPlayerEntity.DataBean?) -> Unit)? = {
        it?.apply {
            startVideoPlayer(this)
        }
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
        var movieId = 0L
        var isWanna: Boolean = false
        when (binder) {
            is MovieBinder -> {
                movieId = binder.movieBean.movieId
                isWanna = binder.movieBean.isWanna()

            }
            is UgcLinkMovieBinder -> {
                movieId = binder.bean.movieId
                isWanna = binder.bean.isWanna()
            }
        }
        if (movieId == 0L) return
        if (isWanna) {//??????????????????????????????????????????
            mViewModel?.wantToSee(movieId, ACTION_POSITIVE, binder)
        } else {//????????????????????????????????????????????????
            mViewModel?.wantToSee(movieId, ACTION_CANCEL, binder)
        }
    }

    /**
     * ????????????
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(type, (binder as CommentListBinder).bean.commentId, binder)
    }

    override fun initVM(): ArticleDetailViewModel = viewModels<ArticleDetailViewModel>().value


    override fun initView() {
//        handleArticleStatusBar(isDarkTheme())
        initBarButton()
        initSmartLayout()
        initVideoView()
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_ERROR || viewState == MultiStateView.VIEW_STATE_NO_NET) {
                    initData()
                }
            }
        })
        initRecyclerView()
//????????????view????????????initView??????????????????startObserve???
        handlePraiseObserve()
        albumTitleView?.topStatusMargin()
        scrollHelper = DetailScrollHelper(articleRv)
        closeIv?.onClick {
            videoView?.pause()
            videoView?.gone()
            closeIv?.gone()
            orientationHelper?.disable()
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }


        }
        mBinding?.linkView?.onClick {
            showUgcLinkDialog(mUgcLinkBinderList) {
                //????????????
                handleWann(it)
            }
        }

    }


    //????????????????????????????????????????????????
    private var albumTitleScrollListener: IAlbumScrollListener? = null
    private fun initRecyclerView() {
        albumTitleScrollListener = IAlbumScrollListener(false, albumTitleView)
        albumTitleScrollListener?.run {
            articleRv?.addOnScrollListener(this)
        }
    }


    private fun initVideoView() {
        videoView?.apply {
//            setDataProvider(BaseDataProvider())
            setReceiverGroup(
                ReceiverGroupManager.getBaseReceiverGroup(this@ArticleDetailActivity)
            )
            setOnPlayerEventListener(this@ArticleDetailActivity)
            setOnErrorEventListener(this@ArticleDetailActivity)
            setOnReceiverEventListener(this@ArticleDetailActivity)
            setEventHandler(object : OnVideoViewEventHandler() {
            })

            setDataProvider(videoDataProvider)
            handleVideoMarginTop(false)
        }
//        startVideoView()
    }

    private fun handleVideoMarginTop(isFullScreen: Boolean) {
        val marginLayoutParams = videoView?.layoutParams as? ViewGroup.MarginLayoutParams
        marginLayoutParams?.topMargin = if (isFullScreen) 0 else statusBarHeight + 44.dp
        videoView?.layoutParams = marginLayoutParams

        val closeMarginLayoutParams = closeIv?.layoutParams as? ViewGroup.MarginLayoutParams
        closeMarginLayoutParams?.topMargin = if (isFullScreen) 20.dp else statusBarHeight + 64.dp
        closeIv?.layoutParams = closeMarginLayoutParams
    }


    /**
     * js??????????????????
     */
    private fun startVideoPlayer(bean: JsEntity.VideoPlayerEntity.DataBean) {
        videoId = getLongValue(bean.videoID ?: "0")
        videoSourceType = getLongValue(bean.videoType ?: "0")
        mDataSource = MTimeVideoData("", videoId, videoSourceType, 0L)
        videoDataProvider?.updateSourceData(videoId, videoSourceType)
        orientationHelper = OrientationHelper(this)
        orientationHelper?.sensorEnable(false)
        runOnUiThread {
            videoView?.apply {
                visible()
                closeIv?.visible()
                setDataProvider(videoDataProvider)
                setDataSource(mDataSource)
                start()
            }
        }


    }

    private fun initSmartLayout() {
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
                        scrollHelper?.handleScrollToComment(this@ArticleDetailActivity, mBinderList)
                    }
                    BarButtonItem.Type.PRAISE -> {//??????
                        contentStatus.isPublished {
                            handlePraiseUp(
                                getSelectedStatusByType(barType),
                                this@ArticleDetailActivity
                            )
                        }
                    }
                    BarButtonItem.Type.DISPRAISE -> {//??????
                        contentStatus.isPublished {
                            handlePraiseDown(
                                getSelectedStatusByType(barType),
                                this@ArticleDetailActivity
                            )
                        }
                    }
                    BarButtonItem.Type.FAVORITE -> {//??????
                        contentStatus.isPublished {
                            handleCollection(getSelectedStatusByType(barType))
                        }
                    }
                    BarButtonItem.Type.SEND -> {//????????????
                        contentStatus.isPublished {
                            saveComment(text)
                        }
                    }
                    BarButtonItem.Type.PHOTO -> {//??????
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos =
                            { photos ->
                                photos.e()
                                commentImgLayout?.e()
                                if (photos.isNotEmpty()) {
                                    commentImgLayout?.setPhotoInfo(photos[0])
                                }
                            }
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        this@ArticleDetailActivity.showOrHideSoftInput(this.getEditTextView())

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
        mViewModel?.collectionOrCancel(COLLECTION_OBJ_TYPE_ARTICLE, contentId, isCancel, this)
        LiveEventBus.get(COLLECTION_OR_CANCEL).post(
            com.kotlin.android.app.router.liveevent.event.CollectionState(
                CommConstant.COLLECTION_TYPE_ARTICLE
            )
        )
    }

    /**
     * ?????????????????????????????????
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
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
    private fun handlePraiseDown(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        mViewModel?.praiseDownOrCancel(type, contentId, isCancel, extend)
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
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
//        ???????????????commentId
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


//            articleRv?.scrollToPosition(if (index < 0) 0 else index)
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
        CommentHelper.resetInput(commonBarBean, barButton, updateBarState)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        val moreClick: (View) -> Unit = {
            contentStatus.contentCanShare(R.string.article_is_checking) {
                showShareDialog()
            }
        }
        val attentionClick: (View, Long) -> Unit = { view, userId ->
            mViewModel?.follow(userId)
        }
        val backClick: (View) -> Unit = {
            onBackPressed()
        }

        titleView?.apply {
            setTitleBackground(com.kotlin.android.ugc.detail.component.R.color.color_ffffff)
            setTitleColor(false)
            setListener(backClick, moreClick = moreClick, attentionClick = attentionClick)
            topStatusMargin()
        }
        albumTitleView?.apply {
            setTitleBackground(com.kotlin.android.ugc.detail.component.R.color.color_00000000)
            setTitleColor(true)
            setListener(backClick, moreClick = moreClick, attentionClick = attentionClick)
        }
    }

    private fun getShareInfo(sharePlatform: SharePlatform) {
        mViewModel?.getShareExtendInfo(
            CommConstant.SHARE_TYPE_ARTICLE,
            contentId,
            extend = sharePlatform
        )
    }

    /**
     * ????????????????????????
     */
    private fun initCommonLayout(ugcCommonBarBean: UgcCommonBarBean) {
        this.commonBarBean = ugcCommonBarBean
        authUserId = ugcCommonBarBean.createUser.userId
        contentStatus = ugcCommonBarBean.createUser.contentStatus
        editBtn = ugcCommonBarBean.editBtn
        mViewModel?.setTitleBar(ugcCommonBarBean.createUser)//????????????
        resetInput(CommentHelper.UpdateBarState.INIT)
    }


    override fun initData() {
        mUgcLinkBinderList.clear()
        mBinding?.setVariable(BR.viewModel, mViewModel)
        mBinding?.linkView?.gone()
        mViewModel?.loadDetailData(type, contentId, recId)
        mViewModel?.loadRecommendData(contentId, recId)
        loadCommentData(false)
    }

    private fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, contentId, type, isNewComment, isMore)
    }

    override fun startObserve() {
        //????????????
        recommendArticleObserve()

        //        ????????????
        detailObserve()
//        ???????????????????????????
        commentObserve()
//???????????????
        wannShareObserve()
//        ??????????????????
        playerObserve()
//        ??????????????????
        loginEvent()
//        ??????????????????
        deleteCommentEventObserve()
    }

    private fun recommendArticleObserve() {
        mViewModel?.recommendState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    mUgcLinkBinderList.addAll(this)
                    LogUtils.e("??????????????????","?????????????????????this.isNotEmpty()???${this.isNotEmpty()} mBinding?.stateView?.getViewState():${mBinding?.stateView?.getViewState()} ===${mBinding?.stateView?.getViewState() == MultiStateView.VIEW_STATE_CONTENT}")
                    mBinding?.linkView?.visible(
                        (this.isNotEmpty())&& mBinding?.stateView?.getViewState() == MultiStateView.VIEW_STATE_CONTENT
                    )
                }
            }
        })
    }

    /**
     * ??????????????????????????????????????????
     */
    private fun firstInAndScrollToComment() {
        val hasComment =
            mBinderList.filter { it is CommentListBinder || it is CommentListEmptyBinder }
                .isNotEmpty()

        if (hasComment && isWebViewLoadFinished) {
            mBinding?.barButton?.postDelayed({
                needScrollToComment = scrollHelper?.firstInAndScrollToComment(
                    this,
                    hotCommentBinderList,
                    mBinderList,
                    needScrollToComment
                )
                    ?: false
            }, 100L)

        }

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


    private fun wannShareObserve() {
        //        ??????
        mViewModel?.followState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        showToast(com.kotlin.android.ugc.detail.component.R.string.ugc_follow_success)
                        titleView?.updateFollow(true)
                        albumTitleView?.updateFollow(true)
                    } else {
                        bizMsg?.showToast()
                    }
                }

                netError?.showToast()
                error?.showToast()
            }
        })


        //        ??????

        mViewModel?.shareExtendUIState?.observe(this, ShareObserver(this))


        //        ?????????????????????
        mViewModel?.wantToSeeState?.observe(this, object : WantToSeeObserve(this) {
            override fun onChanged(t: BaseUIModel<DetailBaseExtend<Any>>?) {
                super.onChanged(t)
                t?.apply {
                    success?.apply {
                        if (extend is UgcLinkMovieBinder) {//??????????????????
                            if ((result as? WantToSeeResult)?.isSuccess() == true) {
                                val movieBinder = extend as UgcLinkMovieBinder
                                movieBinder.updateWannaStatus()
                                mUgcLinkBinderList.filter { it is UgcLinkMovieBinder && it.bean.movieId == movieBinder.bean.movieId }
                                    .forEach {
                                        (it as UgcLinkMovieBinder).bean.movieStatus =
                                            movieBinder.bean.movieStatus
                                    }
                            }
                        }
                    }
                    error?.showToast()
                    netError?.showToast()

                }
            }
        })
    }

    private fun showShareDialog() {
        if (isLogin() && isSelf(authUserId)) {//??????????????????????????????????????????????????????
            if (editBtn != null) {
                showShareDialog(
                    null,
                    ShareFragment.LaunchMode.ADVANCED,
                    SharePlatform.COPY_LINK,
                    SharePlatform.EDIT,
                    SharePlatform.DELETE,
                    event = shareAction
                )
            } else {
                showShareDialog(
                    null,
                    ShareFragment.LaunchMode.ADVANCED,
                    SharePlatform.COPY_LINK,
                    SharePlatform.DELETE,
                    event = shareAction
                )
            }

        } else {
            showShareDialog(
                null,
                ShareFragment.LaunchMode.ADVANCED,
                SharePlatform.COPY_LINK,
                event = shareAction
            )
        }
    }

    private fun playerObserve() {
        mViewModel?.videoPlayUrlState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    "?????????????????????${this}".e()
                    videoDataProvider?.setVideoPlayUrlList(this)
                }

                netError?.apply {
                    videoDataProvider?.setVideoPlayUrlError()
                }
                error?.apply {
                    videoDataProvider?.setVideoPlayUrlError()
                }
            }
        })
    }

    private fun updateCommentCanComment() {
        hotCommentBinderList.filter { it is CommentListBinder }.forEach {
            (it as CommentListBinder).setCommentPmsn(commonBarBean?.commentPmsn)
        }
    }

    private fun commentObserve() {
        //???????????????????????????????????????
        mViewModel?.hotCommentListState?.observe(this, Observer {
            smartRefreshLayout?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    if (hotCommentBinderList.isEmpty()) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        "??????????????????$hotCommentBinderList".e()
                        mergerBinder(true)
                    }
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    if (loadMore) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mBinderList.addAll(hotCommentBinderList)
                        notifyAdapterData()
                    }
//                    firstInAndScrollToComment()
                }
            }
        })
        //????????????
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

                netError?.showToast()
                error?.showToast()
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
        mViewModel?.detailState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    val articleDetailBinderList = mutableListOf<MultiTypeBinder<*>>()
                    commonBar?.apply {
                        initCommonLayout(this)
                    }
                    if (bannerData.isNotEmpty()) {//banner??????
                        articleDetailBinderList.add(
                            UgcBannerImageBinder(
                                bannerData,
                                titleData?.title.orEmpty()
                            )
                        )
                    }
                    mViewModel?.updateTitleBar(bannerData.isNotEmpty())
                    mViewModel?.poplarClick(this.titleData?.title.orEmpty())
                    mViewModel?.submitContentTrace(type,contentId.toString(),authUserId)
                    //???????????????????????????????????????????????????????????????????????????????????????????????????
                    KeyBoard.assistActivity(this@ArticleDetailActivity, bannerData.isNotEmpty())
                    if (bannerData.isEmpty()) {
                        titleView?.topStatusMargin()
                    }
                    //                    if (bannerData.isEmpty()) {
                    //                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    //                    }
                    albumTitleScrollListener?.isAlbum = bannerData.isNotEmpty()
                    if (titleData != null) {
                        titleData?.apply {
                            articleDetailBinderList.add(UgcTitleBinder(this))
                        }
                    } else {
                        articleDetailBinderList.add(UgcTitleBinder(UgcTitleViewBean()))
                    }

                    webContentData?.apply {
                        articleDetailBinderList.add(
                            UgcWebViewBinder(
                                this,
                                articleRv,
                                videoListener,
                                loadFinishListener = {
                                    isWebViewLoadFinished = true
                                    LogUtils.e("??????????????????--firstInAndScrollToComment ????????????--$needScrollToComment ---$isWebViewLoadFinished")
                                    firstInAndScrollToComment()
                                    LogUtils.e("??????????????????--firstInAndScrollToComment ????????????--$needScrollToComment ---$isWebViewLoadFinished")
                                }
                            )
                        )
                    }
                    movieData?.apply {
                        articleDetailBinderList.add(MovieBinder(this))
                    }
                    if (TextUtils.isEmpty(copyRight).not()) {
                        articleDetailBinderList.add(UgcCopyRightBinder(copyRight))
                    }
                    articleDetailBinderList.add(
                        CommentListTitleBinder(
                            CommentTitleViewBean(
                                commonBar?.commentSupport?.commentCount
                                    ?: 0L
                            )
                        )
                    )

                    detailBinderList.clear()
                    detailBinderList.addAll(articleDetailBinderList)
                    mergerBinder(false)
//                    firstInAndScrollToComment()
                    if (mUgcLinkBinderList.isNotEmpty()) {
                        mUgcLinkBinderList.addAll(0, ugcLinkBinderList)
                    } else {
                        mUgcLinkBinderList.addAll(ugcLinkBinderList)
                    }
                    mBinding?.linkView?.visible(mUgcLinkBinderList.isNotEmpty())
                }

                netError?.run {
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.run {
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }
                if (isEmpty) {
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }

            }
        })

        //????????????
        mViewModel?.deleteContent?.observe(this) {
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
        }
    }

    private fun loginEvent() {
        LiveEventBus
            .get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java)
            .observe(this, Observer {
                it.d()
                if (it?.isLogin == true) {//????????????
//                        ??????????????????
                    isNewComment = false
                    hotCommentBinderList.clear()
                    mBinderList.clear()
                    initData()
                    dismissUgcLinkDialog()
                }
            })
    }

    /**
     * ??????????????????
     */
    private fun showDeleteDialog() {
        BaseDialog.Builder(this).setContent(R.string.delete_content)
            .setPositiveButton(R.string.ok) { dialog, id ->
                mViewModel?.deleteContent(CommConstant.PRAISE_OBJ_TYPE_ARTICLE, contentId)
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dismissShareDialog()
                dialog?.dismiss()
            }.create().show()
    }

    /**
     * ??????????????????
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        CommentHelper.updateCommentTitleList(mBinderList, isDelete)
    }


    /**
     * ??????????????????????????????????????? startObserver???
     */
    private fun handlePraiseObserve() {
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

//?????????????????????
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

    private fun mergerBinder(isFromComment: Boolean) {
        synchronized(this) {
            var isNotContainsDetailBinder = isContainsDetailBinder().not()
            if (isNotContainsDetailBinder) {
                mBinderList.addAll(0, detailBinderList)
            }

//            if (isContainsRecommendBinder().not()) {
//                if (detailBinderList.isEmpty()) {
//                    mBinderList.addAll(0, recommentBinderList)
//                } else {
//                    mBinderList.addAll(detailBinderList.size, recommentBinderList)
//                }
//            }

            mBinderList.removeAll { it is CommentListBinder || it is CommentListEmptyBinder }

            mBinderList.addAll(hotCommentBinderList)
            "???????????????mBinderList:$mBinderList".d()
            "hotCommentBinderList:$hotCommentBinderList".d()
            notifyAdapterData {
                if (isFromComment) {
                    LogUtils.e("??????????????????--firstInAndScrollToComment ????????????--???$needScrollToComment ---$isWebViewLoadFinished")
                    firstInAndScrollToComment()
                    LogUtils.e("??????????????????--firstInAndScrollToComment  ????????????--???$needScrollToComment ---$isWebViewLoadFinished")
                }
            }
        }
    }


    private fun isContainsDetailBinder(): Boolean {
        synchronized(this) {
            val titleIndex = mBinderList.indexOfFirst { it is UgcTitleBinder }
            val webIndex = mBinderList.indexOfFirst { it is UgcWebViewBinder }
            val movieIndex = mBinderList.indexOfFirst { it is MovieBinder }
            return titleIndex >= 0 || webIndex >= 0 || movieIndex >= 0
        }
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
        val isShow = state == MultiStateView.VIEW_STATE_CONTENT
        barButton?.visible(isShow)
        mBinding?.titleView?.setMoreVisible(isShow)
        mBinding?.albumTitleView?.setMoreVisible(isShow)
//        ??????????????????????????????
        if (isShow){
            mBinding?.linkView?.visible(mUgcLinkBinderList.isNotEmpty())
        }else{
            mBinding?.linkView?.gone()

        }
    }


    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }


    private var isFullScreen = false
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSoftInput()
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFullScreen = false
            PlayerHelper.portraitMatchWidth_16_9(this, videoView, null)
//            ?????????????????????
            immersive().statusBarColor(
                albumTitleView?.getTitleBackgroundColor() ?: getColor(R.color.transparent)
            )
            notifyAdapterData()
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//???????????????????????????
            isFullScreen = true
            PlayerHelper.landscapeMatchWidthHeight(this, videoView, null)
            immersive().statusBarColor(getColor(R.color.transparent))
        }
        handleVideoMarginTop(isFullScreen)
    }

    private fun notifyAdapterData(callback: (() -> Unit)? = null) {
        mAdapter.notifyAdapterDataSetChanged(mBinderList, false, callback)
    }


    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        "eventCode:$eventCode".e()
        when (eventCode) {

            DataInter.ReceiverEvent.EVENT_REQUEST_BACK -> onBackPressed()
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE -> {//??????????????????
                orientationHelper?.toggleScreen()
            }
//            DataInter.ReceiverEvent.EVENT_CODE_SHARE -> {//??????
//
//            }

        }
    }

    override fun onPause() {
        videoView?.pause()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
//        orientationHelper?.enable()
    }

    override fun onStop() {
        super.onStop()
//        orientationHelper?.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationHelper?.destroy()
        videoView?.stopPlayback()
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