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
 * ?????????: vivian.wei
 * ????????????: 2022/3/10
 * ??????: ????????????
 */
class MineVMFragment : BaseVMFragment<MineVMViewModel, FragMineVmBinding>(), LifecycleObserver {

    init {
        lifecycle.addObserver(this)  // ??????????????????
    }

    companion object {
        fun newInstance() = MineVMFragment()

        // ??????????????? 0 ?????? 1??????
        const val MY_FRIEND_PAGE_TYPE_FOLLOW = 0L
        const val MY_FRIEND_PAGE_TYPE_FANS = 1L

        const val ACTIVITY_PAGE_SIZE = 5L       // ????????????????????????
        const val UN_LOGIN_MOVIE_COUNT_TEXT_SIZE = 10F // ????????????????????????
        const val LOGIN_MOVIE_COUNT_TEXT_SIZE = 17F
        const val MOVIE_BUBBLE_DELAY = 200L
        const val UNREAD_MEDAL_TEXT_SIZE = 12F
        const val WRITE_CENTER_MAX_COUNT_SHOW_NUM = 100000L     // ??????????????????????????????
        const val WRITE_CENTER_MAX_COUNT_SHOW_STRING = "10???+"  // ??????????????????10???????????????
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
    private val mAuthRoleCornerRadius = 11F.dpF         // ????????????
    private val mUserLevelCornerRadius = 20F.dpF        // ????????????
    private val mAuthRoleMarginStart = 5.dp             // ????????????
    private val mPersonHomeCornerRadius = 11F.dpF       // ????????????
    private val mUnLoginFollowMarginTop = 5.dp          // ?????????MarginTop
    private val mLoginFollowMarginTop = 8.dp
    private val mUnLoginMovieTipViewMarginTop = 6.dp
    private val mWriteCenterCornerRadius = 8.dpF        // ????????????
    private val mWriteCenterLevelIconSize = 19.dp       // ??????????????????????????????
    private val mUnreadMedalsLeftRightPadding = 10.dp   // ????????????
    private val mUnreadMedalsDrawablePadding = 3.dp

    private var mActivityAdapter: MultiTypeAdapter? = null  // ????????????Adapter

    override fun initVM() = viewModels<MineVMViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive()
                .transparentStatusBar(isFitsSystemWindows = false)
                .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.apply {
            // ??????
            initTitle()
            // ????????????
            initAccount()
            // ????????????
            initWriteCenter()
            // ??????
            mActivityAdapter = createMultiTypeAdapter(activityInc.activityRv)
        }
        // ???????????????????????????UI
        updateUnLoginAccountUI()
        // ???????????????????????????UI
        updateUnLoginStatisticUI()
        // ???????????????
        initEvent()
    }

    /**
     * ???????????????
     */
    private fun initTitle() {
        mBinding?.apply {
            // ??????: ??????????????????????????????????????????xml???}
            titleBar.apply {
                marginTop = statusBarHeight
                scan(isReversed = false) {
                    // ?????????
                    mIQRCodeProvider?.startQrScanActivity()
                }
                setting {
                    // ?????????
                    mMineProvider?.startSetting(context as Activity)
                }
                message {
                    // ??????
                    getProvider(IMessageCenterProvider::class.java)
                            ?.startMessageCenterActivity(context as Activity)
                }
            }
            // ????????????
            updateMessageCenterRedPoint(0L)
        }
    }

    /**
     * ?????????????????????
     */
    private fun initAccount() {
        mBinding?.apply {
            // ????????????
            headBgView.setBackground(
                    colorRes = R.color.color_fbffea,
                    endColorRes = R.color.color_ffffff,
                    orientation = GradientDrawable.Orientation.TOP_BOTTOM,
            )
        }
        mBinding?.userInfoInc?.apply {
            // ????????????
            authRoleTv.setBackground(
                    colorRes = R.color.color_36c096_alpha_10,
                    cornerRadius = mAuthRoleCornerRadius,
            )
            // "????????????"??????
            personHomePageTv.background = getGradientDrawable(
                    color = getColor(R.color.color_0091ff_alpha_10),
                    cornerRadius = mPersonHomeCornerRadius,
                    direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM,
            )
        }
    }

