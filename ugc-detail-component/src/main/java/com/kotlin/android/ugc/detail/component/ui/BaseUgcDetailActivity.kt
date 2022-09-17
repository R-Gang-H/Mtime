package com.kotlin.android.ugc.detail.component.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_JOURNAL
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.getShareTypeByContentType
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.comment.component.DELETE_COMMENT
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.helper.CommentHelper
import com.kotlin.android.comment.component.helper.DetailScrollHelper
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.CommentImageLayout
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.router.ext.isSelf
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.binder.MovieBinder
import com.kotlin.android.ugc.detail.component.contentCanEdit
import com.kotlin.android.ugc.detail.component.isPublished
import com.kotlin.android.ugc.detail.component.observe.WantToSeeObserve
import com.kotlin.android.ugc.detail.component.ui.link.dismissUgcLinkDialog
import com.kotlin.android.ugc.detail.component.ui.widget.UgcTitleView
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * create by lushan on 2022/3/17
 * des:
 **/
abstract class BaseUgcDetailActivity<VM : DetailBaseViewModel, VB : ViewBinding> :
    BaseVMActivity<VM, VB>() {
    protected var authUserId: Long = -1L
    protected var passType: Long = 0L
    protected var contentId: Long = 0L//内容id
    protected var recId: Long = 0L//记录id
    protected var editBtn: CommunityContent.BtnShow? = null//编辑传递记录id
    protected var contentStatus: Long = -1L//创作者内容状态
    protected var needScrollToComment = false//第一次进入加载完数据是否需要滑动到评论处
    protected var isNewComment = false//是否选中的是最新评论
    protected var isCommenting: Boolean = false//是否在发表评论中
    protected var scrollHelper: DetailScrollHelper? = null

    @Volatile
    protected var mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    protected val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//最热评论
    protected val recommendBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    protected val detailBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    protected var commonBarBean: UgcCommonBarBean? = null//用户执行reset后恢复底部评论数据
    protected var mAdapter: MultiTypeAdapter? = null
    override fun initVariable() {
        super.initVariable()
        passType = intent?.getLongExtra(UGC_DETAIL_TYPE, CONTENT_TYPE_JOURNAL)
            ?: CONTENT_TYPE_JOURNAL
        contentId = intent?.getLongExtra(UGC_DETAIL_CONTENT_ID, 0L) ?: 0L
        needScrollToComment = intent?.getBooleanExtra(UGC_DETAIL_NEED_TO_COMMENT, false) ?: false
        recId = intent?.getLongExtra(UGC_DETAIL_REC_ID, 0L) ?: 0L
    }

    protected var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.WEI_BO,
            SharePlatform.QQ,
            SharePlatform.COPY_LINK -> {
                getShareInfo(platform)
            }
            SharePlatform.DELETE -> {//删除
                showDeleteDialog()
            }
            SharePlatform.EDIT -> {//编辑页面
                gotoEdit()
                dismissShareDialog()
            }
        }
    }

    override fun initView() {
        initSmartLayout()
        initBarButton()
        handlePraiseObserve()
        initStateView()
        scrollHelper = DetailScrollHelper(getRecyclerView())
        getRecyclerView()?.apply {
            mAdapter = createMultiTypeAdapter(
                this,
                LinearLayoutManager(this@BaseUgcDetailActivity)
            ).apply {
                setOnClickListener(::handleListAction)
            }
        }
    }

    private fun initStateView() {
        getMultiStateView()?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_ERROR || viewState == MultiStateView.VIEW_STATE_NO_NET) {
                    initData()
                }
            }
        })
    }

    private fun initSmartLayout() {
        getSmartLayout()?.apply {
            setEnableRefresh(false)
            setEnableLoadMore(true)
            setOnLoadMoreListener {
                loadCommentData(true)
            }
        }
    }

    private fun initBarButton() {
        getBarButton()?.apply {
            isWithoutMovie = true
            style = PublishCommentView.Style.COMMENT
            inputEnable(true)
            action = { barType, isSelected ->
                when (barType) {
                    BarButtonItem.Type.COMMENT -> {
                        scrollHelper?.handleScrollToComment(this@BaseUgcDetailActivity, mBinderList)
                    }
                    BarButtonItem.Type.PRAISE -> {//点赞
                        contentStatus.isPublished {
                            handlePraiseUp(
                                getSelectedStatusByType(barType),
                                this@BaseUgcDetailActivity
                            )
                        }
                    }
                    BarButtonItem.Type.DISPRAISE -> {//点踩
                        contentStatus.isPublished {
                            handlePraiseDown(
                                getSelectedStatusByType(barType),
                                this@BaseUgcDetailActivity
                            )
                        }
                    }
                    BarButtonItem.Type.FAVORITE -> {//收藏
                        contentStatus.isPublished {
                            handleCollection(getSelectedStatusByType(barType))
                        }
                    }
                    BarButtonItem.Type.SEND -> {//发送评论
                        contentStatus.isPublished {
                            saveComment(text)
                        }
                    }
                    BarButtonItem.Type.PHOTO -> {//图片
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos =
                            { photos ->
                                photos.e()
                                if (photos.isNotEmpty()) {
                                    getImageLayout()?.setPhotoInfo(photos[0])
                                }
                            }
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        this@BaseUgcDetailActivity.showOrHideSoftInput(this.getEditTextView())

                    }

                }
            }
            editAction = {//点击文本如果没有登录直接跳转到登录页面
                afterLogin {
                    contentStatus.isPublished {
                        setEditStyle()
                    }
                }
            }

        }
    }

    /**
     * 处理收藏、取消收藏操作
     */
    private fun handleCollection(isCancel: Boolean) {
        showInteractiveDialog()
        mViewModel?.collectionOrCancel(getCollectionType(), contentId, isCancel, this)
    }

    /**
     * 三种状态，
     *  INIT,//初始化
     *  ADD,//增加item
     *  DELETE//删除item
     */
    protected fun resetInput(updateBarState: CommentHelper.UpdateBarState) {
        CommentHelper.resetInput(commonBarBean, getBarButton(), updateBarState)
    }

    /**
     * 需要传递控件进去，需要放在 startObserver后
     */
    private fun handlePraiseObserve() {
        //        点赞、取消点赞
        mViewModel?.praiseUpState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_PRAISE_UP,
                getBarButton(),
                BarButtonItem.Type.PRAISE
            )
        )

