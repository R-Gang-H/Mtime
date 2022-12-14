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
 * ?????????: zl
 * ????????????: 2020/7/24 2:35 PM
 * ??????:??????-?????????
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

    private var localUserPic: String? = ""//?????????????????????????????????????????????????????????????????????????????????

    override fun initVM(): MineViewModel {
        return viewModels<MineViewModel>().value
    }

    override fun initView() {

//        ????????????
        ShapeExt.setShapeCorner2Color(
            identificationTv,
            R.color.color_8798af,
            40,
            isNormalPadding = false
        )
//        ??????????????????
        ShapeExt.setShapeCorner2Color2Stroke(
            singInTv,
            corner = 22,
            strokeColor = R.color.color_1d2736,
            strokeWidth = 1,
            isNormalPadding = false
        )
//        ????????????
        ShapeExt.setShapeCorner2Color(messageNumTv, R.color.color_ff5a36, 8)
        //??????????????????
        setShopStyle()
        initListener()
    }

    private fun setShopStyle() {
        //??????????????????????????????
        ShapeExt.setShapeCorner2Color2Stroke(
            shopEntryTv,
            R.color.color_20a0da,
            22,
            R.color.color_20a0da,
            1,
            false
        )
        //??????title????????????
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
     * ???????????????????????? ????????????
     */
    private fun gotoProfile() {
        afterLogin {//??????????????????????????????????????????????????????????????????
            userProvider?.startProfileActivity(context as Activity)
        }
    }

    private fun initListener() {
//        ????????????
        userHeadIv?.onClick {
//                ?????????????????????
            gotoProfile()
        }
        nickNameTv?.onClick {//?????????
//                ??????????????????
            gotoProfile()
        }
        vipIV?.onClick {//????????????

        }

        fansTv?.onClick {//??????
//                ????????????
            gotoFriendList(1L)
        }

        attentionTv?.onClick {//??????
//                ????????????
            gotoFriendList(0L)
        }
        identificationTv?.onClick {//????????????
//            ?????????????????????null????????????????????? 1"??????", 2"?????????", 3"?????????", 4"??????", -1???????????????;
            if (UserManager.instance.userAuthType in arrayOf(-1L, 2L, 3L, 4L)) {
//                ???????????????????????????????????????????????????
                return@onClick
            }
//                ??????????????????
            mineProvider?.startAuthenActivity(context as Activity)
        }

        signatureTv?.onClick {//????????????
//                ??????????????????
            gotoProfile()
        }
        personHomePageTv?.onClick {//????????????
            gotoPersonHomeActivity()
        }
        personHomePageIv?.onClick {//????????????
            gotoPersonHomeActivity()
        }

        scanIv?.onClick {//?????????
//                ???????????????
            getProvider(IQRcodeProvider::class.java)
                ?.startQrScanActivity()
        }



        messageIv?.onClick {//??????
            getProvider(IMessageCenterProvider::class.java)
                ?.startMessageCenterActivity(context as Activity)
        }


        wannaCL?.onClick {//??????
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_TYPE, MINE_WANT_SEE),
                context as Activity
            )
        }

        hasSeenCL?.onClick {//??????
//                ????????????
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_TYPE, MINE_HAS_SEE),
                context as Activity
            )
        }

        orderCL?.onClick {//??????
//                ???????????????
            val ticketOrderProvider: ITicketOrderProvider? =
                getProvider(ITicketOrderProvider::class.java)
            ticketOrderProvider?.startTicketOrderListActivity(context as Activity)
        }

        walletCL?.onClick {//??????
//                ????????????
            mineProvider?.startMyWalletActivity(context as Activity)
        }

        fansClubCL?.onClick {//???????????????
//                ????????????
            mineProvider?.startMemberCenterActivity(context as Activity)
        }

        singInTv?.onClick {//????????????
            afterLogin {
                //                ????????????
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

        myPublishTv?.onClick {//????????????
//                ????????????
            gotoPersonHomeActivity()

        }

        shopCL?.onClick {
            //????????????
            youzanProvider?.startYouZanWebView("https://h5.youzan.com/v2/showcase/homepage?alias=lUWblj8NNI")
//            val intent= Intent(activity,YouzanActivity::class.java)
//            startActivity(intent)
        }

        myCollectionTv?.onClick {//??????
//                ????????????
            mineProvider?.startMyCollection(context as Activity)
        }

        settingTv?.onClick {
//            ????????????
            userProvider?.startSettingActivity()
            // TODO: 2020/12/10 ??????jssdk??????????????????????????????
//            getProvider(IJsSDKProvider::class.java)
//                    ?.startH5Activity(BrowserEntity(
//                            title = "test????????????????????????",
//                            url = "http://general.mtime.cn/mobile/2019/19review/test.html"
//                    ))
        }

//        blackNameTv?.onClick {//?????????
////                ???????????????12???20?????????
//        }
//
//        privacyTv?.onClick {//??????
////                ???????????????12???20?????????
//        }
//
//        customerServiceTv?.onClick {//????????????
////           ????????????
//        }

        feedBackTv?.onClick {//????????????
//            ????????????
            val instance =
                getProvider(IUgcProvider::class.java)
            instance?.launchDetail(VariateExt.feedbackPostId, CommConstant.PRAISE_OBJ_TYPE_POST)
        }

        // ????????????
        licenseTv.onClick {
            mineProvider?.startLicenseActivity(mContext)
        }

        aboutUsTv?.onClick {//????????????
//            ????????????
            userProvider?.startAboutActivity()
        }

    }

    private fun gotoPersonHomeActivity() {
        afterLogin {
            //                ?????????????????????
            val communityPersonProvider: ICommunityPersonProvider? =
                getProvider(ICommunityPersonProvider::class.java)
            communityPersonProvider?.startPerson(userId = UserManager.instance.userId)
        }
    }

    /**
     * ??????????????????????????????
     * @param type  0 ?????? 1??????
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
                val addMBean = getString(R.string.mine_m_bean_add)//????????????m?????????
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

//        ??????????????????
        mViewModel?.statisticState?.observe(this) {
            it?.apply {
                success?.apply {
                    mViewModel?.updateMineData(this)
                }
            }
        }

//        ??????????????????
        mViewModel?.accountDetailState?.observe(this) {
            it?.apply {
                success?.apply {
                    UserManager.instance.update(this)
                    mViewModel?.updateUserAccountDetail()
                }
            }
        }
//        ??????????????????
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