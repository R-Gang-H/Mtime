package com.kotlin.android.mine.ui.home

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.MINE_HAS_SEE
import com.kotlin.android.app.data.constant.CommConstant.MINE_WANT_SEE
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.KEY_TYPE
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mine.BR
import com.kotlin.android.mine.R
import com.kotlin.android.mine.VIP_LUNCKY_DRAW
import com.kotlin.android.mine.databinding.FragMineBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.router.ext.put

import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.app.router.provider.qrcode.IQRcodeProvider
import com.kotlin.android.app.router.provider.ticket_order.ITicketOrderProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.app.router.provider.user.IAppUserProvider
import com.kotlin.android.app.router.provider.youzan.IYouZanProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import kotlinx.android.synthetic.main.frag_mine.*

/**
 * 创建者: zl
 * 创建时间: 2020/7/24 2:35 PM
 * 描述:我的-主界面
 */
class MineFragment : BaseVMFragment<MineViewModel, FragMineBinding>() {

    companion object {
        fun newInstance() = MineFragment()
    }

    private val userProvider =
        getProvider(IAppUserProvider::class.java)
    private val mineProvider =
        getProvider(IMineProvider::class.java)
    private val youzanProvider =
        getProvider(IYouZanProvider::class.java)

    private var localUserPic: String? = ""//本地记录一份用户头像，避免每次调接口后都要重新加载一次

    override fun initVM(): MineViewModel {
        return viewModels<MineViewModel>().value
    }

    override fun initView() {

//        申请认证
        ShapeExt.setShapeCorner2Color(
            identificationTv,
            R.color.color_8798af,
            40,
            isNormalPadding = false
        )
//        抽奖签到按钮
        ShapeExt.setShapeCorner2Color2Stroke(
            singInTv,
            corner = 22,
            strokeColor = R.color.color_1d2736,
            strokeWidth = 1,
            isNormalPadding = false
        )
//        未读消息
        ShapeExt.setShapeCorner2Color(messageNumTv, R.color.color_ff5a36, 8)
        //设置商城样式
        setShopStyle()
        initListener()
    }

    private fun setShopStyle() {
        //进入商城按钮设置样式
        ShapeExt.setShapeCorner2Color2Stroke(
            shopEntryTv,
            R.color.color_20a0da,
            22,
            R.color.color_20a0da,
            1,
            false
        )
        //商城title设置样式
        val str = SpannableString(getString(R.string.mine_shop_title))
        str.setSpan(
            ForegroundColorSpan(getColor(R.color.color_19b3c2)),
            2,
            5,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        shopTitleTv.text = str
    }

    /**
     * 跳转到个人资料页 编辑页面
     */
    private fun gotoProfile() {
        afterLogin {//避免未登录的时候，进行登录后直接跳转到目标页
            userProvider?.startProfileActivity(context as Activity)
        }
    }

    private fun initListener() {
//        点击头像
        userHeadIv?.onClick {
//                跳转到编辑页面
            gotoProfile()
        }
        nickNameTv?.onClick {//用户名
//                跳转到编辑页
            gotoProfile()
        }
        vipIV?.onClick {//身份标志

        }

        fansTv?.onClick {//粉丝
//                粉丝列表
            gotoFriendList(1L)
        }

        attentionTv?.onClick {//关注
//                关注列表
            gotoFriendList(0L)
        }
        identificationTv?.onClick {//申请认证
//            用户认证类型：null代表没有认证， 1"个人", 2"影评人", 3"电影人", 4"机构", -1“审核中”;
            if (UserManager.instance.userAuthType in arrayOf(-1L, 2L, 3L, 4L)) {
//                已经进行了认证就不能在进入认证页面
                return@onClick
            }
//                身份认证页面
            mineProvider?.startAuthenActivity(context as Activity)
        }

        signatureTv?.onClick {//个性签名
//                跳转编辑页面
            gotoProfile()
        }
        personHomePageTv?.onClick {//个人中心
            gotoPersonHomeActivity()
        }
        personHomePageIv?.onClick {//个人中心
            gotoPersonHomeActivity()
        }

        scanIv?.onClick {//扫一扫
//                扫一扫页面
            getProvider(IQRcodeProvider::class.java)
                ?.startQrScanActivity()
        }



        messageIv?.onClick {//消息
            getProvider(IMessageCenterProvider::class.java)
                ?.startMessageCenterActivity(context as Activity)
        }


        wannaCL?.onClick {//想看
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_TYPE, MINE_WANT_SEE),
                context as Activity
            )
        }