//        点踩、取消点踩
        mViewModel?.praiseDownState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_PRAISE_DOWN,
                getBarButton(),
                BarButtonItem.Type.DISPRAISE
            )
        )

//收藏、取消收藏
        mViewModel?.collectionState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_COLLECTIN,
                getBarButton(),
                BarButtonItem.Type.FAVORITE
            )
        )

    }

    protected fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, contentId, getPraiseType(), isNewComment, isMore)
    }


    private fun getShareInfo(sharePlatform: SharePlatform) {
        mViewModel?.getShareExtendInfo(
            if (isVideo() || isAudio()) getShareType() else
                getShareTypeByContentType(passType),
            contentId,
            extend = sharePlatform
        )
    }

    abstract fun getShareType(): Long
    abstract fun getPraiseType(): Long
    abstract fun getCollectionType(): Long

    /**
     * 处理点赞、取消点赞操作
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        if (extend is CommentListBinder) {//列表中的点赞
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(
                CommConstant.getPraiseUpType(
                    getPraiseType(),
                    CommConstant.COMMENT_PRAISE_ACTION_COMMENT
                ), commentId, isCancel, extend
            )
        } else {//评论组件中点赞
            mViewModel?.praiseUpOrCancel(getPraiseType(), contentId, isCancel, extend)
        }
    }

    /**
     * 处理点赞，取消点赞操作，只有评论组件中有，列表中无点踩
     */
    private fun handlePraiseDown(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        mViewModel?.praiseDownOrCancel(getPraiseType(), contentId, isCancel, extend)
    }

    private fun saveComment(text: String) {
        if (TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(getImageLayout()?.getImagePath())) {
            showToast(R.string.comment_detail_cannt_send_comment)
            return
        }
        if (isCommenting) {
            return
        }
        mViewModel?.saveComment(
            getPraiseType(),
            contentId,
            path = getImageLayout()?.getImagePath().orEmpty(),
            content = text
        )
    }

    open fun isVideo(): Boolean {
        return false
    }

    open fun isAudio(): Boolean {
        return false
    }

    open fun gotoEdit() {

    }

    override fun startObserve() {
        wannaAndShareObserve()
        commentObserve()
        loginEvent()
        deleteCommentEventObserve()
        deleteContentObserve()
    }

    private fun deleteContentObserve() {
        //删除内容
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

    //    评论详情中删除评论
    private fun deleteCommentEventObserve() {
        LiveEventBus.get(
            DELETE_COMMENT,
            com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java
        ).observe(this, Observer {
            deleteComment(it?.commentId.orZero())
        })
    }

    private fun deleteComment(commentId: Long) {
        mAdapter?.apply {
            CommentHelper.deleteComment(commentId, mBinderList, hotCommentBinderList, this)
        }
        updateCommentTitle(true)
        resetInput(CommentHelper.UpdateBarState.DELETE)
    }

    private fun loginEvent() {
        LiveEventBus
            .get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java)
            .observe(this, Observer {
                it.d()
                if (it?.isLogin == true) {//登录成功
//                        清空评论数据
                    isNewComment = false
                    hotCommentBinderList.clear()
                    mBinderList.clear()
                    initData()
                    dismissUgcLinkDialog()
                }
            })
    }

    private fun commentObserve() {
        //热门评论，默认请求热门评论
        mViewModel?.hotCommentListState?.observe(this, Observer {
            getSmartLayout()?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    if (hotCommentBinderList.isEmpty()) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mergerBinder()
                    }
                    getSmartLayout()?.setNoMoreData(noMoreData)
                    if (loadMore) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mBinderList.addAll(hotCommentBinderList)
                        notifyAdapterData()
                    }
                    firstInAndScrollToComment()
                }
            }
        })
        //最新评论
        mViewModel?.newCommentListState?.observe(this, Observer {
            getSmartLayout()?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    hotCommentBinderList.addAll(this.commentBinderList)
                    updateCommentCanComment()
                    mBinderList.addAll(hotCommentBinderList)
                    getSmartLayout()?.setNoMoreData(noMoreData)
                    "切换最新评论列表：$mBinderList".d()
                    notifyAdapterData()
                    "切换最新评论列表后：$mBinderList".d()
                }
            }

        })

        //        发表评论
        mViewModel?.saveCommentState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                isCommenting = showLoading
                success?.apply {
                    showToast(R.string.comment_component_publish_success)
                    sendMessage(getBarButton()?.text.orEmpty(), this)
                }

                netError?.showToast()
                error?.showToast()
            }
        })
        //        删除某条评论
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
                            mAdapter?.notifyAdapterAdded(hotCommentBinderList)
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

    protected fun notifyAdapterData() {
        mAdapter?.notifyAdapterDataSetChanged(mBinderList, false)
    }

    private fun sendMessage(text: String, commentId: Long) {
//        需要先获取commentId
        val index = mBinderList.indexOfFirst { it is CommentListBinder }
        val commentListBinder = CommentListBinder(
            this, CommentViewBean(
                commentId = commentId,
                userName = UserManager.instance.nickname,
                userId = UserManager.instance.userId,
                userPic = UserManager.instance.userAvatar.orEmpty(),
                commentContent = text,
                publishDate = formatPublishTime(System.currentTimeMillis()),
                type = getPraiseType(),
                objId = contentId,
                commentPic = mViewModel?.getUpLoadImageUrl(
                    getImageLayout()?.getImagePath().orEmpty()
                ).orEmpty()
            )
        )
        getImageLayout()?.gone()
        getImageLayout()?.clearImagePath()

        val filter = mBinderList.filter { it is CommentListEmptyBinder }.toMutableList()
        mAdapter?.notifyAdapterRemoved(filter) {
            mBinderList.removeAll(filter)
            if (index < 0) {
                mBinderList.add(commentListBinder)
            } else {
                mBinderList.add(index, commentListBinder)
            }
            CommentHelper.addCommentBinder(hotCommentBinderList, commentListBinder)
            updateCommentTitle(false)
            mAdapter?.notifyAdapterInsert(index, commentListBinder)
            mBinderList.filter { it is CommentListTitleBinder }.forEach {
                it.notifyAdapterSelfChanged()
            }
            resetInput(CommentHelper.UpdateBarState.ADD)
        }

    }

    /**
     * 更新标题数量
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        CommentHelper.updateCommentTitleList(mBinderList, isDelete)
    }

    /**
     * 第一次进入是否要滑动到评论处
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

    private fun updateCommentCanComment() {
        hotCommentBinderList.filter { it is CommentListBinder }.forEach {
            (it as CommentListBinder).setCommentPmsn(commonBarBean?.commentPmsn)
        }
    }

    private fun wannaAndShareObserve() {
        //        分享
        mViewModel?.shareExtendUIState?.observe(this, ShareObserver(this))
        //        关注
        mViewModel?.followState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        showToast(R.string.ugc_follow_success)
                        updateFollow(true)
                    } else {
                        bizMsg?.showToast()
                    }
                }

                netError?.showToast()
                error?.showToast()
            }
        })
    }

    protected abstract fun updateFollow(isFollowed: Boolean)
    protected abstract fun getSmartLayout(): SmartRefreshLayout?
    protected abstract fun getBarButton(): PublishCommentView?
    protected abstract fun getImageLayout(): CommentImageLayout?
    protected abstract fun getMultiStateView(): MultiStateView?
    protected abstract fun getRecyclerView(): RecyclerView?
    protected abstract fun getTitleView():UgcTitleView?
    open protected fun mergerBinder() {

    }


    open fun showShareDialog() {
        if (isLogin() && isSelf(authUserId)) {//登录了且是自己的文章，就显示删除按钮
            if (isVideo() && editBtn != null) {
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

    open fun setContentState(
        stateView: MultiStateView?,
        @MultiStateView.ViewState state: Int
    ) {
        stateView?.setViewState(state)
        val show = state == MultiStateView.VIEW_STATE_CONTENT
        getBarButton()?.visible(show)
        getTitleView()?.setMoreVisible(show)
    }

    /**
     * 避免出现列表中点赞，出现多个加载框，最后无法取消的情况
     */
    protected fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    /**
     * 删除内容弹框
     */
    private fun showDeleteDialog() {
        BaseDialog.Builder(this).setContent(R.string.delete_content)
            .setPositiveButton(R.string.ok) { dialog, id ->
                mViewModel?.deleteContent(passType, contentId)
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dismissShareDialog()
                dialog?.dismiss()
            }.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    open protected fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            com.kotlin.android.comment.component.R.id.likeTv -> {//点赞按钮
                val isCancel = (binder as CommentListBinder).bean.isLike()
                handlePraiseUp(isCancel, binder)
            }
            com.kotlin.android.comment.component.R.id.deleteTv -> {//删除按钮
                deleteComment(binder)
            }
            R.id.movieBtnFl -> {//想看按钮
                handleWann(binder)//处理想看逻辑
            }

            R.id.hotTv -> {//最热按钮
                isNewComment = false
                handleCommentChange()
            }
            R.id.newTv -> {//最新按钮
                isNewComment = true
                handleCommentChange()
            }
        }
    }

    private fun handleCommentChange() {
        mAdapter?.notifyAdapterRemoved(mBinderList.filter { it is CommentListBinder || it is CommentListEmptyBinder }
            .toMutableList()) {
            mBinderList.removeIf { it is CommentListBinder || it is CommentListEmptyBinder }
            hotCommentBinderList.clear()
            "切换评论列表：$mBinderList".d()
//            mAdapter.notifyAdapterChanged(mBinderList)
            loadCommentData(false)
        }
    }

    /**
     * 删除评论
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(
            getPraiseType(),
            (binder as CommentListBinder).bean.commentId,
            binder
        )
    }

    /**
     * 处理想看逻辑
     */
    private fun handleWann(binder: MultiTypeBinder<*>) {
        if (binder is MovieBinder) {
            if (binder.movieBean.isWanna()) {//想看，此时取消想看
                mViewModel?.wantToSee(
                    binder.movieBean.movieId,
                    DetailBaseViewModel.ACTION_CANCEL, binder
                )
            } else {//取消想看，此时应想看
                mViewModel?.wantToSee(
                    binder.movieBean.movieId,
                    DetailBaseViewModel.ACTION_POSITIVE, binder
                )
            }
        }
    }
}