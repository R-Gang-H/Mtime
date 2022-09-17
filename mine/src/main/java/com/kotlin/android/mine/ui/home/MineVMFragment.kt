package com.kotlin.android.mine.ui.home

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.app.router.provider.message_center.UnReadMessageObserver
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.app.router.provider.qrcode.IQRcodeProvider
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.app.router.provider.ticket_order.ITicketOrderProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.app.router.provider.user.IAppUserProvider
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.KEY_TYPE
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mine.*
import com.kotlin.android.mine.bean.AccountStatisticsInfoViewBean
import com.kotlin.android.mine.bean.UserViewBean
import com.kotlin.android.mine.databinding.FragMineVmBinding
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.formatMsgCount
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.setUserAuthType
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.isLogin
import com.kotlin.android.router.ext.put
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.titlebar.message
import com.kotlin.android.widget.titlebar.scan
import com.kotlin.android.widget.titlebar.setting

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/10
 * 描述: 我的首页
 */
class MineVMFragment : BaseVMFragment<MineVMViewModel, FragMineVmBinding>(), LifecycleObserver {

    init {
        lifecycle.addObserver(this)  // 用于消息提醒
    }

    companion object {
        fun newInstance() = MineVMFragment()

        // 我的好友页 0 关注 1粉丝
        const val MY_FRIEND_PAGE_TYPE_FOLLOW = 0L
        const val MY_FRIEND_PAGE_TYPE_FANS = 1L

        const val ACTIVITY_PAGE_SIZE = 5L       // 活动列表每页数量
        const val UN_LOGIN_MOVIE_COUNT_TEXT_SIZE = 10F // 想看提示文字大小
        const val LOGIN_MOVIE_COUNT_TEXT_SIZE = 17F
        const val MOVIE_BUBBLE_DELAY = 200L
        const val UNREAD_MEDAL_TEXT_SIZE = 12F
        const val WRITE_CENTER_MAX_COUNT_SHOW_NUM = 100000L     // 创作中心数字显示规则
        const val WRITE_CENTER_MAX_COUNT_SHOW_STRING = "10万+"  // 创作中心超过10万显示内容
    }

    // Provider
    private val mMineProvider = getProvider(IMineProvider::class.java)
    private val mUserProvider = getProvider(IAppUserProvider::class.java)
    private val mCommunityPersonProvider: ICommunityPersonProvider? =
            getProvider(ICommunityPersonProvider::class.java)
    private val mIQRCodeProvider: IQRcodeProvider? = getProvider(IQRcodeProvider::class.java)
    private val mTicketOrderProvider: ITicketOrderProvider? =
            getProvider(ITicketOrderProvider::class.java)
    private val mIJsSDKProvider: IJsSDKProvider? = getProvider(IJsSDKProvider::class.java)
    private val mIUgcProvider = getProvider(IUgcProvider::class.java)
    private val mITabletProvider = getProvider(ITabletProvider::class.java)

    // UI
    private val mAuthRoleCornerRadius = 11F.dpF         // 认证角色
    private val mUserLevelCornerRadius = 20F.dpF        // 会员等级
    private val mAuthRoleMarginStart = 5.dp             // 认证角色
    private val mPersonHomeCornerRadius = 11F.dpF       // 个人主页
    private val mUnLoginFollowMarginTop = 5.dp          // 关注数MarginTop
    private val mLoginFollowMarginTop = 8.dp
    private val mUnLoginMovieTipViewMarginTop = 6.dp
    private val mWriteCenterCornerRadius = 8.dpF        // 创作中心
    private val mWriteCenterLevelIconSize = 19.dp       // 创作中心等级图标宽高
    private val mUnreadMedalsLeftRightPadding = 10.dp   // 未读勋章
    private val mUnreadMedalsDrawablePadding = 3.dp

    private var mActivityAdapter: MultiTypeAdapter? = null  // 活动列表Adapter