        hasSeenCL?.onClick {//看过
//                看过页面
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_TYPE, MINE_HAS_SEE),
                context as Activity
            )
        }

        orderCL?.onClick {//订单
//                订单列表页
            val ticketOrderProvider: ITicketOrderProvider? =
                getProvider(ITicketOrderProvider::class.java)
            ticketOrderProvider?.startTicketOrderListActivity(context as Activity)
        }

        walletCL?.onClick {//钱包
//                钱包页面
            mineProvider?.startMyWalletActivity(context as Activity)
        }

        fansClubCL?.onClick {//影迷俱乐部
//                会员主页
            mineProvider?.startMemberCenterActivity(context as Activity)
        }

        singInTv?.onClick {//签到抽奖
            afterLogin {
                //                抽奖页面
                mViewModel?.mineClubState?.value?.apply {
                    (context as? Activity)?.apply {
                        getProvider(IJsSDKProvider::class.java)?.startH5Activity(
                            BrowserEntity(
                                title = getString(R.string.mine_member_draw_lottery),
                                url = VIP_LUNCKY_DRAW,
                                true
                            )
                        )
                    }
                }
            }
        }

        myPublishTv?.onClick {//我的发布
//                用户主页
            gotoPersonHomeActivity()

        }

        shopCL?.onClick {
            //商城跳转
            youzanProvider?.startYouZanWebView("https://h5.youzan.com/v2/showcase/homepage?alias=lUWblj8NNI")
//            val intent= Intent(activity,YouzanActivity::class.java)
//            startActivity(intent)
        }

        myCollectionTv?.onClick {//收藏
//                收藏页面
            mineProvider?.startMyCollection(context as Activity)
        }

        settingTv?.onClick {
//            设置页面
            userProvider?.startSettingActivity()
            // TODO: 2020/12/10 调试jssdk用，待联调完成后删掉
//            getProvider(IJsSDKProvider::class.java)
//                    ?.startH5Activity(BrowserEntity(
//                            title = "test《原创内容声明》",
//                            url = "http://general.mtime.cn/mobile/2019/19review/test.html"
//                    ))
        }

