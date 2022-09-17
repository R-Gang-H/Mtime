package com.kotlin.android.card.monopoly.ui.friend

import android.app.AlertDialog
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.card.monopoly.adapter.CardFriendItemBinder
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ActCardFriendBinding
import com.kotlin.android.card.monopoly.databinding.ItemCardFriendBinding
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ext.showPocketCardDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.AuctionFilterView
import com.kotlin.android.card.monopoly.widget.dialog.PocketCardDialog
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.monopoly.Friend
import com.kotlin.android.app.data.entity.monopoly.Friends
import com.kotlin.android.app.data.entity.monopoly.Robot
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.act_card_friend.*

/**
 * @desc 我的卡友页面
 * @author zhangjian
 * @date 2020/9/3 08:37
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_CARD_FRIEND)
class CardFriendActivity : BaseVMActivity<CardMonopolyApiViewModel, ActCardFriendBinding>(),
    OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    private var jumpType = Constants.CARD_FRIEND_CARD_MAIN
    private lateinit var mAdapter: MultiTypeAdapter
    private var mListData: ArrayList<MultiTypeBinder<ItemCardFriendBinding>>? = ArrayList()
    private var pageIndexs: Long = 1
    private var orderType: Long = 1
    private var firstLoadData: Boolean = true

    //搜索用户名
    private var searchName: String = ""

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        jumpType = intent.getIntExtra(KEY_CARD_FRIEND, Constants.CARD_FRIEND_CARD_MAIN)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        //设置标题
        setTitleView()
        //设置背景
        setBackgroundRound()
        //设置搜索
        setSearchView()
        //设置筛选
        setFilterView()
        //设置列表
        setCardFriendView()
    }

    private fun setSearchView() {
        searchFriendView.apply {
            searchAction = {
                searchName = it.keyword
                refreshData()
            }
            cancel = {
                searchName = ""
                refreshData()
            }
        }
    }

    private fun setBackgroundRound() {
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        rlBg?.setBackground(
            colorRes = android.R.color.white,
            cornerRadius = 6.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
    }

    private fun setFilterView() {
        friendFilters.setTextValue(
            getString(R.string.card_empty_seat),
            getString(R.string.type_gold),
            getString(R.string.type_suit)
        )
        friendFilters.onClickFilter = object : AuctionFilterView.OnClickFilterListener {
            override fun clickView(orderType: Int) {
                this@CardFriendActivity.orderType = orderType.toLong()
                refreshData()
            }

        }
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
        refreshData()
    }

    private fun loadFriends() {

        mViewModel?.friends(orderType, searchName, pageIndexs)
    }

    override fun startObserve() {
        mViewModel?.friendsUiState?.observe(this) {
            it?.apply {
                if (pageIndexs == 1L) {
                    showProgressDialog(showLoading)
                }

                success?.apply {
                    showData(this)
                }

                if (loadMore) {
                    if (pageIndexs == 1L) {
                        mRefreshLayout.resetNoMoreData()
                    }
                    pageIndexs++
                    mRefreshLayout?.finishLoadMore(true)
                }

                if (noMoreData) {
                    mRefreshLayout?.finishLoadMoreWithNoMoreData()
                }

                if (isEmpty) {
                    mRefreshLayout?.finishLoadMore(true)
                    if (pageIndexs == 1L) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }

                error?.apply {
                    mRefreshLayout?.finishLoadMore(false)
                    if (pageIndexs == 1L) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    mRefreshLayout?.finishLoadMore(false)
                    if (pageIndexs == 1L) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        }

        mViewModel?.deleteFriendUiState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    showToast(this.bizMessage)
                }
                error?.apply {
                    showToast(getString(R.string.common_request_fail_please_retry))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!firstLoadData) {
            refreshData()
        }
        firstLoadData = false
    }

    /**
     * 设置标题
     */
    private fun setTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD_STATUS_BAR)
            setState(State.REVERSE)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(getString(R.string.card_monopoly), alwaysShow = true) {

            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_more_light,
                reverseDrawableRes = R.drawable.ic_title_bar_more_dark,
                isReversed = true
            ) {
                showFunctionMenuDialog(
                    dismiss = {
                        syncStatusBar()
                    }
                )
            }
        }
    }


    /**
     * 设置卡友列表
     */
    private fun setCardFriendView() {
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.color.color_f2f3f6)?.let { divider.setDrawable(it) }
        if (rvCardFriend.itemDecorationCount == 0) {
            rvCardFriend.addItemDecoration(divider)
        }
        mAdapter = createMultiTypeAdapter(rvCardFriend, LinearLayoutManager(this))
    }

    /**
     * 设置个人信息
     */
    private fun updateUserView(data: UserInfo?) {
        userView?.apply {
            userInfo = data
            hideBubble()
        }
    }

    private fun showData(friends: Friends) {
        if (pageIndexs == 1L) {
            mAdapter.notifyAdapterClear()
            mListData?.clear()
        }
        //设置个人信息
        if (friends.userInfo != null) {
            updateUserView(friends.userInfo)
        }

        //设置列表数据
        val dataList = convertRobotToFriend(
            friends.robotList ?: listOf(),
            friends.friendList ?: listOf()
        )
        dataList.forEach { mList ->
            val binder = CardFriendItemBinder(
                mList,
                jumpType,
                ::getFriendDataCallBack,
                ::deleteFriendCallBack
            )
            mListData?.add(binder)
        }

        mAdapter.notifyAdapterAdded(mListData as List<MultiTypeBinder<*>>)
    }

    private fun getFriendDataCallBack(friendData: Friend) {
        val intent = Intent()
        intent.putExtra(KEY_FRIENDS, friendData)
        when (jumpType) {
            Constants.CARD_FRIEND_CARD_DISCARD -> {
                setResult(FRIEND_RESULT_CODE, intent)
                finish()
            }
            Constants.CARD_FRIEND_TOOLS_SLAVE -> {
                setResult(FRIEND_RESULT_CODE_100, intent)
                finish()
            }
            Constants.CARD_FRIEND_TOOLS_ROB -> {
                if (!friendData.isRobot) {
                    showPocketCardDialog(
                        data = PocketCardDialog.Data(friendId = friendData.userId),
                        style = PocketCardDialog.Style.TA_CARD,
                        event = {
                            intent.putExtra(KEY_CARD_MONOPOLY_CARD_ID, it?.firstOrNull())
                            setResult(FRIEND_RESULT_CODE_101, intent)
                            finish()
                        }
                    )
                }

            }
            Constants.CARD_FRIEND_TOOLS_ROB_LIMIT -> {
                if (!friendData.isRobot) {
                    showPocketCardDialog(
                        data = PocketCardDialog.Data(friendId = friendData.userId),
                        style = PocketCardDialog.Style.TA_LIMIT_CARD,
                        event = {
                            intent.putExtra(KEY_CARD_MONOPOLY_CARD_ID, it?.firstOrNull())
                            setResult(FRIEND_RESULT_CODE_101, intent)
                            finish()
                        }
                    )
                }

            }
            Constants.CARD_FRIEND_TOOLS_HACK -> {
                setResult(FRIEND_RESULT_CODE_102, intent)
                finish()
            }
        }

    }

    private fun deleteFriendCallBack(friendId: Long, position: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            title = getString(R.string.str_delete_title)
            setMessage(getString(R.string.str_delete_content))
            setPositiveButton(getString(R.string.ok)) { p0, p1 ->
                mViewModel?.deleteFriend(friendId)
                mListData?.get(position)?.let { mAdapter.notifyAdapterRemoved(it) }
            }
            setNegativeButton(getString(R.string.cancel)) { p0, p1 ->
                p0.dismiss()
            }
        }
        dialog.show()
    }


    /**
     * 将返回的机器人信息转成朋友信息展示
     */
    private fun convertRobotToFriend(
        robotList: List<Robot>,
        friendList: List<Friend>
    ): ArrayList<Friend> {
        val tempFriendList: ArrayList<Friend> = ArrayList()
        if (searchName.isEmpty()) {
            if (robotList.isNotEmpty()) {
                robotList.forEach { robot ->
                    val data = Friend()
                    data.isRobot = true
                    data.openPocketCount = robot.openPocketCount
                    data.openPocketRemainCount = robot.openPocketRemainCount
                    data.nickName = robot.robotName
                    data.userId = robot.robotId
                    tempFriendList.add(data)
                }
            }
        }

        if (friendList.isNotEmpty()) {
            friendList.forEach { friend ->
                tempFriendList.add(friend)
            }
        }
        return tempFriendList

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadFriends()
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mRefreshLayout.setNoMoreData(false)
                pageIndexs = 1L
                loadFriends()
            }
        }
    }

    private fun refreshData() {
        pageIndexs = 1L
        mListData?.clear()
        mAdapter.notifyAdapterClear()
        mRefreshLayout.setEnableLoadMore(true)
        mRefreshLayout.setNoMoreData(false)
        loadFriends()
    }

}