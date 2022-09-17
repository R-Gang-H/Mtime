package com.kotlin.android.home.ui.toplist

import android.graphics.drawable.GradientDrawable
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.toplist.GameRankUser
import com.kotlin.android.app.data.entity.toplist.GameTopList
import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.home.R
import com.kotlin.android.home.BR
import com.kotlin.android.home.databinding.ActToplistGameDetailBinding
import com.kotlin.android.home.ui.toplist.adapter.TopListGameDetailItemBinder
import com.kotlin.android.home.ui.toplist.constant.TopListConstant
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.core.heightValue
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.act_toplist_game_detail.*
import kotlinx.android.synthetic.main.act_toplist_game_detail.mMultiStateView
import kotlinx.android.synthetic.main.layout_toplist_detail_head.*
import kotlin.math.abs

/**
 * @author vivian.wei
 * @date 2020/7/20
 * @desc 游戏榜单详情页
 */
@Route(path = RouterActivityPath.Home.PAGER_TOPLIST_GAME_DETAIL_ACTIVITY)
class TopListGameDetailActivity : BaseVMActivity<TopListGameDetailViewModel, ActToplistGameDetailBinding>(),
        MultiStateView.MultiStateListener {

    companion object {
        val TITLE_HEIGHT = 44.dp                // px
        const val HEAD_COVER_ALPHA = 0.2f       // 透明度

        const val DES_MINE_LINE = 2             // 描述最小行
        const val DES_MAX_LINE = 1000           // 描述最大行
    }

    private var mRankType: Long = 0
    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsDesExpand = false

    override fun initVM(): TopListGameDetailViewModel {
        mRankType = intent.getLongExtra(TopListConstant.KEY_TOPLIST_ID, 0)
        return viewModels<TopListGameDetailViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initView() {
        // 初始化title
        initTitle()
        // 游戏榜单详情页背景是固定图
        mToplistDetailHeadBgIv.setImageResource(R.drawable.ic_toplist_detail_head_blur_bg)
        // 游戏榜单详情页没有分享
        mToplistDetailHeadShareIv.isGone = true
        // 列表
        mAdapter = createMultiTypeAdapter(mToplistGameDetailRv, LinearLayoutManager(this))
        initEvent()
    }

    /**
     * 初始化title
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

        // 背景渐变蒙层
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

                //滑动过程改变title背景颜色
                if (pos < 0) {
                    pos = 0
                } else if (pos > height) {
                    pos = height
                }
                val ratio = pos / height.toFloat()
                val alpha = (255 * ratio).toInt()
                val color = statusBarColor and 0x00ffffff or (alpha shl 24)
                // 标题栏背景
//                mToplistDetailTitleBgCl.setBackgroundColor(color)
                mToplistDetailTitleCl.setBackgroundColor(color)
                // 返回、分享Icon
                if (ratio < 0.5) {
                    mToplistDetailHeadBackIv.setImageResource(R.drawable.ic_back_light)
                    mToplistDetailHeadShareIv.setImageResource(R.drawable.ic_more_light)
                } else {
                    mToplistDetailHeadBackIv.setImageResource(R.drawable.icon_back)
                    mToplistDetailHeadShareIv.setImageResource(R.drawable.ic_ver_more)
                    mToplistDetailHeadBackIv.alpha = ratio
                    mToplistDetailHeadShareIv.alpha = ratio
                }
                // 标题文字也需要渐变
                mToplistDetailHeadTitleTv.alpha = ratio
            }
        })
    }

    private fun initEvent() {
        // 点击返回箭头
        mToplistDetailHeadBackCl.onClick {
            finish()
        }

        // 点击描述
        mToplistDetailHeadInfoIntroTv.onClick {
            it.maxLines = if(mIsDesExpand) DES_MINE_LINE else DES_MAX_LINE
            mIsDesExpand = !mIsDesExpand
        }

        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {
        // 游戏榜单详情: 一次加载全部、没有分页
        mViewModel?.getIndexGameTopList(mRankType)
    }

    override fun startObserve() {
        mViewModel?.mGameTopListUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.let { gameTopList ->
                    showGameTopList(gameTopList)
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
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mViewModel?.getIndexGameTopList(mRankType)
            }
        }
    }

    /**
     * 显示游戏榜单详情数据
     */
    private fun showGameTopList(gameTopList: GameTopList) {
        gameTopList.let {
            // 设置头部数据
            setHead(gameTopList)
            it.userList?.let { list ->
                mAdapter.notifyAdapterAdded(getBinderList(list))
            }
        }
    }

    /**
     * 设置头部数据
     */
    private fun setHead(gameTopList: GameTopList) {
        var toplistInfo = TopListInfo()
        toplistInfo.title = gameTopList.rankName ?: ""
        toplistInfo.description = gameTopList.rankDesc ?: ""
        toplistInfo.type = TopListConstant.TOPLIST_TYPE_GAME

        mBinding?.setVariable(BR.toplistInfo, toplistInfo)
        mToplistDetailHeadInfoCountTv.text = String.format("共有%d人入榜", gameTopList.totalCount ?: 0L)
    }

    /**
     * 上榜用户列表
     */
    private fun getBinderList(list: List<GameRankUser>): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListGameDetailItemBinder>()
        list.map{
            var binder = TopListGameDetailItemBinder(it)
            binderList.add(binder)
        }
        return binderList
    }

}