//        blackNameTv?.onClick {//黑名单
////                黑名单列表12月20期不做
//        }
//
//        privacyTv?.onClick {//隐私
////                隐私设置页12月20期不做
//        }
//
//        customerServiceTv?.onClick {//在线客服
////           在线客服
//        }

        feedBackTv?.onClick {//意见反馈
//            意见反馈
            val instance =
                getProvider(IUgcProvider::class.java)
            instance?.launchDetail(VariateExt.feedbackPostId, CommConstant.PRAISE_OBJ_TYPE_POST)
        }

        // 证照信息
        licenseTv.onClick {
            mineProvider?.startLicenseActivity(mContext)
        }

        aboutUsTv?.onClick {//关于我们
//            关于我们
            userProvider?.startAboutActivity()
        }

    }

    private fun gotoPersonHomeActivity() {
        afterLogin {
            //                跳转到用户主页
            val communityPersonProvider: ICommunityPersonProvider? =
                getProvider(ICommunityPersonProvider::class.java)
            communityPersonProvider?.startPerson(userId = UserManager.instance.userId)
        }
    }

    /**
     * 跳转到粉丝或关注列表
     * @param type  0 关注 1粉丝
     */
    private fun gotoFriendList(type: Long) {
        afterLogin {
            val communityPersonProvider: ICommunityPersonProvider? =
                getProvider(ICommunityPersonProvider::class.java)
            communityPersonProvider?.startMyFriend(UserManager.instance.userId, type, false, null)
        }
    }

    override fun initData() {
        mBinding?.setVariable(BR.viewModel, mViewModel)
        mViewModel?.initData()
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.setLoginState(isLogin())
        mViewModel?.getAccountDetail()
        mViewModel?.getMineStatisticInfo()

    }

    override fun startObserve() {
        mViewModel?.mineUserState?.observe(this) {
            bgIV?.postDelayed({
                if (it.userHeadPic.orEmpty() != localUserPic.orEmpty()) {
                    bgIV?.loadImage(
                        data = it.userHeadPic,
                        width = bgIV.measuredWidth,
                        height = bgIV.measuredHeight
                    )
                    ShapeExt.setForegroundGradientColorWithColor(
                        bgIV,
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        getColor(R.color.color_ccffffff),
                        getColor(R.color.color_ffffff),
                        0
                    )
                    localUserPic = it.userHeadPic
                }
            }, 50)
        }
        mViewModel?.mineHasSeenState?.observe(this) {
            val num = it?.evaluateMovieNum ?: 0
            val content = getString(R.string.mine_comment_movie_format, num)
            val spannableString = SpannableString(content)
            spannableString.setSpan(
                ForegroundColorSpan(getColor(R.color.color_ff5a36)),
                0,
                num.toString().length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            hasSeenCommentTv?.text = spannableString
        }

        mViewModel?.mineClubState?.observe(this) {
            val marginLayoutParams = mBeanTv?.layoutParams as? ViewGroup.MarginLayoutParams
            marginLayoutParams?.topMargin = if (isLogin()) 0 else 10.dp
            mBeanTv?.layoutParams = marginLayoutParams
            if (isLogin()) {
                var mAddNum = if (it?.mAddNum ?: 0 > 0L) it.mAddNum else 0L
                val formatCount = formatCount(mAddNum)
                val addMBean = getString(R.string.mine_m_bean_add)//今日增加m豆文案
                val content =
                    getString(R.string.mine_m_bean_add_format).format(addMBean, formatCount)
                val startIndex = addMBean.length
                SpannableString(content).apply {
                    setSpan(
                        ForegroundColorSpan(getColor(R.color.color_19b3c2)),
                        startIndex,
                        startIndex + formatCount.length,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    setSpan(
                        AbsoluteSizeSpan(27, true),
                        startIndex,
                        startIndex + formatCount.length,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    setSpan(
                        StyleSpan(Typeface.BOLD_ITALIC),
                        startIndex,
                        startIndex + formatCount.length,
                        SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }.also {
                    mBeanTv?.text = it
                }


            } else {
                mBeanTv?.text = getString(R.string.mine_scan_after_login)
            }
        }

//        用户统计信息
        mViewModel?.statisticState?.observe(this) {
            it?.apply {
                success?.apply {
                    mViewModel?.updateMineData(this)
                }
            }
        }

//        用户账户详情
        mViewModel?.accountDetailState?.observe(this) {
            it?.apply {
                success?.apply {
                    UserManager.instance.update(this)
                    mViewModel?.updateUserAccountDetail()
                }
            }
        }
//        未读消息数量
        mViewModel?.unReadMessageState?.observe(this) {
            it?.apply {
                success?.apply {
                    val totalCount =
                        unreadBroadcastCount + unreadNotificationCount + unreadPraiseCount + unreadReviewCount
                    messageNumTv?.visible(totalCount > 0L)
                    setUnReadMessage(totalCount)
                }
            }
        }

    }

    private fun setUnReadMessage(unReadNum: Long) {
        val totalNumStr = if (unReadNum >= 100L) "99+" else unReadNum.toString()
        if (unReadNum >= 100L) {
            messageNumTv?.setPadding(7.5f.dp, 0, 7.5f.dp, 0)
        } else {
            messageNumTv?.setPadding(0, 0, 0, 0)
        }
        messageNumTv?.text = totalNumStr
    }

    override fun destroyView() {
    }


}