    override fun initVM() = viewModels<MineVMViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive()
                .transparentStatusBar(isFitsSystemWindows = false)
                .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.apply {
            // 标题
            initTitle()
            // 账户信息
            initAccount()
            // 创作中心
            initWriteCenter()
            // 活动
            mActivityAdapter = createMultiTypeAdapter(activityInc.activityRv)
        }
        // 更新未登录账户信息UI
        updateUnLoginAccountUI()
        // 更新未登录统计信息UI
        updateUnLoginStatisticUI()
        // 初始化事件
        initEvent()
    }

    /**
     * 初始化标题
     */
    private fun initTitle() {
        mBinding?.apply {
            // 标题: 跟随页面一起滑动，需要定义到xml里}
            titleBar.apply {
                marginTop = statusBarHeight
                scan(isReversed = false) {
                    // 扫一扫
                    mIQRCodeProvider?.startQrScanActivity()
                }
                setting {
                    // 设置页
                    mMineProvider?.startSetting(context as Activity)
                }
                message {
                    // 消息
                    getProvider(IMessageCenterProvider::class.java)
                            ?.startMessageCenterActivity(context as Activity)
                }
            }
            // 隐藏红点
            updateMessageCenterRedPoint(0L)
        }
    }

    /**
     * 初始化账户信息
     */
    private fun initAccount() {
        mBinding?.apply {
            // 顶部背景
            headBgView.setBackground(
                    colorRes = R.color.color_fbffea,
                    endColorRes = R.color.color_ffffff,
                    orientation = GradientDrawable.Orientation.TOP_BOTTOM,
            )
        }
        mBinding?.userInfoInc?.apply {
            // 认证角色
            authRoleTv.setBackground(
                    colorRes = R.color.color_36c096_alpha_10,
                    cornerRadius = mAuthRoleCornerRadius,
            )
            // "个人主页"背景
            personHomePageTv.background = getGradientDrawable(
                    color = getColor(R.color.color_0091ff_alpha_10),
                    cornerRadius = mPersonHomeCornerRadius,
                    direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM,
            )
        }
    }

    /**
     * 初始化创作中心
     */
    private fun initWriteCenter() {
        mBinding?.writeCenterInc?.apply {
            writeCenterHomeTv.setBackground(
                    colorRes = R.color.color_f3f5f9_alpha_60,
                    cornerRadius = mWriteCenterCornerRadius
            )
            writeCenterContentTv.setBackground(
                    colorRes = R.color.color_f3f5f9_alpha_60,
                    cornerRadius = mWriteCenterCornerRadius
            )
            writeCenterTaskTv.setBackground(
                    colorRes = R.color.color_f3f5f9_alpha_60,
                    cornerRadius = mWriteCenterCornerRadius
            )
        }
    }

    /**
     * 初始化事件
     */
    private fun initEvent() {
        // 用户模块
        mBinding?.userInfoInc?.apply {
            // 头像
            userHeadIv.onClick {
                // 个人资料页
                gotoProfile()
            }
            // 昵称
            nickNameTv.onClick {
                // 个人资料页
                gotoProfile()
            }
            // 等级
            levelTv.onClick {
                // 会员中心页
                gotoMemberCenter()
            }
            followTv.onClick {
                // 关注列表
                gotoFriendList(MY_FRIEND_PAGE_TYPE_FOLLOW)
            }
            fansTv.onClick {
                // 粉丝列表
                gotoFriendList(MY_FRIEND_PAGE_TYPE_FANS)
            }
            personHomePageTv.onClick {
                // 个人主页
                gotoPersonHomeActivity()
            }
        }

        // movie模块
        mBinding?.movieInc?.apply {
            wantSeeCl.onClick {
                // 想看页
                mMineProvider?.startWannaSeeActivity(
                        bundle = Bundle().put(KEY_TYPE, CommConstant.MINE_WANT_SEE),
                        activity = context as Activity
                )
            }
            hasSeenCl.onClick {
                // 看过页
                mMineProvider?.startWannaSeeActivity(
                        bundle = Bundle().put(KEY_TYPE, CommConstant.MINE_HAS_SEE),
                        activity = context as Activity
                )
            }
            collectionCl.onClick {
                // 收藏页
                mCommunityPersonProvider?.startPersonCollection()
            }
        }

        // order模块
        mBinding?.orderInc?.apply {
            orderCl.onClick {
                // 订单列表页
                mTicketOrderProvider?.startTicketOrderListActivity(context as Activity)
            }
            walletCl.onClick {
                // 钱包页
                mMineProvider?.startMyWalletActivity(context as Activity)
            }
        }

        // 创作中心模块
        mBinding?.writeCenterInc?.apply {
            // 创作中心
            writeCenterHomeTv.onClick {
                mMineProvider?.startActivityCreatorCenterActivity()
            }
            writeCenterContentTv.onClick {
                mMineProvider?.startMyContent(context as Activity)
            }
            writeCenterTaskTv.onClick {
                mMineProvider?.startActivityCreatorTaskActivity()
            }
        }

        // 服务模块
        mBinding?.serviceInc?.apply {
            // 展开收起
            serviceExpandTv.onClick {
                val visible = !serviceSecondLineGroup.isVisible
                serviceSecondLineGroup.visible(visible)
                serviceExpandTv.text = getString(
                        if (visible) R.string.mine_service_collapse else R.string.mine_service_expand
                )
                serviceExpandIv.setImageResource(
                        if (visible)
                            R.drawable.ic_mine_service_collapse
                        else
                            R.drawable.ic_mine_service_expand
                )
            }
            // 会员中心
            serviceMemberCenterTv.onClick {
                gotoMemberCenter()
            }
            // M豆抽奖
            serviceMBeanTv.onClick {
                afterLogin {
                    // 抽奖页面
                    mIJsSDKProvider?.startH5Activity(
                            BrowserEntity(
                                    title = getString(R.string.mine_member_draw_lottery),
                                    url = VIP_LUNCKY_DRAW,
                                    true
                            )
                    )
                }
            }
            // 身份认证
            serviceIdentifyTv.onClick {
                // 用户认证类型：null代表没有认证， 1"个人", 2"影评人", 3"电影人", 4"机构", -1“审核中”;
                // 已经进行了认证就不能在进入认证页面
                when (UserManager.instance.userAuthType) {
                    CommConstant.USER_AUTH_TYPE_REVIEW_AUDIT -> {
                        showToast(getString(R.string.mine_request_indentifity_ing))
                    }
                    CommConstant.USER_AUTH_TYPE_REVIEW_PERSON,
                    CommConstant.USER_AUTH_TYPE_MOVIE_PERSON,
                    CommConstant.USER_AUTH_TYPE_ORGANIZATION -> {
                        showToast(getString(R.string.mine_service_has_auth_type))
                    }
                    else -> {
                        // 身份认证页
                        mMineProvider?.startAuthenActivity(context as Activity)
                    }
                }
            }
            // 我的片单
            serviceTopListTv.onClick {
                mITabletProvider?.startFilmListMineActivity()
            }
            // 意见反馈
            serviceFeedbackTv.onClick {
                mIUgcProvider?.launchDetail(
                        VariateExt.feedbackPostId,
                        CommConstant.PRAISE_OBJ_TYPE_POST
                )
            }
            // 证照信息
            serviceLicenseTv.onClick {
                mMineProvider?.startLicenseActivity(mContext)
            }
            // 关于我们
            serviceAboutUsTv.onClick {
                mUserProvider?.startAboutActivity()
            }
        }

        // 活动模块
        mBinding?.activityInc?.apply {
            // 查看更多
            activityMoreTv.onClick {
                // 活动页
                mMineProvider?.startActivityListActivity()
            }
        }
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        requestData()
    }

    /**
     * 请求数据
     */
    private fun requestData() {
        if (isLogin()) {
            // 用户信息
            mViewModel?.getAccountDetail()
            // 用户统计信息
            mViewModel?.getMineStatisticInfo(mContext)
        } else {
            // 隐藏消息红点
            updateMessageCenterRedPoint(0L)
            // 更新未登录账户信息UI
            updateUnLoginAccountUI()
            // 更新未登录统计信息UI
            updateUnLoginStatisticUI()
            // 活动列表
            mViewModel?.getActivityList(mContext, ACTIVITY_PAGE_SIZE)
        }
    }

    override fun startObserve() {
        getAccountDetailObserve()
        getStatisticObserve()
        getActivityListObserve()
    }

    /**
     * 用户账户详情Observe
     */
    private fun getAccountDetailObserve() {
        mViewModel?.accountDetailState?.observe(this) {
            it?.apply {
                success?.apply {
                    UserManager.instance.update(this)
                    binders?.let { viewBean ->
                        // 更新登录账户信息UI
                        updateLoginAccountUI(viewBean)
                    }
                }
                error?.apply {

                }
                netError?.apply {

                }
            }
        }
    }

    /**
     * 用户统计信息Observe
     */
    private fun getStatisticObserve() {
        mViewModel?.statisticUIState?.observe(this) {
            it.apply {
                success?.apply {
                    binders?.let { viewBean ->
                        // 更新登录用户统计信息UI
                        updateLoginStatisticUI(viewBean)
                    }
                    // 更新活动模块
                    updateActivity(binders?.activities, this.myActivity?.activityCount.orZero())
                }
                error?.apply {

                }
                netError?.apply {

                }
            }
        }
    }

    /**
     * 活动列表Observe
     */
    private fun getActivityListObserve() {
        mViewModel?.activityUIState?.observe(this) {
            it.apply {
                success?.apply {
                    // 更新活动模块
                    updateActivity(binders, this.activityCount.orZero())
                }
                error?.apply {

                }
                netError?.apply {

                }
            }
        }
    }

    /**
     * 更新未登录账户信息UI
     */
    private fun updateUnLoginAccountUI() {
        // 用户信息
        mBinding?.userInfoInc?.apply {
            // 头像
            setUserHead("")
            // 设置用户昵称
            setNickName(getString(R.string.mine_login_sign))
            // 认证类型
            setAuth(CommConstant.USER_AUTH_TYPE_REVIEW_NULL)
            // 等级和角色
            levelRoleCl.visible(false)
        }
    }

    /**
     * 更新未登录统计信息UI
     */
    private fun updateUnLoginStatisticUI() {
        mBinding?.userInfoInc?.apply {
            // 设置关注和粉丝数量及样式
            setFollowFansCount(
                    marginTop = mUnLoginFollowMarginTop,
                    followCount = 0L,
                    fansCount = 0L
            )
        }
        // 电影
        mBinding?.movieInc?.apply {
            setMovieIncTextStyle(false, 0, wantSeeBubbleTv, wantSeeCountTv, wantSeeTv)
            setMovieIncTextStyle(false, 0, hasSeenBubbleTv, hasSeenCountTv, hasSeenTv)
            setMovieIncTextStyle(false, 0, null, collectionCountTv, collectionTv)
        }
        // 设置创作中心提示和等级图标
        setWriteCenterTipLevelIcon(
                login = false,
                creator = false,
                levelUrl = ""
        )
        // 设置创作中心数量
        setWriteCenterCount(
                contentCount = 0,
                browseCount = 0,
                unreadMedals = mutableListOf()
        )
    }

    /**
     * 更新登录账户信息UI
     */
    private fun updateLoginAccountUI(viewBean: UserViewBean) {
        mBinding?.userInfoInc?.apply {
            // 头像
            setUserHead(viewBean.headPic)
            // 昵称
            setNickName(viewBean.nickname)
            // 认证类型
            setAuth(viewBean.userAuthType)
            // 用户等级 认证角色
            val showLevel = (USER_LEVEL_RUMEN..USER_LEVEL_DIANTANG).contains(viewBean.userLevel)
            val showRole = viewBean.userAuthRole.isNotEmpty()
            levelRoleCl.visible(showLevel || showRole)
            if (levelRoleCl.isVisible) {
                setUserLevel(showLevel, viewBean.userLevel, viewBean.userLevelDesc)
                setAuthRole(showLevel, showRole, viewBean.userAuthRole)
            }
        }
    }

    /**
     * 更新登录用户统计信息UI
     */
    private fun updateLoginStatisticUI(viewBean: AccountStatisticsInfoViewBean) {
        mBinding?.userInfoInc?.apply {
            // 设置关注和粉丝数量及样式
            setFollowFansCount(
                    marginTop = mLoginFollowMarginTop,
                    followCount = viewBean.followCount,
                    fansCount = viewBean.fansCount
            )
        }
        // 电影
        mBinding?.movieInc?.apply {
            // 样式和数量显示
            setMovieIncTextStyle(
                    true,
                    viewBean.wantSeeCount,
                    wantSeeBubbleTv,
                    wantSeeCountTv,
                    wantSeeTv
            )
            setMovieIncTextStyle(
                    true,
                    viewBean.watchedCount,
                    hasSeenBubbleTv,
                    hasSeenCountTv,
                    hasSeenTv
            )
            setMovieIncTextStyle(
                    true,
                    viewBean.favoriteCount,
                    null,
                    collectionCountTv,
                    collectionTv
            )
            // 想看气泡
            if (viewBean.hotFilmWantSeeCount > 0) {
                wantSeeBubbleTv.text =
                        getString(
                                R.string.mine_want_see_hot_film_count,
                                formatMsgCount(viewBean.hotFilmWantSeeCount)
                        )
                wantSeeBubbleTv.postDelayed({
                    wantSeeBubbleTv.marginStart = wantSeeCl.measuredWidth / 2
                    wantSeeBubbleTv.visible()
                }, MOVIE_BUBBLE_DELAY)
            } else {
                wantSeeBubbleTv.gone()
            }
            // 看过气泡
            if (viewBean.ratingWaitCount > 0) {
                hasSeenBubbleTv.text =
                        getString(
                                R.string.mine_has_seen_rating_wait_count,
                                formatMsgCount(viewBean.ratingWaitCount)
                        )
                hasSeenBubbleTv.postDelayed({
                    hasSeenBubbleTv.marginStart = hasSeenCl.measuredWidth / 2
                    hasSeenBubbleTv.visible()
                }, MOVIE_BUBBLE_DELAY)
            } else {
                hasSeenBubbleTv.gone()
            }
        }
        // 设置创作中心提示和等级图标
        setWriteCenterTipLevelIcon(
                login = true,
                creator = viewBean.creator,
                levelUrl = viewBean.creatorAppLogoUrl
        )
        // 创作中心数量
        setWriteCenterCount(
                contentCount = viewBean.contentCount,
                browseCount = viewBean.sevenDayBrowseCount,
                unreadMedals = viewBean.unreadMedals
        )
    }

    /**
     * 设置用户头像
     */
    private fun setUserHead(headPic: String) {
        mBinding?.userInfoInc?.apply {
            if (headPic.isEmpty()) {
                userHeadIv.setImageResource(R.drawable.default_user_head)
            } else {
                userHeadIv.loadImage(
                        data = headPic,
                        width = 70.dp,
                        height = 70.dp,
                        defaultImgRes = R.drawable.default_user_head,
                        errorImgRes = R.drawable.default_user_head,
                )
            }
        }
    }

    /**
     * 设置用户昵称
     */
    private fun setNickName(nickName: String) {
        mBinding?.userInfoInc?.apply {
            nickNameTv.text = nickName
        }
    }

    /**
     * 设置用户认证类型
     */
    private fun setAuth(userAuthType: Long) {
        mBinding?.userInfoInc?.apply {
            userAuthTypeIv.setUserAuthType(userAuthType)
        }
    }

    /**
     * 设置用户级别
     */
    private fun setUserLevel(showLevel: Boolean, level: Long, levelDesc: String) {
        mBinding?.userInfoInc?.apply {
            levelTv.visible(showLevel)
            levelIv.visible(showLevel)
            if (showLevel) {
                var colorRes = R.color.color_2ab5e1
                var icon = R.mipmap.ic_user_vip_rumen_60
                when (level) {
                    USER_LEVEL_ZHONGJI -> {//中级
                        colorRes = R.color.color_36c096
                        icon = R.mipmap.ic_user_vip_zhongji_60
                    }
                    USER_LEVEL_GAOJI -> {//高级
                        colorRes = R.color.color_91d959
                        icon = R.mipmap.ic_user_vip_gaoji_60
                    }
                    USER_LEVEL_ZISHEN -> {//资深
                        colorRes = R.color.color_feb12a
                        icon = R.mipmap.ic_user_vip_zishen_60
                    }
                    USER_LEVEL_DIANTANG -> {//殿堂
                        colorRes = R.color.color_ff5a36
                        icon = R.mipmap.ic_user_vip_diantang_60
                    }
                    else -> {
                    }
                }
                levelTv.setBackground(
                        colorRes = colorRes,
                        cornerRadius = mUserLevelCornerRadius,
                )
                levelTv.text = levelDesc
                levelIv.setImageResource(icon)
            }
        }
    }

    /**
     * 设置认证角色
     */
    private fun setAuthRole(showLevel: Boolean, showRole: Boolean, authRole: String) {
        mBinding?.userInfoInc?.apply {
            authRoleTv.visible(showRole)
            if (showRole) {
                authRoleTv.marginStart = if (showLevel) mAuthRoleMarginStart else 0
                authRoleTv.text = authRole
            }
        }
    }

    /**
     * 设置关注和粉丝数量及样式
     */
    private fun setFollowFansCount(marginTop: Int, followCount: Long, fansCount: Long) {
        mBinding?.userInfoInc?.apply {
            followTv.marginTop = marginTop
            followTv.text = getString(R.string.mine_login_follow_count, formatCount(followCount))
            fansTv.text = getString(R.string.mine_login_fans_count, formatCount(fansCount))
        }
    }

    /**
     * 设置电影模块文本样式
     */
    private fun setMovieIncTextStyle(
            isLogin: Boolean,
            count: Long,
            bubbleView: AppCompatTextView?,
            countView: AppCompatTextView,
            tipView: AppCompatTextView,
    ) {
        // 气泡
        bubbleView?.let {
            if (isLogin.not()) {
                it.visible(false)
            }
        }
        // 数量
        countView.setTextColor(getColor(if (isLogin) R.color.color_1d2736 else R.color.color_bbbbbb))
        countView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                if (isLogin) LOGIN_MOVIE_COUNT_TEXT_SIZE else UN_LOGIN_MOVIE_COUNT_TEXT_SIZE
        )
        countView.typeface =
                Typeface.defaultFromStyle(if (isLogin) Typeface.BOLD else Typeface.NORMAL)
        countView.text = if (isLogin) {
            count.toString()
        } else {
            getString(R.string.mine_movie_count_tip)
        }
        // 提示文字
        tipView.marginTop = if (isLogin) 0 else mUnLoginMovieTipViewMarginTop
    }

    /**
     * 设置创作中心提示和等级图标
     */
    private fun setWriteCenterTipLevelIcon(login: Boolean, creator: Boolean, levelUrl: String) {
        mBinding?.writeCenterInc?.apply {
            // 登录状态/创作者提示
            writeCenterTipTv.visible(!login || !creator)
            if (writeCenterTipTv.isVisible) {
                writeCenterTipTv.text = getString(
                        if (!login)
                            R.string.mine_write_center_un_login
                        else
                            R.string.mine_write_center_not_creator
                )
                writeCenterLevelIv.gone()
            } else {
                // 等级图标
                if (levelUrl.isNotEmpty()) {
                    writeCenterLevelIv.loadImage(
                            data = levelUrl,
                            width = mWriteCenterLevelIconSize,
                            height = mWriteCenterLevelIconSize,
                            defaultImgRes = 0,  // 不显示默认图
                    )
                    writeCenterLevelIv.visible()
                } else {
                    writeCenterLevelIv.gone()
                }
            }
        }
    }

    /**
     * 设置创作中心统计和未读勋章
     */
    private fun setWriteCenterCount(
            contentCount: Long,
            browseCount: Long,
            unreadMedals: MutableList<AccountStatisticsInfoViewBean.UnreadMedalViewBean>
    ) {
        mBinding?.writeCenterInc?.apply {
            // 10万以内按通用规则显示，超过10w显示10万+
            writeCenterContentCountTv.text =
                    if (contentCount > WRITE_CENTER_MAX_COUNT_SHOW_NUM)
                        WRITE_CENTER_MAX_COUNT_SHOW_STRING
                    else
                        formatCount(contentCount)
            writeCenterBrowseCountTv.text =
                    if (browseCount > WRITE_CENTER_MAX_COUNT_SHOW_NUM)
                        WRITE_CENTER_MAX_COUNT_SHOW_STRING
                    else
                        formatCount(browseCount)
            writeCenterStatisticCl.onClick {
                // 创作中心-数据中心-全部
                mMineProvider?.startActivityDataCenterActivity()
            }
            // 停止轮播
            writeCenterFlipper.stopFlipping()
            // 移除未读勋章
            if (writeCenterFlipper.childCount > 1) {
                writeCenterFlipper.removeViews(1, writeCenterFlipper.childCount - 1)
            }
            // 添加未读勋章
            if (unreadMedals.isNotEmpty()) {
                // 添加未读勋章
                unreadMedals.map {
                    val tv = TextView(mContext).apply {
                        setPadding(mUnreadMedalsLeftRightPadding, 0,
                                mUnreadMedalsLeftRightPadding, 0)
                        setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_mine_write_center_message, 0, 0, 0
                        )
                        compoundDrawablePadding = mUnreadMedalsDrawablePadding
                        gravity = Gravity.CENTER_VERTICAL
                        maxLines = 1
                        ellipsize = TextUtils.TruncateAt.END
                        setTextColor(com.kotlin.android.mtime.ktx.getColor(R.color.color_feb12a))
                        textSize = UNREAD_MEDAL_TEXT_SIZE
                        text = getString(R.string.mine_write_center_unread_medal,
                                it.unreadCount,
                                it.medalName
                        )
                        onClick {
                            // 创作中心-我的勋章
                            mMineProvider?.startMyMedal(mActivity)
                        }
                    }
                    writeCenterFlipper.addView(tv)
                }
                writeCenterFlipper.startFlipping()
            }
        }
    }

    /**
     * 更新活动模块
     */
    private fun updateActivity(binders: MutableList<MultiTypeBinder<*>>?, totalCount: Long) {
        val visible = binders.isNullOrEmpty().not()
        mBinding?.activityInc?.apply {
            activityRootLayout.visible(visible)
            if (visible) {
                mActivityAdapter?.notifyAdapterDataSetChanged(binders, false)
                activityMoreTv.visible(totalCount > ACTIVITY_PAGE_SIZE)
            }
        }
    }

    private var unReadMessageObserver: UnReadMessageObserver? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unRegisterUnreadMessageCount() {
        unReadMessageObserver?.let {
            getProvider(IMessageCenterProvider::class.java)?.removeUnreadMessageCountObserver(it)
        }
    }

    /**
     * 添加未读消息数的监听
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun registerUnreadMessageCount() {
        if (unReadMessageObserver == null) {
            unReadMessageObserver = object : UnReadMessageObserver {
                override fun onNotifyMessageCount(totalCount: Long) {
                    // 更新消息中心红点
                    updateMessageCenterRedPoint(totalCount)
                }
            }
        }
        unReadMessageObserver?.let {
            getProvider(IMessageCenterProvider::class.java)?.addUnreadMessageCountObserver(it)
        }
    }

    /**
     * 更新消息中心红点
     */
    private fun updateMessageCenterRedPoint(unReadNum: Long) {
        val isShowRedPoint = unReadNum > 0L
        val totalNumStr = if (isShowRedPoint) formatMsgCount(unReadNum) else null
        mBinding?.titleBar?.updateRedPoint(
                isShow = isShowRedPoint,
                index = 0,
                isReversed = true,
                title = totalNumStr,
        )
    }

    /**
     * 跳转到会员中心页
     */
    private fun gotoMemberCenter() {
        mMineProvider?.startMemberCenterActivity(context as Activity)
    }

    /**
     * 跳转到个人资料页
     */
    private fun gotoProfile() {
        mMineProvider?.startPersonalData(context as Activity)
    }

    /**
     * 跳转到粉丝或关注列表
     * @param type  0 关注 1粉丝
     */
    private fun gotoFriendList(type: Long) {
        mCommunityPersonProvider?.startMyFriend(
                userId = UserManager.instance.userId,
                type = type,
                isPublish = false,
        )
    }

    /**
     * 跳转到用户主页
     */
    private fun gotoPersonHomeActivity() {
        afterLogin {
            mCommunityPersonProvider?.startPerson(userId = UserManager.instance.userId)
        }
    }

}