    /**
     * ?????????????????????
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
     * ???????????????
     */
    private fun initEvent() {
        // ????????????
        mBinding?.userInfoInc?.apply {
            // ??????
            userHeadIv.onClick {
                // ???????????????
                gotoProfile()
            }
            // ??????
            nickNameTv.onClick {
                // ???????????????
                gotoProfile()
            }
            // ??????
            levelTv.onClick {
                // ???????????????
                gotoMemberCenter()
            }
            followTv.onClick {
                // ????????????
                gotoFriendList(MY_FRIEND_PAGE_TYPE_FOLLOW)
            }
            fansTv.onClick {
                // ????????????
                gotoFriendList(MY_FRIEND_PAGE_TYPE_FANS)
            }
            personHomePageTv.onClick {
                // ????????????
                gotoPersonHomeActivity()
            }
        }

        // movie??????
        mBinding?.movieInc?.apply {
            wantSeeCl.onClick {
                // ?????????
                mMineProvider?.startWannaSeeActivity(
                        bundle = Bundle().put(KEY_TYPE, CommConstant.MINE_WANT_SEE),
                        activity = context as Activity
                )
            }
            hasSeenCl.onClick {
                // ?????????
                mMineProvider?.startWannaSeeActivity(
                        bundle = Bundle().put(KEY_TYPE, CommConstant.MINE_HAS_SEE),
                        activity = context as Activity
                )
            }
            collectionCl.onClick {
                // ?????????
                mCommunityPersonProvider?.startPersonCollection()
            }
        }

        // order??????
        mBinding?.orderInc?.apply {
            orderCl.onClick {
                // ???????????????
                mTicketOrderProvider?.startTicketOrderListActivity(context as Activity)
            }
            walletCl.onClick {
                // ?????????
                mMineProvider?.startMyWalletActivity(context as Activity)
            }
        }

        // ??????????????????
        mBinding?.writeCenterInc?.apply {
            // ????????????
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

        // ????????????
        mBinding?.serviceInc?.apply {
            // ????????????
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
            // ????????????
            serviceMemberCenterTv.onClick {
                gotoMemberCenter()
            }
            // M?????????
            serviceMBeanTv.onClick {
                afterLogin {
                    // ????????????
                    mIJsSDKProvider?.startH5Activity(
                            BrowserEntity(
                                    title = getString(R.string.mine_member_draw_lottery),
                                    url = VIP_LUNCKY_DRAW,
                                    true
                            )
                    )
                }
            }
            // ????????????
            serviceIdentifyTv.onClick {
                // ?????????????????????null????????????????????? 1"??????", 2"?????????", 3"?????????", 4"??????", -1???????????????;
                // ???????????????????????????????????????????????????
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
                        // ???????????????
                        mMineProvider?.startAuthenActivity(context as Activity)
                    }
                }
            }
            // ????????????
            serviceTopListTv.onClick {
                mITabletProvider?.startFilmListMineActivity()
            }
            // ????????????
            serviceFeedbackTv.onClick {
                mIUgcProvider?.launchDetail(
                        VariateExt.feedbackPostId,
                        CommConstant.PRAISE_OBJ_TYPE_POST
                )
            }
            // ????????????
            serviceLicenseTv.onClick {
                mMineProvider?.startLicenseActivity(mContext)
            }
            // ????????????
            serviceAboutUsTv.onClick {
                mUserProvider?.startAboutActivity()
            }
        }

