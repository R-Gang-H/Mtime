package com.kotlin.tablet.ui.details

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.filmlist.Movy
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.date.toStringWithDateFormat
import com.kotlin.android.ktx.ext.date.yyyy_MM_dd
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.isSelf
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import com.kotlin.android.user.login.gotoLoginPage
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.*
import com.kotlin.tablet.KEY_FILM_LIST_ID
import com.kotlin.tablet.KEY_TO_DETAILS_SOURCE
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.FilmDetailsBinder
import com.kotlin.tablet.databinding.ActivityFilmListDetailsBinding
import com.kotlin.tablet.ui.details.dialog.BottomDialog
import com.kotlin.tablet.ui.details.dialog.DialogEnum
import com.kotlin.tablet.ui.details.dialog.FilmDetailsShareDialog
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import org.jetbrains.anko.toast

/**
 * 片单详情页面
 */
@Route(path = RouterActivityPath.TABLET.FILM_LIST_DETAILS)
class FilmListDetailsActivity :
    BaseVMActivity<FilmListDetailsViewModel, ActivityFilmListDetailsBinding>(),
    OnLoadMoreListener, MultiStateView.MultiStateListener {

    private var titleBar: TitleBar? = null
    var filmListId = 0L//片单id
    var source = 0L//【选填】默认值为0； 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
    private var type = 0L//列表展示类型：0全部，1可播放，2未看过（默认全部）
    private val bottomDialog by lazy { BottomDialog() }//底部弹窗
    private val dialog by lazy { Dialog(this, R.style.common_dialog) }//删除dialog
    private var shareDialog: FilmDetailsShareDialog? = null
    private var action: Long = 0
    private lateinit var mAdapter: MultiTypeAdapter

    override fun initView() {
        mBinding?.apply {
            mRefreshLayout.setEnableRefresh(false)//禁止刷新
            mRefreshLayout.setOnLoadMoreListener(this@FilmListDetailsActivity)
            mMultiStateView.setMultiStateListener(this@FilmListDetailsActivity)
            mAdapter = createMultiTypeAdapter(mRecycleView)
            mCheckLayout.setBackground(
                colorRes = R.color.white,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
        }
        initListener()
        initShareDialog()
    }

    private fun initShareDialog() {
        if (shareDialog == null) {
            shareDialog = FilmDetailsShareDialog(filmListId)
        }
    }

    private fun initListener() {
        mBinding?.apply {
            tvCollection.onClick {
                if (isLogin()) {
                    mViewModel?.collect(action, filmListId)
                } else {
                    gotoLoginPage(this@FilmListDetailsActivity, requestCode = 100)
                }
            }
            //可播放
            tvNumPlayable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tvNumPlayable.setCompoundDrawablesAndPadding(
                        leftResId = R.drawable.ic_label_playunable,
                        padding = 4.dp
                    )
                    tvNumUnread.isChecked = false
                    type = 1L
                    mViewModel?.loadPageMovies(true, filmListId, type, source)
                } else {
                    tvNumPlayable.setCompoundDrawablesAndPadding(
                        leftResId = R.drawable.ic_label_playabletag,
                        padding = 4.dp
                    )
                }
                if (!tvNumPlayable.isChecked && !tvNumUnread.isChecked) {
                    type = 0L
                    mViewModel?.loadPageMovies(true, filmListId, type, source)
                }
            }
            //未看过
            tvNumUnread.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tvNumPlayable.isChecked = false
                    type = 2L
                    mViewModel?.loadPageMovies(true, filmListId, type, source)
                }
                if (!tvNumPlayable.isChecked && !tvNumUnread.isChecked) {
                    type = 0L
                    mViewModel?.loadPageMovies(true, filmListId, type, source)
                }
            }

        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleBar = TitleBarManager.with(this, themeStyle = ThemeStyle.STANDARD_STATUS_BAR)
            .setTitle(
                title = getString(R.string.tablet_film_details_title),
                isBold = true,
                gravity = Gravity.CENTER,
            )
            .back {
                finish()
            }
    }

    override fun initVM(): FilmListDetailsViewModel {
        filmListId = intent.getLongExtra(KEY_FILM_LIST_ID, 0)
        source = intent.getLongExtra(KEY_TO_DETAILS_SOURCE, 0)
        return viewModels<FilmListDetailsViewModel>().value
    }


    override fun initData() {
        filmListId.let {
            mViewModel?.loadBasicInfo(it, source)
            mViewModel?.loadPageMovies(true, it, type, source)
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun startObserve() {
        //上半部分
        mViewModel?.baseInfoState?.observe(this) {
            it.apply {
                mBinding?.mMultiStateView.complete(this)
                success?.apply {
                    mBinding?.bean = this
                    mBinding?.tvLastModifyTimeStr?.text = getString(
                        R.string.tablet_film_list_details_update,
                        lastModifyTime?.toStringWithDateFormat(
                            "yyyy年MM月dd日"
                        )
                    )
                    if (this.userId?.let { it1 -> isSelf(it1) } == true) {//当前浏览用户跟创建人是同一人时
                        updateTitleBar(isShow = true, approvalStatus)
                        mBinding?.isUser = true
                    } else {
                        updateTitleBar(isShow = false, approvalStatus)
                        mBinding?.isUser = false
                    }
                    mBinding?.apply {
                        ivCoverUrl.loadImage(
                            coverUrl,
                            screenWidth,
                            200.dp,
                            defaultImgRes = R.drawable.icon_film_list_bg_k
                        )
                        if (numFavorites != null) {
                            if (numFavorites!! > 9999) {//收藏数
                                tvNumFavorites.text =
                                    getString(
                                        R.string.tablet_main_num_favorites,
                                        formatCount(numFavorites!!)
                                    )
                            } else {
                                tvNumFavorites.text =
                                    getString(
                                        R.string.tablet_main_num_favorites,
                                        numFavorites.toString()
                                    )
                            }
                        }
                        if (numRead != null && numMovie != null) {
                            if (numMovie!! > 9999) {
                                tvNumMovie.text = getString(
                                    R.string.tablet_main_numRead_look,
                                    numRead.toString(),
                                    formatCount(numMovie!!)
                                )
                            } else if (numRead!! > 9999) {
                                tvNumMovie.text =
                                    getString(
                                        R.string.tablet_main_numRead_look,
                                        formatCount(numRead!!),
                                        numMovie.toString()
                                    )
                            } else if (numMovie!! > 9999 && numRead!! > 9999) {
                                tvNumMovie.text =
                                    getString(
                                        R.string.tablet_main_numRead_look,
                                        formatCount(numRead!!),
                                        formatCount(numMovie!!)
                                    )
                            } else {
                                tvNumMovie.text =
                                    getString(
                                        R.string.tablet_main_numRead_look,
                                        numRead.toString(),
                                        numMovie.toString()
                                    )
                            }
                        }
                        ivUserAvatarUrl.onClick {//跳转对应用户的个人主页页面
                            userId?.let { it1 ->
                                getProvider(ICommunityPersonProvider::class.java)?.startPerson(
                                    it1
                                )
                            }
                        }
                        tvUserNickName.onClick {//跳转对应用户的个人主页页面
                            userId?.let { it1 ->
                                getProvider(ICommunityPersonProvider::class.java)?.startPerson(
                                    it1
                                )
                            }
                        }
                        if (favorites) {//已收藏
                            action = 2
                            tvCollection.setText(R.string.tablet_film_details_collection)
                        } else {
                            action = 1
                            tvCollection.setText(R.string.tablet_film_details_unCollection)
                        }
                        when (approvalStatus) {//审核状态
                            10L -> {
                                tvApprovalStatusStr.setBackground(
                                    strokeColorRes = R.color.color_979797,
                                    strokeWidth = 1.dp,
                                    cornerRadius = 12.dpF,
                                    direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM
                                )
                                tvApprovalStatusStr.setTextColorRes(R.color.color_979797)
                                bottomDialog.isShow = false
                            }
                            20L -> {
                                tvApprovalStatusStr.setBackground(
                                    strokeColorRes = R.color.color_ff5a36,
                                    strokeWidth = 1.dp,
                                    cornerRadius = 12.dpF,
                                    direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM
                                )
                                tvApprovalStatusStr.setTextColorRes(R.color.color_ff5a36)
                                bottomDialog.isShow = false
                            }
                            30L -> {
                                tvApprovalStatusStr.setBackground(
                                    strokeColorRes = R.color.color_1cacde,
                                    strokeWidth = 1.dp,
                                    cornerRadius = 12.dpF,
                                    direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM
                                )
                                tvApprovalStatusStr.setTextColorRes(R.color.color_1cacde)
                            }
                            else -> {
                                tvApprovalStatusStr.setBackground(
                                    strokeColorRes = R.color.color_979797,
                                    strokeWidth = 1.dp,
                                    cornerRadius = 12.dpF,
                                    direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM
                                )
                                tvApprovalStatusStr.setTextColorRes(R.color.color_979797)
                                bottomDialog.isShow = true
                            }
                        }
                    }
                }
            }
        }
        //列表下半部分
        mViewModel?.pageMoviesState?.observe(this) {
            it.apply {
                mBinding?.mRefreshLayout?.complete(this)
                success?.apply {
                    mBinding?.data = this
                    mBinding?.apply {
                        if (numPlayable == 0L && numUnread == 0L) {
                            mCheckLayout.gone()
                        } else {
                            mCheckLayout.visible()
                            if (numUnread!! >= 9999) {
                                tvNumUnread.text = getString(
                                    R.string.tablet_film_details_numUnread,
                                    formatCount(numUnread!!)
                                )
                            } else {
                                tvNumUnread.text = getString(
                                    R.string.tablet_film_details_numUnread,
                                    numUnread.toString()
                                )
                            }
                            if (numPlayable!! >= 9999) {
                                tvNumPlayable.text = getString(
                                    R.string.tablet_film_details_numPlayable,
                                    formatCount(numPlayable!!)
                                )
                            } else {
                                tvNumPlayable.text = getString(
                                    R.string.tablet_film_details_numPlayable,
                                    numPlayable.toString()
                                )
                            }
                        }
                    }
                    if (isRefresh) {
                        mAdapter.notifyAdapterDataSetChanged(
                            getBinder(
                                this.movies,
                                this.filmListType
                            ), isScrollToTop = false
                        )
                    } else {
                        mAdapter.notifyAdapterAdded(getBinder(this.movies, this.filmListType))
                    }
                }
                error?.apply {
                    showToast("请求失败请稍后重试")
                }
            }
        }
        //删除片单
        mViewModel?.deleteState?.observe(this) {
            it.apply {
                success?.apply {
                    showToast("删除成功")
                    finish()
                }
                error?.apply {
                    showToast("请求失败请稍后重试")
                }
            }
        }
        //收藏片单
        mViewModel?.collectState?.observe(this) {
            it.apply {
                success?.apply {
                    mBinding?.apply {
                        if (action == 1L) {
                            tvCollection.setText(R.string.tablet_film_details_collection)
                            showToast("收藏成功")
                        } else {
                            tvCollection.setText(R.string.tablet_film_details_unCollection)
                            showToast("取消收藏")
                        }
                    }
                }
                error?.apply {
                    showToast("请求失败请稍后重试")
                }
            }
        }
    }

    private fun updateTitleBar(isShow: Boolean, approvalStatusStr: Long?) {
        if (isShow) {
            titleBar?.addItem(
                isReversed = true,
                isReset = true,
                drawableRes = R.drawable.ic_title_bar_36_more_h,
                reverseDrawableRes = R.drawable.ic_title_bar_36_more_h_reversed,
                click = {
                    if (approvalStatusStr == 30L) {//审核通过
                        bottomDialog.show(supportFragmentManager, "")
                        bottomDialog.setCallBack { type, _ ->
                            when (type) {
                                DialogEnum.EDIT -> {
                                    getProvider(ITabletProvider::class.java)?.startFilmListCreateActivity(
                                        true,
                                        filmListId
                                    )
                                }
                                DialogEnum.SHARE -> {
                                    shareDialog?.show(supportFragmentManager, "ShareDialog")
                                }
                                DialogEnum.DELETE -> {
                                    showDialog()
                                }
                            }
                        }
                    } else {
                        showToast("请分享审核通过内容")
                    }
                }
            )
        } else {
            titleBar?.addItem(
                isReversed = true,
                isReset = true,
                drawableRes = R.drawable.ic_title_bar_36_share_1,
                reverseDrawableRes = R.drawable.ic_title_bar_36_share_1_reversed,
                click = { shareDialog?.show(supportFragmentManager, "ShareDialog") }
            )
        }
    }

    /**
     * 删除dialog
     */
    private fun showDialog() {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.layout_film_delete_dialog, null)
        val mDialogLayout =
            view.findViewById<LinearLayout>(R.id.mDialogLayout)
        val tvOk =
            view.findViewById<TextView>(R.id.tv_ok)
        val tvCancel =
            view.findViewById<TextView>(R.id.tv_cancel)
        mDialogLayout.setBackground(
            colorRes = R.color.white,
            cornerRadius = 12.dpF,
        )
        tvOk.setBackground(
            cornerRadius = 18.dpF,
            colorRes = R.color.white,
            strokeWidth = 1.dp,
            strokeColorRes = R.color.color_d8d8d8
        )
        tvCancel.setBackground(
            colorRes = R.color.color_20a0da,
            cornerRadius = 18.dpF
        )
        dialog.setContentView(view)
        dialog.create()
        val window = dialog.window
        val attributes = window?.attributes
        attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        attributes?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        attributes?.gravity = Gravity.CENTER
        window?.decorView?.setPadding(44, 0, 44, 0)
        window?.attributes = attributes
        if (!dialog.isShowing) dialog.show()

        tvCancel.onClick {
            dialog.dismiss()
        }
        tvOk.onClick {
            mViewModel?.delete(
                filmListId
            )
        }
    }

    /**
     * 列表binder
     */
    private fun getBinder(
        movies: MutableList<Movy>?,
        filmListType: Long?
    ): List<MultiTypeBinder<*>> {
        val binderList = mutableListOf<MultiTypeBinder<*>>()
        movies?.forEach {
            binderList.add(FilmDetailsBinder(it, this, filmListType))
        }
        return binderList
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadPageMovies(false, filmListId, type, source)
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                filmListId.let {
                    mViewModel?.loadBasicInfo(it, source)
                    mViewModel?.loadPageMovies(true, it, type, source)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            filmListId.let {
                mViewModel?.loadBasicInfo(it, source)
                mViewModel?.loadPageMovies(true, it, type, source)
            }
        }
    }
}