package com.kotlin.android.card.monopoly.ui.suit.detail

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.monopoly.LimitSuitUsers
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.SuitRankItemBinder
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ActSuitRankBinding
import com.kotlin.android.card.monopoly.databinding.ItemSuitRankBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.GridItemDecoration
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.act_suit_rank.*

/**
 * @desc 限量套装排行榜
 * @author zhangjian
 * @date 2020/9/11 09:08
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_SUIT_RANK)
class SuitRankingActivity : BaseVMActivity<CardMonopolyApiViewModel, ActSuitRankBinding>(),
    MultiStateView.MultiStateListener {

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private var suitID = 0L
    private var suitFlag = false //true:限量卡,false:普卡
    private lateinit var mAdapter: MultiTypeAdapter
    private var mListData: ArrayList<MultiTypeBinder<ItemSuitRankBinding>>? = ArrayList()

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        mMultiStateView.setMultiStateListener(this)
        initTitle()
        initContentView()
    }

    private fun initContentView() {
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        scrollContainer?.setBackground(
            colorRes = R.color.color_ffffff,
            cornerRadius = 6.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )

        suitID = (intent?.getLongExtra(Constants.PARAM_SUIT_ID, 0L) ?: 0L)
        suitFlag = (intent?.getBooleanExtra(Constants.PARAM_SUIT_TYPE, false)) ?: false

        if (rvRank.itemDecorationCount == 0) {
            rvRank.addItemDecoration(
                GridItemDecoration(
                    10.dp,
                    ContextCompat.getColor(this, R.color.color_ffffff)
                )
            )
        }
        mAdapter = createMultiTypeAdapter(rvRank, GridLayoutManager(this, 2))
        showProgressDialog()
        mViewModel?.loadSuitRankList(suitID)
    }

    private fun initTitle() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD_STATUS_BAR)
            setState(State.REVERSE)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(getString(R.string.suit_detail), alwaysShow = true) {

            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_more_light,
                reverseDrawableRes = R.drawable.ic_title_bar_more_dark,
                isReversed = true,
            ) {
                showFunctionMenuDialog(
                    dismiss = {
                        syncStatusBar()
                    }
                )
            }
        }
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel?.limitSuitUsersUiState?.observe(this) {
            dismissProgressDialog()
            it?.apply {
                success?.apply {
                    showData(this)
                }

                if (isEmpty) {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
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

    private fun showData(data: LimitSuitUsers) {
        //套装信息
        suitImageView?.data = Suit(suitCover = data.suitCover)
        //套装名称
        tvName.text = data.suitName
        //用户数
        if (suitFlag) {
            tvDesption.text =
                String.format(getString(R.string.have_suit_limit_all_count), data.userCount)
        } else {
            tvDesption.text =
                String.format(getString(R.string.have_suit_normal_all_count), data.userCount)
        }
        //TopOne
        data.maxinumUser?.apply {
            civTopOneAvatar.setUserInfo(this)
            tvTopOneCount.text = String.format(getString(R.string.have_suit_count), suitCount)
            tvTopOneName.text = nickName
        }
        ShapeExt.setShapeCorner2ColorWithColor(
            topOne,
            ContextCompat.getColor(this, R.color.color_f2f3f6),
            12,
            false
        )
        topOne.onClick {
            jumpMainPage(data.maxinumUser?.userId ?: 0L)
        }
        //TopTwo
        civTopTwoAvatar.setUserInfo(data.earliestUser)
        tvTopTwoName.text = data.earliestUser?.nickName
        ShapeExt.setShapeCorner2ColorWithColor(
            topTwo,
            ContextCompat.getColor(this, R.color.color_f2f3f6),
            12,
            false
        )
        topTwo.onClick {
            jumpMainPage(data.earliestUser?.userId ?: 0L)
        }
        //排行榜
        data.userList?.forEach {
            mListData?.add(SuitRankItemBinder(it, ::jumpMainPage))
        }
        mListData?.let { mAdapter.notifyAdapterAdded(it) }

    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mViewModel?.loadSuitRankList(suitID)
            }
        }
    }

    private fun jumpMainPage(userId: Long) {
        mProvider?.startCardMainActivity(this, userId = userId)
    }
}