        // ????????????
        mBinding?.activityInc?.apply {
            // ????????????
            activityMoreTv.onClick {
                // ?????????
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
     * ????????????
     */
    private fun requestData() {
        if (isLogin()) {
            // ????????????
            mViewModel?.getAccountDetail()
            // ??????????????????
            mViewModel?.getMineStatisticInfo(mContext)
        } else {
            // ??????????????????
            updateMessageCenterRedPoint(0L)
            // ???????????????????????????UI
            updateUnLoginAccountUI()
            // ???????????????????????????UI
            updateUnLoginStatisticUI()
            // ????????????
            mViewModel?.getActivityList(mContext, ACTIVITY_PAGE_SIZE)
        }
    }

    override fun startObserve() {
        getAccountDetailObserve()
        getStatisticObserve()
        getActivityListObserve()
    }

    /**
     * ??????????????????Observe
     */
    private fun getAccountDetailObserve() {
        mViewModel?.accountDetailState?.observe(this) {
            it?.apply {
                success?.apply {
                    UserManager.instance.update(this)
                    binders?.let { viewBean ->
                        // ????????????????????????UI
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
     * ??????????????????Observe
     */
    private fun getStatisticObserve() {
        mViewModel?.statisticUIState?.observe(this) {
            it.apply {
                success?.apply {
                    binders?.let { viewBean ->
                        // ??????????????????????????????UI
                        updateLoginStatisticUI(viewBean)
                    }
                    // ??????????????????
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
     * ????????????Observe
     */
    private fun getActivityListObserve() {
        mViewModel?.activityUIState?.observe(this) {
            it.apply {
                success?.apply {
                    // ??????????????????
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
     * ???????????????????????????UI
     */
    private fun updateUnLoginAccountUI() {
        // ????????????
        mBinding?.userInfoInc?.apply {
            // ??????
            setUserHead("")
            // ??????????????????
            setNickName(getString(R.string.mine_login_sign))
            // ????????????
            setAuth(CommConstant.USER_AUTH_TYPE_REVIEW_NULL)
            // ???????????????
            levelRoleCl.visible(false)
        }
    }

    /**
     * ???????????????????????????UI
     */
    private fun updateUnLoginStatisticUI() {
        mBinding?.userInfoInc?.apply {
            // ????????????????????????????????????
            setFollowFansCount(
                    marginTop = mUnLoginFollowMarginTop,
                    followCount = 0L,
                    fansCount = 0L
            )
        }
        // ??????
        mBinding?.movieInc?.apply {
            setMovieIncTextStyle(false, 0, wantSeeBubbleTv, wantSeeCountTv, wantSeeTv)
            setMovieIncTextStyle(false, 0, hasSeenBubbleTv, hasSeenCountTv, hasSeenTv)
            setMovieIncTextStyle(false, 0, null, collectionCountTv, collectionTv)
        }
        // ???????????????????????????????????????
        setWriteCenterTipLevelIcon(
                login = false,
                creator = false,
                levelUrl = ""
        )
        // ????????????????????????
        setWriteCenterCount(
                contentCount = 0,
                browseCount = 0,
                unreadMedals = mutableListOf()
        )
    }

    /**
     * ????????????????????????UI
     */
    private fun updateLoginAccountUI(viewBean: UserViewBean) {
        mBinding?.userInfoInc?.apply {
            // ??????
            setUserHead(viewBean.headPic)
            // ??????
            setNickName(viewBean.nickname)
            // ????????????
            setAuth(viewBean.userAuthType)
            // ???????????? ????????????
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
     * ??????????????????????????????UI
     */
    private fun updateLoginStatisticUI(viewBean: AccountStatisticsInfoViewBean) {
        mBinding?.userInfoInc?.apply {
            // ????????????????????????????????????
            setFollowFansCount(
                    marginTop = mLoginFollowMarginTop,
                    followCount = viewBean.followCount,
                    fansCount = viewBean.fansCount
            )
        }
        // ??????
        mBinding?.movieInc?.apply {
            // ?????????????????????
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
            // ????????????
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
            // ????????????
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
        // ???????????????????????????????????????
        setWriteCenterTipLevelIcon(
                login = true,
                creator = viewBean.creator,
                levelUrl = viewBean.creatorAppLogoUrl
        )
        // ??????????????????
        setWriteCenterCount(
                contentCount = viewBean.contentCount,
                browseCount = viewBean.sevenDayBrowseCount,
                unreadMedals = viewBean.unreadMedals
        )
    }

    /**
     * ??????????????????
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
     * ??????????????????
     */
    private fun setNickName(nickName: String) {
        mBinding?.userInfoInc?.apply {
            nickNameTv.text = nickName
        }
    }

    /**
     * ????????????????????????
     */
    private fun setAuth(userAuthType: Long) {
        mBinding?.userInfoInc?.apply {
            userAuthTypeIv.setUserAuthType(userAuthType)
        }
    }

    /**
     * ??????????????????
     */
    private fun setUserLevel(showLevel: Boolean, level: Long, levelDesc: String) {
        mBinding?.userInfoInc?.apply {
            levelTv.visible(showLevel)
            levelIv.visible(showLevel)
            if (showLevel) {
                var colorRes = R.color.color_2ab5e1
                var icon = R.mipmap.ic_user_vip_rumen_60
                when (level) {
                    USER_LEVEL_ZHONGJI -> {//??????
                        colorRes = R.color.color_36c096
                        icon = R.mipmap.ic_user_vip_zhongji_60
                    }
                    USER_LEVEL_GAOJI -> {//??????
                        colorRes = R.color.color_91d959
                        icon = R.mipmap.ic_user_vip_gaoji_60
                    }
                    USER_LEVEL_ZISHEN -> {//??????
                        colorRes = R.color.color_feb12a
                        icon = R.mipmap.ic_user_vip_zishen_60
                    }
                    USER_LEVEL_DIANTANG -> {//??????
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
     * ??????????????????
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
     * ????????????????????????????????????
     */
    private fun setFollowFansCount(marginTop: Int, followCount: Long, fansCount: Long) {
        mBinding?.userInfoInc?.apply {
            followTv.marginTop = marginTop
            followTv.text = getString(R.string.mine_login_follow_count, formatCount(followCount))
            fansTv.text = getString(R.string.mine_login_fans_count, formatCount(fansCount))
        }
    }

    /**
     * ??????????????????????????????
     */
    private fun setMovieIncTextStyle(
            isLogin: Boolean,
            count: Long,
            bubbleView: AppCompatTextView?,
            countView: AppCompatTextView,
            tipView: AppCompatTextView,
    ) {
        // ??????
        bubbleView?.let {
            if (isLogin.not()) {
                it.visible(false)
            }
        }
        // ??????
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
        // ????????????
        tipView.marginTop = if (isLogin) 0 else mUnLoginMovieTipViewMarginTop
    }

    /**
     * ???????????????????????????????????????
     */
    private fun setWriteCenterTipLevelIcon(login: Boolean, creator: Boolean, levelUrl: String) {
        mBinding?.writeCenterInc?.apply {
            // ????????????/???????????????
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
                // ????????????
                if (levelUrl.isNotEmpty()) {
                    writeCenterLevelIv.loadImage(
                            data = levelUrl,
                            width = mWriteCenterLevelIconSize,
                            height = mWriteCenterLevelIconSize,
                            defaultImgRes = 0,  // ??????????????????
                    )
                    writeCenterLevelIv.visible()
                } else {
                    writeCenterLevelIv.gone()
                }
            }
        }
    }

    /**
     * ???????????????????????????????????????
     */
    private fun setWriteCenterCount(
            contentCount: Long,
            browseCount: Long,
            unreadMedals: MutableList<AccountStatisticsInfoViewBean.UnreadMedalViewBean>
    ) {
        mBinding?.writeCenterInc?.apply {
            // 10???????????????????????????????????????10w??????10???+
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
                // ????????????-????????????-??????
                mMineProvider?.startActivityDataCenterActivity()
            }
            // ????????????
            writeCenterFlipper.stopFlipping()
            // ??????????????????
            if (writeCenterFlipper.childCount > 1) {
                writeCenterFlipper.removeViews(1, writeCenterFlipper.childCount - 1)
            }
            // ??????????????????
            if (unreadMedals.isNotEmpty()) {
                // ??????????????????
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
                            // ????????????-????????????
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
     * ??????????????????
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
     * ??????????????????????????????
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun registerUnreadMessageCount() {
        if (unReadMessageObserver == null) {
            unReadMessageObserver = object : UnReadMessageObserver {
                override fun onNotifyMessageCount(totalCount: Long) {
                    // ????????????????????????
                    updateMessageCenterRedPoint(totalCount)
                }
            }
        }
        unReadMessageObserver?.let {
            getProvider(IMessageCenterProvider::class.java)?.addUnreadMessageCountObserver(it)
        }
    }

    /**
     * ????????????????????????
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
     * ????????????????????????
     */
    private fun gotoMemberCenter() {
        mMineProvider?.startMemberCenterActivity(context as Activity)
    }

    /**
     * ????????????????????????
     */
    private fun gotoProfile() {
        mMineProvider?.startPersonalData(context as Activity)
    }

    /**
     * ??????????????????????????????
     * @param type  0 ?????? 1??????
     */
    private fun gotoFriendList(type: Long) {
        mCommunityPersonProvider?.startMyFriend(
                userId = UserManager.instance.userId,
                type = type,
                isPublish = false,
        )
    }

    /**
     * ?????????????????????
     */
    private fun gotoPersonHomeActivity() {
        afterLogin {
            mCommunityPersonProvider?.startPerson(userId = UserManager.instance.userId)
        }
    }

}