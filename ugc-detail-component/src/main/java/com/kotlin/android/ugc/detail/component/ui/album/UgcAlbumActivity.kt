package com.kotlin.android.ugc.detail.component.ui.album

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.comment.component.DELETE_COMMENT
import com.kotlin.android.comment.component.PRAISE_TYPE_ALBUM
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.helper.CommentHelper
import com.kotlin.android.comment.component.helper.DetailScrollHelper
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.album.AlbumInfo
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
import com.kotlin.android.user.afterLogin
import com.kotlin.android.router.liveevent.DELETE_ALBUM_IMAGE
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.ugc.detail.component.BR
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumTitleViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleBarBean
import com.kotlin.android.ugc.detail.component.binder.UgcAlbumBinder
import com.kotlin.android.ugc.detail.component.binder.UgcAlbumTitleBinder
import com.kotlin.android.ugc.detail.component.databinding.ActivityUgcAlbumBinding
import com.kotlin.android.ugc.detail.component.event.ALBUM_UPDATE
import com.kotlin.android.ugc.detail.component.event.UpdateAlbumEvent
import com.kotlin.android.ugc.detail.component.ui.album.UpdateAlbumDialogFragment.Companion.TAG_UPDATE_DIALOG_FRAGMENT
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_ugc_album.*
import kotlinx.android.synthetic.main.activity_ugc_album.barButton
import kotlinx.android.synthetic.main.activity_ugc_album.smartRefreshLayout
import kotlinx.android.synthetic.main.activity_ugc_album.stateView
import kotlinx.android.synthetic.main.activity_ugc_album.titleView

/**
 * Ugc 图集详情
 */

