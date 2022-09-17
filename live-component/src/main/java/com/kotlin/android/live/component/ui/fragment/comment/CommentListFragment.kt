package com.kotlin.android.live.component.ui.fragment.comment

import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.FragmentCommentListBinding
import com.kotlin.android.live.component.ui.detail.LiveDetailActivity
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.user.afterLogin
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.fragment_comment_list.*

/**
 * create by vivian.wei on 2021/3/2
 * description:直播评论列表
 */
class CommentListFragment : BaseVMFragment<DetailBaseViewModel, FragmentCommentListBinding>(),
        MultiStateView.MultiStateListener {

    companion object {
        const val COMMENT_SIZE = 140 // 评论字数限制
    }

    private var mLiveId = 0L
    private var mIsFirst = true
    // 评论
    private var mCommentAdapter: MultiTypeAdapter? = null
    private val mNewCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf() // 最新评论
    private var mIsCommenting: Boolean = false   //是否在发表评论中

    override fun initVM(): DetailBaseViewModel = viewModels<DetailBaseViewModel>().value

    override fun initView() {
        // 评论列表
        createCommentAdapter()
        // 初始化事件
        initEvent()
        // 底部评论组件
        initBarButton()
        // 刷新组件
        initRefreshLayout()
        // 顶部分隔条渐变
        ShapeExt.setGradientColorWithColor(mLiveDetailCommentShadowView, GradientDrawable.Orientation.TOP_BOTTOM,
                getColor(R.color.color_f9f9fb), getColor(R.color.color_ffffff))
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()

        //首次加载
        if (mIsFirst) {
            val mDetailBean = (activity as? LiveDetailActivity)?.getDetailDataBean()
            mDetailBean?.let {
                mLiveId = it.liveId
                loadCommentData(false)
            }
        }
    }

    override fun startObserve() {
        // 最新评论列表
        observeComment()
        // 发表评论
        observeSaveComment()
        // 登录成功后回来刷新页面
        loginEventObserve()
        // 点赞、取消点赞
        observePraise()
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                // 获取评论列表
                loadCommentData(false)
            }
        }
    }

    override fun destroyView() {
    }

    /**
     * 评论列表Adapter
     */
    private fun createCommentAdapter() {
        mCommentAdapter = createMultiTypeAdapter(mLiveDetailCommentRv, LinearLayoutManager(mContext))
        mCommentAdapter?.setOnClickListener(::handleListAction)
    }

    /**
     * 评论列表点击事件
     */
    private fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            // 点赞按钮
            com.kotlin.android.comment.component.R.id.likeTv -> {
                val isCancel = (binder as CommentListBinder).bean.isLike()
                handlePraiseUp(isCancel, binder)
            }
        }
    }

    private fun initEvent() {
        mMultiStateView.setMultiStateListener(this)
    }

    fun onBackPressed(): Boolean {
        return if (publishCommentView?.style != PublishCommentView.Style.WITH_NONE) {
            publishCommentView?.style = PublishCommentView.Style.WITH_NONE
            publishCommentView?.inputEnable(true)
            true
        } else {
            false
        }
    }

    /**
     * 初始化底部评论组件
     */
    private fun initBarButton() {
        publishCommentView?.apply {
            editStyle = PublishCommentView.EditStyle.WITHOUT_MOVIE_ONLY
            style = PublishCommentView.Style.WITH_NONE
            inputEnable(true)
            action = { barType, _ ->
                when (barType) {
                    BarButtonItem.Type.SEND -> {    //发送评论
                        saveComment(text)
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        getActivity().showOrHideSoftInput(this.getEditTextView())
                    }
                    BarButtonItem.Type.PHOTO -> {   //图片
                        activity?.let {
                            it.showPhotoAlbumFragment(false, limitedCount = 1L)
                                    .actionSelectPhotos = { photos ->
                                photos.e()
                                if (photos.isNotEmpty()) {
                                    commentImgLayout.setPhotoInfo(photos[0])
                                }
                            }
                        }
                    }
                }
            }
            editAction = {//点击文本如果没有登录直接跳转到登录页面
                afterLogin {
                    setEditStyle()
                }
            }
        }
        activity?.let {
            KeyBoard.assistActivity(it, true)
        }
    }

    /**
     * observe最新评论列表
     */
    private fun observeComment() {
        mViewModel?.newCommentListState?.observe(this) {
            it?.apply {
                success?.apply {
                    if(mIsFirst) {
                        mIsFirst = false
                    }
                    mRefreshLayout?.finishLoadMore()
                    mNewCommentBinderList.addAll(this.commentBinderList)
                    mCommentAdapter?.notifyAdapterAdded(mNewCommentBinderList)
                    mRefreshLayout?.setNoMoreData(noMoreData)

                }

                error?.apply {
                    if(mIsFirst) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    } else {
                        mRefreshLayout?.finishLoadMore()
                        this.showToast()
                    }
                }

                netError?.apply {
                    if(mIsFirst) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    } else {
                        mRefreshLayout?.finishLoadMore()
                        this.showToast()
                    }
                }
            }

        }
    }

    /**
     * observe 发布评论
     */
    private fun observeSaveComment() {
        // 发表评论
        mViewModel?.saveCommentState?.observe(this) {
            it?.apply {
                mIsCommenting = showLoading
                if (showLoading.not()) {
                    dismissProgressDialog()
                }

                success?.apply {
                    showToast(R.string.comment_component_publish_success)
                    // 显示新发布的评论
                    sendMessage(publishCommentView?.text.orEmpty(), this)
                }

                netError?.let {
                    showToast(R.string.live_component_live_comment_publish_net_error)
                }
                error?.showToast()
            }
        }
    }

    /**
     * 登录成功后回来刷新页面
     */
    private fun loginEventObserve() {
        LiveEventBus.get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java).observe(this) {
            //登录成功
            if (it?.isLogin == true) {
                // 刷新数据
                mCommentAdapter?.notifyAdapterClear()
                mNewCommentBinderList.clear()
                loadCommentData(false)
            }
        }
    }

    /**
     * observe 点赞、取消点赞
     */
    private fun observePraise() {
        // 点赞、取消点赞
        activity?.let {
            mViewModel?.praiseUpState?.observe(this,
                    CommonObserver(it,
                            CommonObserver.TYPE_PRAISE_UP,
                            publishCommentView,
                            BarButtonItem.Type.PRAISE))
        }
    }

    /**
     * 发布评论
     */
    private fun saveComment(content: String) {
        var text = content.trim()
        // 空评论
        if (TextUtils.isEmpty(text)) {
            showToast(com.kotlin.android.comment.component.R.string.comment_detail_cannt_send_comment)
            return
        }
        // 字数限制
        if (text.length > COMMENT_SIZE) {
            showToast(getString(R.string.comment_size, COMMENT_SIZE))
            return
        }
        if (mIsCommenting) return
        mIsCommenting = false
        showInteractiveDialog()

        mViewModel?.saveComment(CommContent.TYPE_LIVE, mLiveId,
                path = commentImgLayout?.getImagePath().orEmpty(), content = text)
    }

    /**
     * 初始化刷新组件
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

    /**
     * 加载评论列表
     */
    private fun loadCommentData(isMore: Boolean) {
        activity?.let {
            mViewModel?.loadCommentList(it, mLiveId, CommContent.TYPE_LIVE, true, isMore)
        }
    }

    /**
     * 处理点赞、取消点赞操作
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        if (isLogin()) {
            activity?.let {
                // CommonObserver类，点赞的回调onChanged()里，loading消失用的是activity
                it.showProgressDialog()
            }
        }
        if (extend is CommentListBinder) {
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(
                    CommConstant.getPraiseUpType(CommConstant.PRAISE_OBJ_TYPE_LIVE, CommConstant.COMMENT_PRAISE_ACTION_COMMENT),
                    commentId, isCancel, extend)
        }
    }

    /**
     * 显示loading
     */
    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    /**
     * 显示新发布的评论
     */
    private fun sendMessage(text: String, commentId: Long) {
        var binderList = mNewCommentBinderList
        var index = binderList.indexOfFirst { it is CommentListBinder }
        activity?.let {
            val commentListBinder = CommentListBinder(it, CommentViewBean(commentId = commentId,
                    userName = UserManager.instance.nickname,
                    userId = UserManager.instance.userId,
                    userPic = UserManager.instance.userAvatar.orEmpty(),
                    commentContent = text,
                    publishDate = formatPublishTime(System.currentTimeMillis()),
                    type = CommConstant.PRAISE_OBJ_TYPE_LIVE,
                    objId = mLiveId,
                    commentPic = mViewModel?.getUpLoadImageUrl(commentImgLayout?.getImagePath().orEmpty())?:""))

            commentImgLayout?.gone()
            commentImgLayout?.clearImagePath()

            val filter =binderList.filter { it is CommentListEmptyBinder }.toMutableList()
            mCommentAdapter?.notifyAdapterRemoved(filter) {
                index = if(index > -1) index else 0
                binderList.removeAll(filter)
                binderList.add(index , commentListBinder)

                mCommentAdapter?.notifyAdapterInsert(0, mutableListOf(commentListBinder)){
                    mLiveDetailCommentRv?.scrollToPosition(0)
                    publishCommentView?.apply {
                        style = PublishCommentView.Style.WITH_NONE
                        inputEnable(true)
                        resetInput()
                    }
                }

            }

        }
    }

}