package com.kotlin.android.community.ui.person.center

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_JOURNAL
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.MINE_HAS_SEE
import com.kotlin.android.app.data.constant.CommConstant.MINE_WANT_SEE
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.user.IAppUserProvider
import com.kotlin.android.community.BR
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityPersonBinding
import com.kotlin.android.community.ui.person.*
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel.Companion.ACTION_CANCEL
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel.Companion.ACTION_POSITIVE
import com.kotlin.android.community.ui.person.center.content.article.CommunityCenterContentFragment
import com.kotlin.android.community.ui.person.center.family.CommunityPersonFamilyFragment
import com.kotlin.android.community.ui.person.center.filmlist.CommunityCenterFilmListFragment
import com.kotlin.android.community.ui.person.center.photo.CommunityPersonPhotoFragment
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.*
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.put
import com.kotlin.android.ugc.detail.component.binderadapter.setAttendBg
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import org.jetbrains.anko.toast


/**
 * 社区个人主页
 * @author WangWei
 * @data 2020/9/8
 */
@Route(path = RouterActivityPath.Community.PAGER_PERSON)
class CommunityPersonActivity :
    BaseVMActivity<CommunityPersonViewModel, FragCommunityPersonBinding>() {

    var userId: Long = 0L
    var index: Long = 0L
    private val provider = getProvider(ICommunityPersonProvider::class.java)
    private val mProvider = getProvider(ICardMonopolyProvider::class.java)
    private val userProvider = getProvider(IAppUserProvider::class.java)
    private var mMainProvider = getProvider(IMainProvider::class.java)
    private val mineProvider = getProvider(IMineProvider::class.java)

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(true)
    }


    override fun initVariable() {
        super.initVariable()
        userId = intent.getLongExtra(KEY_USER_ID, 0)
        index = intent.getLongExtra(KEY_TYPE, 0)
    }

    override fun initView() {
        var record = 0f
        var firstTouch = true
        mBinding?.personAppBarLayout?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (appBarLayout != null) {
                var result = Math.abs(verticalOffset * 1.0f)
                if (result - record > 0 && (verticalOffset * 1.0f) / appBarLayout.totalScrollRange == -1.0f) {
                    mBinding?.title?.ivToolbarPic?.visible()
                    mBinding?.title?.tvToolbarName?.visible()
                    mBinding?.title?.timeZone?.invisible()
                    mBinding?.title?.mTitleBackIv?.setImageResource(R.drawable.ic_title_bar_36_back)

                } else if ((result - record) < 0) {
                    mBinding?.title?.ivToolbarPic?.invisible()
                    mBinding?.title?.tvToolbarName?.invisible()
                    mBinding?.title?.timeZone?.visible()
                    mBinding?.title?.mTitleBackIv?.setImageResource(R.drawable.ic_title_bar_36_back_reversed)

                }
                record = result
            }
        })
        mBinding?.header?.tvAttend?.setBackground(
            cornerRadius = 20f.dpF,
            strokeColorRes = R.color.color_4e5e73,
            strokeWidth = 1.dp
        )
        mBinding?.title?.timeZone?.setBackground(
            colorRes = R.color.color_1A040404,
            cornerRadius = 15f.dpF,
            direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM
        )
        mBinding?.header?.tvEdit?.setBackground(
            colorRes = R.color.color_1A070606,
            cornerRadius = 15f.dpF,
            direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM
        )
        mBinding?.header?.tvIm?.setBackground(
            colorRes = R.color.color_f2f3f6,
            cornerRadius = 15f.dpF
        )

        mBinding?.header?.content?.setBackground(
            colorRes = R.color.color_ffffff,
            cornerRadius = 15.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
        clickOperate()
    }

    private fun clickOperate() {
        mBinding?.header?.ivBigBoss?.onClick {
            mProvider?.startCardMainActivity(
                context = this,
                userId = userId
            )
        }
        mBinding?.header?.llAttend?.onClick {
            provider?.startMyFriend(userId, 0L)
        }
        mBinding?.header?.llFan?.onClick {
            provider?.startMyFriend(userId, 1L)
        }
        mBinding?.header?.tvWantsee?.onClick {
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_USER_ID, userId).put(KEY_TYPE, MINE_WANT_SEE), this
            )
        }
        mBinding?.header?.tvWantseeDes?.onClick {
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_USER_ID, userId).put(KEY_TYPE, MINE_WANT_SEE), this
            )
        }
        mBinding?.header?.tvSaw?.onClick {
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_USER_ID, userId).put(KEY_TYPE, MINE_HAS_SEE), this
            )
        }
        mBinding?.header?.tvSawDes?.onClick {
            mineProvider?.startWannaSeeActivity(
                Bundle().put(KEY_USER_ID, userId).put(KEY_TYPE, MINE_HAS_SEE), this
            )
        }
        mBinding?.header?.llAuth?.setOnLongClickListener(null)
        //编辑个人资料
        mBinding?.header?.tvEdit?.onClick {
            userProvider?.startProfileActivity(this)
        }
        //2022.6.7产品让去掉 （后续会做点击查看大图 、更新图片）
        /* mBinding?.header?.ivIcon?.onClick {
             if (mViewModel?.uiState?.value?.success?.isSelf == true)
                 userProvider?.startProfileActivity(this)
         }*/
        /*mBinding?.header?.tvTagFan?.onClick {
            //会员主页
            if (mViewModel?.uiState?.value?.success?.isSelf == true)
                mineProvider?.startMemberCenterActivity(this)
        }*/
        mBinding?.title?.timeZone?.onClick {
            //观影时间轴
            getProvider(ICommunityPersonProvider::class.java)?.startTimeLinePage()
        }

        //关注
        mBinding?.header?.tvAttend?.onClick {
            afterLogin {//关注需要登录 ，其他进入社区个人主页不需要登录
                mViewModel?.apply {
                    when (uiState.value?.success?.followed) {
                        true -> {//取关操作
                            BaseDialog.Builder(this@CommunityPersonActivity)
                                .setContent(R.string.attend_tip).setPositiveButton(
                                    R.string.ok,
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        followAction(userId, ACTION_CANCEL)
                                        dialogInterface.dismiss()
                                        uiState.value?.success?.followed = false
                                        /*(uiState as? MutableLiveData<BaseUIModel<UserHomeViewBean>>)
                                            ?.postValue(uiState.value)*/
                                        updateAttendUI()
//                            setAttendBg(tv_attend,false)

                                    }).setNegativeButton(
                                    R.string.cancel,
                                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                                .create().show()

                        }
                        false -> {//关注操作
                            followAction(userId, ACTION_POSITIVE)
                            uiState.value?.success?.followed = true
//                        setAttendBg(tv_attend,true)

//                            (uiState as? MutableLiveData<BaseUIModel<UserHomeViewBean>>)
//                                ?.postValue(uiState.value)
                            updateAttendUI()
                        }
                        else -> {
                        }
                    }
                }
            }

        }

        mBinding?.header?.tvAuth?.onClick {
//            // 用户认证类型：null代表没有认证， 1"个人", 2"影评人", 3"电影人", 4"机构", -1“审核中”;
//            if (UserManager.instance.userAuthType in arrayOf(-1L, 1L, 2L, 3L, 4L)) {
////                已经进行了认证就不能在进入认证页面
//                return@onClick
//            }
            //不是自己才跳转 身份认证页面
            if (mViewModel?.uiState?.value?.success?.isSelf == false)
                mineProvider?.startAuthenActivity(this)
        }

        //发布
        mBinding?.title?.mTitleWriteIv?.onClick {
            // 跳转到发布页面
            getProvider(IPublishProvider::class.java)
                ?.startEditorActivity(type = CONTENT_TYPE_JOURNAL) //PUBLISH_JOURNAL日志 ，PUBLISH_POST帖子
        }
        mBinding?.title?.mTitleBackIv?.onClick { finish() }

        mBinding?.mMultiStateView?.setMultiStateListener(object :
            MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_ERROR
                    || viewState == MultiStateView.VIEW_STATE_NO_NET
                ) {
                    mViewModel?.loadUserHomeInfo(userId)
                }
            }
        })
    }

    //选择图片结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var dialog = this.getChooseAvatarFragment()
        dialog?.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 点击"关注/已关注"按钮
     * @param action 目标操作
     */
    private fun followAction(userId: Long, action: Long) {
        afterLogin {
            if (userId > 0) {
                mViewModel?.follow(userId, action)
            }
        }
    }

    override fun initData() {
        mBinding?.setVariable(BR.data, mViewModel)
        mViewModel?.loadUserHomeInfo(userId)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun startObserve() {
        registerUserInfoUIStateObserve()
    }

    //页面数据监听
    @SuppressLint("StringFormatMatches")
    private fun registerUserInfoUIStateObserve() {

        mViewModel?.uiState?.observe(this, Observer {

            it.apply {
                mBinding?.mMultiStateView.complete(this)
                success?.apply {

//                    ShapeExt.setGradientColor(bg_gradient1,
//                            GradientDrawable.Orientation.TOP_BOTTOM,
//                            R.color.color_96FFFFFF,
//                            R.color.color_CCFFFFFF,
//                            0)
//                    ShapeExt.setGradientColor(bg_gradient2,
//                            GradientDrawable.Orientation.TOP_BOTTOM,
//                            R.color.color_8c202020,
//                            R.color.color_0aFFFFFF,
//                            0)

                    try {
                        Handler(Looper.myLooper()!!).postDelayed({
                            mBinding?.ivDefaultBg?.loadImage(
                                data = if (backgroundAppUrl.isNullOrEmpty()) avatarUrl else backgroundAppUrl,
                                width = screenWidth,
                                height = (mBinding?.header?.cl?.height ?: 0) + 44.dp + 24.dp,
                                blurRadius = 20F,
                                blurSampling = 1F,
                            )
                        }, 500)
                    } catch (e: Exception) {
                        mBinding?.ivDefaultBg?.loadImage(
                            data = backgroundAppUrl,
                            width = screenWidth,
                            height = (mBinding?.header?.cl?.height ?: 0) + 44.dp + 24.dp ?: 0,
                            blurRadius = 20F,
                            blurSampling = 1F,
                        )
                    }


                    if (registDuration?.isEmpty() == true) mBinding?.header?.tvDes?.gone()

                    var items = FragPagerItems(this@CommunityPersonActivity)

                    if (showArticle)
                        items.add(
                            title = getString(R.string.community_content, limit(articleCount)),
                            clazz = CommunityCenterContentFragment::class.java,
                            args = CommunityCenterContentFragment()
                                .bundler(userId, USER_CENTER_TYPE_ARTICLE)
                        )
                    items.add(
                        title = getString(R.string.community_film_coment, limit(filmCommentCount)),
                        clazz = CommunityCenterContentFragment::class.java,
                        args = CommunityCenterContentFragment().bundler(
                            userId,
                            USER_CENTER_TYPE_FILM_COMMENT
                        )
                    )
                    items.add(
                        title = getString(R.string.community_post, limit(postCount)),
                        clazz = CommunityCenterContentFragment::class.java,
                        args = CommunityCenterContentFragment().bundler(
                            userId,
                            USER_CENTER_TYPE_POST
                        )
                    )
                    //视频
                    items.add(
                        title = getString(R.string.community_video, limit(videoCount)),
                        clazz = CommunityCenterContentFragment::class.java,
                        args = CommunityCenterContentFragment().bundler(
                            userId,
                            USER_CENTER_TYPE_FILM_VIDEO
                        )
                    )
                    items.add(
                        title = getString(R.string.community_group, limit(groupCount)),
                        clazz = CommunityPersonFamilyFragment::class.java,
                        args = CommunityPersonFamilyFragment().bundler(userId)
                    )
                    //片单
                    items.add(
                        title = getString(R.string.community_film_menu, limit(filmListCount)),
                        clazz = CommunityCenterFilmListFragment::class.java,
                        args = CommunityCenterFilmListFragment().bundler(userId)
                    )
                    items.add(
                        title = getString(R.string.community_diary, limit(journalCount)),
                        clazz = CommunityCenterContentFragment::class.java,
                        args = CommunityCenterContentFragment().bundler(
                            userId,
                            USER_CENTER_TYPE_DIARY
                        )
                    )
                    items.add(
                        title = getString(R.string.community_photo, limit(albumCount)),
                        clazz = CommunityPersonPhotoFragment::class.java,
                        args = CommunityPersonPhotoFragment().bundler(userId)
                    )
                    if (showAudio) {
                        items.add(
                            title = getString(R.string.community_brodcast, limit(audioCount)),
                            clazz = CommunityCenterContentFragment::class.java,
                            args = CommunityCenterContentFragment().bundler(
                                userId,
                                USER_CENTER_TYPE_AUDIO
                            )
                        )
                    }


                    var adapter = FragPagerItemAdapter(
                        this@CommunityPersonActivity.supportFragmentManager,
                        items
                    )

                    mBinding?.mViewPager?.adapter = adapter
                    mBinding?.mViewPager?.offscreenPageLimit = adapter.count
                    mBinding?.mTabLayout?.setViewPager(mBinding?.mViewPager)
                    mBinding?.mTabLayout?.setSelectedAnim()

                    if (!showArticle && index == 4L) {//目前只针对 单独跳家族处理，文章不显示 其默认索4(全tab下)引减1 ->index 3
                        index = 3
                    }
                    if (index != 0L)
                        mBinding?.mViewPager?.currentItem = index.toInt()
                    else mBinding?.mViewPager?.currentItem = 0

                    //勋章
                    ongoingMedalInfos?.forEach {
                        var view = LayoutInflater.from(this@CommunityPersonActivity)
                            .inflate(R.layout.item_person_xunzhang, null)
                        var imageView = view.findViewById<ImageView>(R.id.iv)
                        imageView.loadImage(it?.appLogoUrl.orEmpty(), width = 14.dp, height = 14.dp)
                        mBinding?.header?.llCreatorIcons?.addView(view)
                    }

                    //跳转到im
                    mBinding?.header?.tvIm?.onClick {
                        getProvider(IMessageCenterProvider::class.java)?.startChatActivity(
                            this@CommunityPersonActivity,
                            userId,
                            false,
                            nikeName,
                            imId,
                            otherMtimeId = userId,
                            otherNickName = nikeName,
                            otherHeadPic = avatarUrl,
                            otherAuthType = authType,
                            otherAuthRole = authRole
                        )
                    }
                    //设置背景
                    if (isSelf) mBinding?.ivDefaultBg?.setOnLongClickListener {
                        this@CommunityPersonActivity.showChooseAvatarDialog(photo = {
                            showPhotoCropDialog(
                                it,
                                CommConstant.IMAGE_UPLOAD_USER_CENTER_BG,
                                cropType = CROP_TYPE_3_4,
                                avatar = {
                                    //更新本地
                                    mBinding?.ivDefaultBg?.loadImage(
                                        data = it,
                                        width = screenWidth,
                                        height = (mBinding?.header?.cl?.height
                                            ?: 0) + 44.dp + 24.dp,
                                        blurRadius = 20F,
                                        blurSampling = 1F,
                                    )
                                }
                            )
                        })
                        false
                    }
                    //时光轴
                    mBinding?.header?.tvDes?.text =
                        getString(R.string.community_day_des, registDuration)
                    //个性签名设置
                    mBinding?.header?.textInfo?.setConvertText(SparseBooleanArray(), 0, info)
                    if (info?.isNullOrEmpty() == true) {
                        mBinding?.header?.textInfo?.gone()
                    }
                    if (isSelf) {
                        //我的勋章
                        mBinding?.header?.rlCreateResult?.onClick {
                            getProvider(IMineProvider::class.java)?.startMyMedal(this@CommunityPersonActivity)
                        }
                    }
                }
                showOrHideProgressDialog(showLoading)
                error?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        })
        mViewModel?.followUiState?.observe(this, Observer {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (this.bizCode == 0L) {//
                    } else {//
                        mViewModel?.uiState?.value?.success?.followed =
                            !(mViewModel?.uiState?.value?.success?.followed!!)
                        /*(mViewModel?.uiState as? MutableLiveData<BaseUIModel<UserHomeViewBean>>)
                            ?.postValue(mViewModel?.uiState?.value)*/
                        updateAttendUI()
                        toast(this.bizMsg.toString())
                    }
                }
                error?.apply {
                    //变回原状态
                    mViewModel?.uiState?.value?.success?.followed =
                        !(mViewModel?.uiState?.value?.success?.followed!!)
                    /* (mViewModel?.uiState as? MutableLiveData<BaseUIModel<UserHomeViewBean>>)
                         ?.postValue(mViewModel?.uiState?.value)*/
                    updateAttendUI()
                }

                netError?.apply {
                    //变回原状态
                }
            }
        })

    }

    private fun updateAttendUI() {
        mBinding?.header?.tvAttend?.text = mViewModel?.uiState?.value?.success?.followContext
        mBinding?.header?.tvAttend?.visibility =
            if (mViewModel?.uiState?.value?.success?.isSelf == true) View.GONE else View.VISIBLE
        setAttendBg(
            mBinding?.header?.tvAttend!!,
            mViewModel?.uiState?.value?.success?.followed ?: false
        )
        if (mViewModel?.uiState?.value?.success?.followed == true) {
            toast(R.string.family_follow_success)
        }
    }

    private fun limit(count: Long): String {
        return if (count > 9999L)
            "9999+"
        else count.toString()
    }

}