@Route(path = RouterActivityPath.UgcDetail.PAGE_UGC_DETAIL_ALBUM_ACTIVITY)
class UgcAlbumActivity : BaseVMActivity<UgcAlbumViewModel, ActivityUgcAlbumBinding>() {
    companion object {
        const val KEY_ALBUM_ID = "key_album_id"
    }

    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(albumRv, LinearLayoutManager(this)).apply {
            setOnClickListener(::handleListAction)
        }
    }

    private var albumId: Long = 0L//相册id 上个页面传递过来的相册id
    private var userId: Long = -1L//相册所有人id
    private var type = CommConstant.PRAISE_OBJ_TYPE_ALBUM
    private val mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//最热评论

    //    标题binder
    private var titleBinder: UgcAlbumTitleBinder = UgcAlbumTitleBinder(UgcAlbumTitleViewBean())

    //    相册内容binder
    private var albumBinder: UgcAlbumBinder = UgcAlbumBinder(UgcAlbumViewBean(), 0L)
    private var isNewComment = false//是否选中的是最新评论
    private var shareUrl: String = ""//分享url
    private var scrollHelper: DetailScrollHelper? = null
    private var isCommenting: Boolean = false//是否在发表评论中
    private var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.WEI_BO,
            SharePlatform.QQ,
            SharePlatform.COPY_LINK -> {
                getShareInfoData(platform)
            }
            SharePlatform.DELETE -> {//删除
                showDeleteDialog()
            }
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initVM(): UgcAlbumViewModel = viewModels<UgcAlbumViewModel>().value

    override fun initVariable() {
        super.initVariable()
        albumId = intent?.getLongExtra(KEY_ALBUM_ID, 0L) ?: 0L
        albumBinder.sAlbumId = albumId
    }

    override fun initView() {
        initBottomBar()
        initSmartRefreshLayout()
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    mViewModel?.loadDetailData(albumId, userId)
                }
            }

        })
        initPraiseAndCollectionState()
        scrollHelper = DetailScrollHelper(albumRv)
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

    private fun initBottomBar() {
        barButton?.apply {
            isWithoutMovie = true
            style = PublishCommentView.Style.COMMENT
            inputEnable(true)

            action = { type, isSelected ->

                when (type) {
                    BarButtonItem.Type.FAVORITE -> {//收藏
//                        isSelectedByType(type, isSelected)
                    }
                    BarButtonItem.Type.PRAISE -> {//支持
                        handlePraiseUp(getSelectedStatusByType(type), this)
                    }
                    BarButtonItem.Type.DISPRAISE -> {//点踩
                        handlePraiseDown(getSelectedStatusByType(type))
                    }
                    BarButtonItem.Type.COMMENT -> {//评论
                        scrollHelper?.handleScrollToComment(this@UgcAlbumActivity, mutableListOf())
                    }
                    BarButtonItem.Type.SEND -> {//发布
                        saveComment(text)
                    }
                    BarButtonItem.Type.PHOTO -> {//图片
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos = { photos ->
                            photos.e()
                            if (photos.isNotEmpty()) {
                                commentImgLayout.setPhotoInfo(photos[0])
                            }
                        }
                    }

                    BarButtonItem.Type.KEYBOARD -> {
                        this@UgcAlbumActivity.showOrHideSoftInput(this.getEditTextView())

                    }
                }

            }
            editAction = {//点击文本如果没有登录直接跳转到登录页面
                afterLogin {
                    setEditStyle()
                }
            }

        }
    }

    /**
     * 加载相册中图片列表
     */
    private fun loadImageList(isMore: Boolean) {
        if (isMore) {
            showProgressDialog()
        }
        mViewModel?.loadAlbumListData(albumId, userId, isMore)
    }

    /**
     * 加载评论数据
     */
    private fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, albumId, type, isNewComment, isMore)
    }

    private fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        "UgcAlbumActivity == view$view == any:$binder".e()
        when (view.id) {
            R.id.albumFl -> {//相册名称
                UpdateAlbumDialogFragment.newInstance(albumId, titleBinder.getAlbumName()).show(supportFragmentManager, TAG_UPDATE_DIALOG_FRAGMENT)
            }
            R.id.addPicLL -> {//上传图片
                showPhotoAlbumFragment(
                        isUploadImageInComponent = true,
                        imageFileType = CommConstant.IMAGE_UPLOAD_USER_UPLOAD,
                        limitedCount = 9L).apply {
                    actionSelectPhotos = {
                        mViewModel?.setUploadImageList(it)
                        // 图片上传成功后
                        if (it.isNotEmpty()) {
                            mViewModel?.uploadImageByAlbumId(albumId, it)
                        }
                    }
                }
            }
            R.id.loadMoreTv -> {//加载更多图片
                loadImageList(true)
            }
            com.kotlin.android.comment.component.R.id.likeTv -> {//点赞按钮
                val isCancel = (binder as CommentListBinder).bean.isLike()
                handlePraiseUp(isCancel, binder)
            }
            com.kotlin.android.comment.component.R.id.deleteTv -> {//删除按钮
                deleteComment(binder)
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
        mAdapter.notifyAdapterRemoved(mBinderList.filter { it is CommentListBinder || it is CommentListEmptyBinder }.toMutableList()) {
            mBinderList.removeIf { it is CommentListBinder || it is CommentListEmptyBinder }
            hotCommentBinderList.clear()
            "切换评论列表：$mBinderList".d()
            notifyAdapterData()
            loadCommentData(false)
        }
    }

    /**
     * 删除评论
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(type, (binder as CommentListBinder).bean.commentId, binder)
    }

    /**
     * 处理点赞、取消点赞操作
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        if (extend is CommentListBinder) {
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(CommConstant.getPraiseUpType(type, CommConstant.COMMENT_PRAISE_ACTION_COMMENT), commentId, isCancel, extend)
        } else {
            mViewModel?.praiseUpOrCancel(type, albumId, isCancel, extend)
        }
    }

    /**
     * 处理点赞，取消点赞操作
     */
    private fun handlePraiseDown(isCancel: Boolean) {
        showInteractiveDialog()
        mViewModel?.praiseDownOrCancel(type, albumId, isCancel, this)
    }

    /**
     * 保存评论
     */
    private fun saveComment(text: String) {
        if (TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(commentImgLayout?.getImagePath())) {
            showToast(com.kotlin.android.comment.component.R.string.comment_detail_cannt_send_comment)
            return
        }
        if (isCommenting) return
        mViewModel?.saveComment(type, albumId, path = commentImgLayout?.getImagePath().orEmpty(), content = text)
    }


    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    /**
     * 删除内容弹框
     */
    private fun showDeleteDialog() {
        BaseDialog.Builder(this).setContent(R.string.delete_content).setPositiveButton(R.string.ok) { dialog, id ->
            mViewModel?.deleteAlbum(albumId)
        }.setNegativeButton(R.string.cancel) { dialog, id ->
            dismissShareDialog()
            dialog?.dismiss()
        }.create().show()
    }

    private fun initPraiseAndCollectionState() {
        //        点赞、取消点赞
        mViewModel?.praiseUpState?.observe(this, CommonObserver(this, CommonObserver.TYPE_PRAISE_UP, barButton, BarButtonItem.Type.PRAISE))

        //        点踩、取消点踩
        mViewModel?.praiseDownState?.observe(this, CommonObserver(this, CommonObserver.TYPE_PRAISE_DOWN, barButton, BarButtonItem.Type.DISPRAISE))

        //收藏、取消收藏
        mViewModel?.collectionState?.observe(this, CommonObserver(this, CommonObserver.TYPE_COLLECTIN, barButton, BarButtonItem.Type.FAVORITE))
    }

    private fun sendMessage(text: String, commentId: Long) {
        val commentListBinder = CommentListBinder(this, CommentViewBean(commentId = commentId,
                userName = UserManager.instance.nickname,
                userId = UserManager.instance.userId,
                userPic = UserManager.instance.userAvatar.orEmpty(),
                commentContent = text,
                publishDate = formatPublishTime(System.currentTimeMillis()),
                type = PRAISE_TYPE_ALBUM, objId = albumId,
                commentPic = mViewModel?.getUpLoadImageUrl(commentImgLayout?.getImagePath().orEmpty()).orEmpty()))

        commentImgLayout?.gone()
        commentImgLayout?.clearImagePath()
        clearEmptyBinder()//删除评论空页面
        val filter = mBinderList.filter { it is CommentListEmptyBinder }.toMutableList()
        mAdapter.notifyAdapterRemoved(filter) {
            mBinderList.removeAll(filter)
            //        在评论数据中添加新数据
            CommentHelper.addCommentBinder(hotCommentBinderList, commentListBinder)
//        上方有固定的title和图片binder和评论头故此处需要+3
            var tagPosition = hotCommentBinderList.indexOfFirst { it is CommentListBinder } + 3
            if (tagPosition < 0) {
                mBinderList.add(commentListBinder)
            } else {
                mBinderList.add(tagPosition, commentListBinder)
            }
//        adapter中添加新数据
            mAdapter.notifyAdapterInsert(tagPosition, commentListBinder)
            updateCommentTitle(false)
            mBinderList.filter { it is CommentListTitleBinder }.forEach {
                it.notifyAdapterSelfChanged()
            }

//        if (mAdapter.itemCount >= 3) {//上方有固定的title和图片binder,第三条为评论title或评论item
//            albumRv?.scrollToPosition(2)//滑动到2说明滑动到评论处
//        }
            resetInput(CommentHelper.UpdateBarState.ADD)
        }

    }

    private fun clearEmptyBinder() {
        val filter = hotCommentBinderList.filter { it is CommentListEmptyBinder }
        filter.forEach {
            mAdapter.notifyAdapterRemoved(it)
        }
        hotCommentBinderList.removeAll(filter)
        mBinderList.removeAll(filter)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleView?.apply {
            setTitleBackground(R.color.color_ffffff)
            setTitleColor(false)
            setListener(back = { finish() }, moreClick = {
                showShareDialog()
            }, attentionClick = { v, userId ->
                mViewModel?.follow(userId)
            })
        }
    }

    private fun getShareInfoData(sharePlatform: SharePlatform) {
        mViewModel?.getShareExtendInfo(CommConstant.SHARE_TYPE_ALBUM, albumId,userId, extend = sharePlatform)
    }

    override fun initData() {
        initData(false)
    }

    private fun initData(isLoaded: Boolean) {
        if (isLoaded.not()) {
            mBinding?.setVariable(BR.viewModel, mViewModel)
        }
        //        初始化标题和相册图片信息
        albumBinder.reset()
        mAdapter.notifyAdapterAdded(mutableListOf(titleBinder, albumBinder))
        mBinderList.add(titleBinder)
        mBinderList.add(albumBinder)
        mBinderList.add(CommentListTitleBinder(CommentTitleViewBean(0L)))
        mViewModel?.setTitleBar()
        mViewModel?.loadDetailData(albumId, userId)
        loadCommentData(false)
        initBottomBar()

    }

    override fun startObserve() {
//内容相关
        detailObserve()
// 评论相关
        commentObserve()
//关注分享
        shareAndFollow()
//        相册图片
        albumImageObserve()
//      更新相册名称
        updateNameObserve()
//        登录相关
        loginEventObserve()
        //        监听删除评论
        deleteCommentEventObserve()
    }

    private fun deleteCommentEventObserve() {
        //    评论详情中删除评论
        LiveEventBus.get(DELETE_COMMENT, com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java)
                .observe(this, Observer {
                    deleteComment(it?.commentId ?: 0L)
                }) //    评论详情中删除评论
        LiveEventBus.get(DELETE_ALBUM_IMAGE, com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java)
                .observe(this, Observer {
//                    id为图片id 去掉相册中的图片
                    albumBinder.deleteImageById(it?.commentId ?: 0L)

                })

    }

    private fun deleteComment(commentId: Long) {
        CommentHelper.deleteComment(commentId, mBinderList, hotCommentBinderList, mAdapter)
        updateCommentTitle(true)
        resetInput(CommentHelper.UpdateBarState.DELETE)
    }

    private fun shareAndFollow() {
        //        关注
        mViewModel?.followState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        showToast(com.kotlin.android.ugc.detail.component.R.string.ugc_follow_success)
                        titleView?.updateFollow(true)
                    } else {
                        bizMsg?.showToast()
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        })


        //        分享
        mViewModel?.shareExtendUIState?.observe(this,ShareObserver(this))
    }

    private fun showShareDialog() {
        if (isLogin() && UserManager.instance.userId == userId) {//登录了且是自己的文章，就显示删除按钮
            showShareDialog(null, ShareFragment.LaunchMode.ADVANCED, SharePlatform.DELETE, SharePlatform.COPY_LINK, event = shareAction)
        } else {
            showShareDialog(null, ShareFragment.LaunchMode.ADVANCED, SharePlatform.COPY_LINK, event = shareAction)
        }
    }

    private fun albumImageObserve() {
        //        删除相册
        mViewModel?.deleteAlbumState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (result) {
                        showToast(R.string.delete_success)
                        finish()
                    }
                }

                error?.showToast()
                netError?.showToast()
            }
        })
        //        往相册中上传图片
        mViewModel?.uploadImageAlbumState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (this.isNotEmpty()) {
                        albumBinder.bean.albumList.addAll(0, this)
                        notifyAdapterData()
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        })
    }

    private fun commentObserve() {
        mViewModel?.hotCommentListState?.observe(this, Observer {
            it?.apply {
                smartRefreshLayout?.finishLoadMore()
                success?.apply {
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    hotCommentBinderList.addAll(this.commentBinderList)
                    mBinderList.addAll(hotCommentBinderList)
                    notifyAdapterData()
                }
            }
        })

        mViewModel?.newCommentListState?.observe(this, Observer {
            it?.apply {
                smartRefreshLayout?.finishLoadMore(500)
                success?.apply {
                    hotCommentBinderList.addAll(this.commentBinderList)
                    mBinderList.addAll(hotCommentBinderList)
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    notifyAdapterData()
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

                    sendMessage(barButton?.text.orEmpty(), this)
                }

                error?.showToast()
                netError?.showToast()
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
                            var emptyBinder = CommentListEmptyBinder()
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
                if (showLoading) {
                    showProgressDialog(behavior = ProgressDialogFragment.Behavior.MULTIPART)
                }

                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    userId = this.userHomePage?.userId ?: 0L//相册所有人id
                    albumBinder.albumUserId = userId
                    setTitleAndPraise(this)
                    titleBinder.updateBean(name.orEmpty(), albumId, userId)
                    loadImageList(false)
                }

                error?.run {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.run {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }
                if (isEmpty) {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
            }
        })

        mViewModel?.albumState?.observe(this, Observer {
            it?.apply {
                success?.run {
                    dismissProgressDialog()
                    albumBinder.updateData(this)
                }

                error?.run {
                    dismissProgressDialog()
                    showToast(this)
                }
                netError?.run {
                    dismissProgressDialog()
                    showToast(this)
                }

            }
        })
    }

    private fun updateNameObserve() {
        LiveEventBus.get(ALBUM_UPDATE, UpdateAlbumEvent::class.java)
                .observe(this, Observer {
                    if (it.isUpdateSuccess) {//修改成功
                        //                        修改相册名称
                        this@UgcAlbumActivity.showOrHideSoftInput(barButton.getEditTextView())
                        titleBinder.updateAlbumName(it.newAlbumName)
                    }
                })
    }

    private fun loginEventObserve() {
        LiveEventBus.get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java).observe(this, Observer {
            if (it?.isLogin == true) {//登录成功
                mAdapter.notifyAdapterRemoved(mBinderList)
                mAdapter.notifyAdapterRemoved(hotCommentBinderList)
                hotCommentBinderList.clear()
                isNewComment = false
                mBinderList.clear()
                initData(true)
            }
        })

    }

    private var mAlbumInfo: AlbumInfo? = null

    /**
     * 设置相册title和标题
     */
    private fun setTitleAndPraise(albumInfo: AlbumInfo) {
        this.mAlbumInfo = albumInfo
        barButton?.style = PublishCommentView.Style.COMMENT
        albumInfo.userHomePage?.apply {
            titleView?.setData(UgcTitleBarBean.convert(this, formatPublishTime(albumInfo.enterTimeLong)))
        }

        barButton?.setTipsByType(BarButtonItem.Type.PRAISE, albumInfo.praiseCount)
        barButton?.setTipsByType(BarButtonItem.Type.COMMENT, albumInfo.commentCount)
        val userPraised = albumInfo.userPraised ?: 0L
        barButton?.isSelectedByType(BarButtonItem.Type.PRAISE, userPraised == 1L)
        mBinderList.filter { it is CommentListTitleBinder }.forEach {
            (it as CommentListTitleBinder).bean.totalCount = albumInfo.commentCount
        }
        notifyAdapterData()
    }

    private fun notifyAdapterData() {
        mAdapter.notifyAdapterDataSetChanged(mBinderList,false)
    }

    private fun resetInput(state: CommentHelper.UpdateBarState) {
        when (state) {
            CommentHelper.UpdateBarState.DELETE -> {//删除评论
                mAlbumInfo?.commentCount = (mAlbumInfo?.commentCount ?: 0L) - 1L
            }
            CommentHelper.UpdateBarState.ADD -> {//添加评论
                mAlbumInfo?.commentCount = (mAlbumInfo?.commentCount ?: 0L) + 1L
            }
        }

        mAlbumInfo?.apply {
            setTitleAndPraise(this)
        }
    }

    /**
     * 更新标题数量
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        updateCommentTitleList(mBinderList, isDelete)
    }

    /**
     * 更新标题数量
     */
    private fun updateCommentTitleList(list: MutableList<MultiTypeBinder<*>>, isDelete: Boolean) {
        list.filter { it is CommentListTitleBinder }.forEach {
            (it as CommentListTitleBinder).bean.totalCount = if (isDelete) {
                if (it.bean.totalCount - 1 >= 0) it.bean.totalCount - 1 else 0
            } else {
                it.bean.totalCount + 1
            }
        }
        mAdapter.notifyAdapterDataSetChanged(list)
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
        val show = state == MultiStateView.VIEW_STATE_CONTENT
        barButton?.visible(show)
        titleView?.visible(show)
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