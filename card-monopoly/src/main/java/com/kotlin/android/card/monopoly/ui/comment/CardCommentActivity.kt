package com.kotlin.android.card.monopoly.ui.comment

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ActSuitCommentBinding
import com.kotlin.android.card.monopoly.ui.CardSuitCommentViewModel
import com.kotlin.android.comment.component.DELETE_COMMENT
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
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.router.liveevent.COLLECTION_OR_CANCEL
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ugc.detail.component.binder.MovieBinder
import com.kotlin.android.ugc.detail.component.binder.UgcTitleBinder
import com.kotlin.android.ugc.detail.component.binder.UgcWebViewBinder
import com.kotlin.android.ugc.detail.component.isPublished
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.act_suit_comment.*

/**
 * @desc ????????????????????????
 * @author zhangjian
 * @date 2021-07-07 11:12:22
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_SUIT_COMMENT)
class CardCommentActivity :
    BaseVMActivity<CardSuitCommentViewModel, ActSuitCommentBinding>() {
    companion object {
        const val ARTICLE_CONTENT_ID = "article_content_id"
        const val ARTICLE_TYPE = "article_type"
        const val CARD_COMMENT_TITLE = "card_comment_title"
    }

    private var type: Long = 0L
    private var suitId: Long = 0L
    private var mPageTitle: String = ""

    private var isNewComment = false//??????????????????????????????
    private var isCommenting: Boolean = false//????????????????????????
    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//????????????
    private var commonBarBean: UgcCommonBarBean? = null//????????????reset???????????????????????????
    private val detailBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    //??????
    private val titleBinderList = mutableListOf<MultiTypeBinder<*>>()
    private var scrollHelper: DetailScrollHelper? = null//????????????????????????
    private var isPublished: Boolean = true

    @Volatile
    private var mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()


    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(
            mBinding?.articleRv as RecyclerView,
            LinearLayoutManager(this)
        ).apply {
            setOnClickListener(::handleListAction)
        }
    }

    override fun initVariable() {
        super.initVariable()
        type = intent?.getLongExtra(ARTICLE_TYPE, 0L) ?: 0L
        suitId = intent?.getLongExtra(ARTICLE_CONTENT_ID, 0L) ?: 0L
        mPageTitle = intent?.getStringExtra(CARD_COMMENT_TITLE) ?: ""
    }

    override fun initView() {
        scrollHelper = DetailScrollHelper(articleRv)
        initSmartLayout()
        initTitleView()
        initBarButton()
        handlePraiseObserve()
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
            style = PublishCommentView.Style.WITH_NONE
            inputEnable(true)
            action = { barType, isSelected ->
                when (barType) {
                    BarButtonItem.Type.COMMENT -> {
                        scrollHelper?.handleScrollToComment(this@CardCommentActivity, mBinderList)
                    }
                    BarButtonItem.Type.PRAISE -> {//??????
                        isPublished.isPublished {
                            handlePraiseUp(getSelectedStatusByType(barType), this)
                        }
                    }
                    BarButtonItem.Type.DISPRAISE -> {//??????
                        isPublished.isPublished {
                            handlePraiseDown(getSelectedStatusByType(barType), this)
                        }
                    }
                    BarButtonItem.Type.FAVORITE -> {//??????
                        isPublished.isPublished {
                            handleCollection(getSelectedStatusByType(barType))
                        }
                    }
                    BarButtonItem.Type.SEND -> {//????????????
                        isPublished.isPublished {
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
                        this@CardCommentActivity.showOrHideSoftInput(this.getEditTextView())

                    }

                }
            }
            editAction = {//?????????????????????????????????????????????????????????
                afterLogin {
                    isPublished.isPublished {
                        setEditStyle()
                    }
                }
            }

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
            suitId,
            path = commentImgLayout?.getImagePath().orEmpty(),
            content = text
        )
    }

    /**
     * ?????????????????????????????????
     */
    private fun handleCollection(isCancel: Boolean) {
        showInteractiveDialog()
        mViewModel?.collectionOrCancel(
            CommConstant.COLLECTION_OBJ_TYPE_ARTICLE,
            suitId,
            isCancel,
            this
        )
        LiveEventBus.get(COLLECTION_OR_CANCEL)
            .post(com.kotlin.android.app.router.liveevent.event.CollectionState(CommConstant.COLLECTION_TYPE_ARTICLE))
    }


    /**
     * ?????????????????????????????????
     */
    private fun handlePraiseDown(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        mViewModel?.praiseDownOrCancel(type, suitId, isCancel, extend)
    }

    private fun initTitleView() {
        mBinding?.mTitle?.apply {
            setThemeStyle(ThemeStyle.STANDARD)
            setState(State.NORMAL)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(mPageTitle, alwaysShow = true)
        }
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
        barButton?.visible(state == MultiStateView.VIEW_STATE_CONTENT || state == MultiStateView.VIEW_STATE_EMPTY)
    }

    override fun initData() {
        loadCommentData(false)
        loadPraiseState()
    }

    override fun startObserve() {
        observeCurUserPraise()
        commentObserve()
        deleteCommentEventObserve()

    }

    //    ???????????????????????????
    private fun deleteCommentEventObserve() {
        LiveEventBus.get(DELETE_COMMENT, com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java)
            .observe(this, Observer {
                deleteComment(it?.commentId ?: 0L)
            })
    }

    private fun deleteComment(commentId: Long) {
        CommentHelper.deleteComment(commentId, mBinderList, hotCommentBinderList, mAdapter)
        updateCommentTitle(true)
        resetInput(CommentHelper.UpdateBarState.DELETE)
    }

    /**
     * ??????????????????????????????????????? startObserver???
     */
    private fun handlePraiseObserve() {
        //?????????????????????
        mViewModel?.praiseUpState?.observe(this,CommonObserver(
            this,
            CommonObserver.TYPE_PRAISE_UP,
            barButton,
            BarButtonItem.Type.PRAISE
        ))
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
            loadCommentData(false)
        }
    }

    private fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, suitId, type, isNewComment, isMore)
    }

    private fun loadPraiseState() {
        mViewModel?.getPraiseState(CommConstant.PRAISE_OBJ_TYPE_CARD_SUIT_REPLY, suitId)
    }

    /**
     * ?????????????????????????????????
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        if (extend is CommentListBinder) {
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(
                CommConstant.getPraiseUpType(type, CommConstant.COMMENT_PRAISE_ACTION_COMMENT),
                commentId, isCancel, extend
            )
        } else {
            mViewModel?.praiseUpOrCancel(
                CommConstant.getPraiseUpType(type, CommConstant.COMMENT_PRAISE_ACTION_COMMENT),
                suitId,
                isCancel,
                extend
            )
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }


    /**
     * ????????????
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(type, (binder as CommentListBinder).bean.commentId, binder)
    }

    override fun onResume() {
        super.onResume()
    }


    private fun commentObserve() {
        //???????????????????????????????????????
        mViewModel?.hotCommentListState?.observe(this) {
            mBinding?.smartRefreshLayout?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    titleBinderList.add(
                        CommentListTitleBinder(
                            CommentTitleViewBean(this.totalCount)
                        )
                    )
                    if (titleBinderList.size <= 1) {
                        mBinderList.addAll(titleBinderList)
                    }
                    if (hotCommentBinderList.isEmpty()) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        "??????????????????$hotCommentBinderList".e()
                        mergerBinder()
                    }
                    mBinding?.smartRefreshLayout?.setNoMoreData(noMoreData)
                    if (loadMore) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mBinderList.addAll(hotCommentBinderList)
                        mAdapter.notifyAdapterDataSetChanged(mBinderList)
                    }
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
        }
        //????????????
        mViewModel?.newCommentListState?.observe(this) {
            mBinding?.smartRefreshLayout?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    hotCommentBinderList.addAll(this.commentBinderList)
                    updateCommentCanComment()
                    mBinderList.addAll(hotCommentBinderList)
                    mBinding?.smartRefreshLayout?.setNoMoreData(noMoreData)
                    "???????????????????????????$mBinderList".d()
                    mAdapter.notifyAdapterDataSetChanged(mBinderList)
                    "??????????????????????????????$mBinderList".d()
                }
            }
        }

        //????????????
        mViewModel?.saveCommentState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)
                isCommenting = showLoading
                success?.apply {
                    showToast(R.string.comment_component_publish_success)
                    sendMessage(barButton?.text.orEmpty(), this)
                }

                netError?.showToast()
                error?.showToast()
            }
        }
        //??????????????????
        mViewModel?.deleteCommentState?.observe(this) {
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
                            mAdapter.notifyAdapterDataSetChanged(mBinderList)
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
        }
    }

    /**
     * ????????????????????????
     */
    private fun initCommonLayout(barBean: Long) {
        val data = UgcCommonBarBean.CommentSupport(praiseUpCount = barBean)
        this.commonBarBean = UgcCommonBarBean(false, UgcCommonBarBean.CreateUser(), true, data)
        resetInput(CommentHelper.UpdateBarState.INIT)
    }

    /**
     * observe ????????????
     */
    private fun observeCurUserPraise() {
        mViewModel?.praiseUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    // ?????????????????????
                    initCommonLayout(this.upCount)
                    barButton.apply {
                        val userPraise = currentUserPraise ?: 0L//??????????????????
                        isSelectedByType(BarButtonItem.Type.PRAISE, userPraise == 1L)//???????????????
                        try {
                            this.setTipsByType(BarButtonItem.Type.PRAISE, upCount)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun sendMessage(text: String, commentId: Long) {
        //???????????????commentId
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
                objId = suitId,
                commentPic = mViewModel?.getUpLoadImageUrl(
                    commentImgLayout?.getImagePath().orEmpty()
                ) ?: ""
            )
        )
        mBinding?.commentImgLayout?.gone()
        mBinding?.commentImgLayout?.clearImagePath()

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
        CommentHelper.resetInput(commonBarBean, barButton, updateBarState, true, true)
    }

    /**
     * ??????????????????
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        CommentHelper.updateCommentTitleList(mBinderList, isDelete)
    }

    private fun updateCommentCanComment() {
        hotCommentBinderList.filter { it is CommentListBinder }.forEach {
            (it as CommentListBinder).setCommentPmsn(commonBarBean?.commentPmsn)
        }
    }

    private fun isContainsDetailBinder(): Boolean {
        synchronized(this) {
            val titleIndex = mBinderList.indexOfFirst { it is UgcTitleBinder }
            val webIndex = mBinderList.indexOfFirst { it is UgcWebViewBinder }
            val movieIndex = mBinderList.indexOfFirst { it is MovieBinder }
            return titleIndex >= 0 || movieIndex >= 0
        }
    }

    private fun mergerBinder() {
        synchronized(this) {
            val isNotContainsDetailBinder = isContainsDetailBinder().not()
            if (isNotContainsDetailBinder) {
                mBinderList.addAll(0, detailBinderList)
            }

            mBinderList.removeAll { it is CommentListBinder || it is CommentListEmptyBinder }

            mBinderList.addAll(hotCommentBinderList)
            "???????????????mBinderList:$mBinderList".d()
            "hotCommentBinderList:$hotCommentBinderList".d()
            mAdapter.notifyAdapterDataSetChanged(mBinderList)
        }
    }

    override fun onBackPressed() {
        if (barButton?.style == PublishCommentView.Style.EDIT_WITHOUT_MOVIE) {
            resetInput(CommentHelper.UpdateBarState.INIT)
            return
        }
        super.onBackPressed()
    }

}