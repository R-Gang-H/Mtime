package com.kotlin.android.live.component.ui.detail

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Handler
import android.os.Message
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.app.data.entity.live.*
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.live.ILiveProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.statusbar.StatusBarUtils
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.LIVE_HAD_APPOINT
import com.kotlin.android.live.component.constant.LIVE_STATUS_LIVING
import com.kotlin.android.live.component.constant.LIVE_STATUS_REVIEW
import com.kotlin.android.live.component.constant.LIVE_UN_APPOINT
import com.kotlin.android.live.component.databinding.ActivityLiveDetailBinding
import com.kotlin.android.live.component.manager.CameraStandManager
import com.kotlin.android.live.component.observer.BaseObserver
import com.kotlin.android.live.component.ui.fragment.comment.CommentListFragment
import com.kotlin.android.live.component.ui.fragment.livedetail.LiveDetailFragment
import com.kotlin.android.live.component.ui.widget.LiveFragmentPageAdapter
import com.kotlin.android.live.component.ui.widget.LivingView
import com.kotlin.android.live.component.viewbean.AppointViewBean
import com.kotlin.android.live.component.viewbean.LiveVideoExtraBean
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.liveevent.LIVE_DETAIL_CHANGE_STATUS
import com.kotlin.android.router.liveevent.LIVE_DETAIL_CLICK_VIDEO
import com.kotlin.android.router.liveevent.LIVE_DETAIL_PLAY_NEXT_VIDEO
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.chat_component.ChatroomFragment
import com.kotlin.chat_component.HuanxinConnector
import com.kotlin.chat_component.HuanxinMessageManager
import com.kotlin.chat_component.inner.modules.chat.interfaces.OnMessageSendListener
import kotlinx.android.synthetic.main.activity_live_detail.*

/**
 * ???????????????
 */
@Route(path = RouterActivityPath.Live.PAGE_LIVE_DETAIL_ACTIVITY)
class LiveDetailActivity : BaseVMActivity<LiveDetailViewModel, ActivityLiveDetailBinding>() {
    companion object {
        const val KEY_LIVE_ID = "class_live_id"
        private const val POLL_LIVE_STATUS = 1001//??????????????????
        private const val POLL_TIME = 10 * 1000L//??????????????????????????????
    }

    private var liveId: Long = -1L//??????id

    private var detailBean: LiveDetail? = null//????????????????????????
    private var liveStatus: Long = -1L//????????????
    private var isPolling = false//???????????????????????????
    private var fragmentPagerItemAdapter: LiveFragmentPageAdapter? = null
    private var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.WEI_BO,
            SharePlatform.QQ,
            SharePlatform.COPY_LINK
            -> {
                mViewModel?.getShareInfo(liveId, platform)
            }
            SharePlatform.POSTER -> {
                dismissShareDialog()
                // ???????????????????????????????????????
                getProvider(ILiveProvider::class.java)
                    ?.launchLiveSharePoster(liveId)
            }
        }
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                POLL_LIVE_STATUS -> {
                    pollLiveStatus()
                }
            }
        }
    }

    private var huanxinMessageListener: HuanxinMessageManager.HuanxinMessageListener? = null

    override fun initVariable() {
        super.initVariable()
        liveId = intent.getLongExtra(KEY_LIVE_ID, -1L)
    }

    override fun initVM(): LiveDetailViewModel = viewModels<LiveDetailViewModel>().value

    override fun initView() {
        PreviewVideoPlayer.get()?.attachActivity(this, "")
        KeyBoard.assistActivity(this, true)
        StatusBarUtils.translucentStatusBar(this, true, false)
        immersive().statusBarColor(getColor(R.color.transparent))
//        setStatusBarColor(getColor(R.color.transparent))
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_ERROR || viewState == MultiStateView.VIEW_STATE_NO_NET) {
                    getLiveDetailData()
                }
            }
        })

        livingView?.apply {
            //??????
            shareLiveAction = {
                showShareDialog()
            }
            //?????????????????????????????????
            playUrlAction = {
                //??????????????????????????????UI
                LiveEventBus.get(LIVE_DETAIL_PLAY_NEXT_VIDEO)
                    .post(com.kotlin.android.app.router.liveevent.event.LiveDetailVideoPlayState(it.videoId))
                if (it.isLive || liveStatus == LIVE_STATUS_LIVING) {//????????????????????????????????????
                    //PlayerConfig.ignoreMobile = true
                    mViewModel?.getCameraPlayUrl(it.videoId)//??????????????????????????????
                    //?????????????????????
                    mViewModel?.getDirectorUnits(liveId)
                } else {//????????????????????????
                    //source 1????????? 0??????
                    livingView?.playVideo(
                        it.videoId,
                        liveStatus,
                        CameraStandManager.getCurrentPlayVideo(it.videoId)?.source ?: 0L == 0L
                    )
                }
            }
            //?????????????????????
            sendChatAction = {
                sendMessage(it)
            }
        }
        setTopMargin()
    }

    private fun setTopMargin() {
        val params = livingView?.layoutParams as? ViewGroup.MarginLayoutParams
        params?.topMargin = statusBarHeight
        livingView?.layoutParams = params
    }


    private fun showShareDialog() {
        showShareDialog(
            null,
            launchMode = ShareFragment.LaunchMode.ADVANCED,
            SharePlatform.COPY_LINK,
            SharePlatform.POSTER,
            event = shareAction
        )
    }

    private fun sendMessage(content: String) {
        val page = fragmentPagerItemAdapter?.getPage(0)
        if (page is ChatroomFragment) {
            page.chatLayout?.sendTextMessage(content)
            livingView?.sendChat("")
        }
    }

    override fun onBackPressed() {
        if (fragmentPagerItemAdapter?.count ?: 0 > 0) {
            val firstFragment = fragmentPagerItemAdapter?.getPage(0)
            if (firstFragment is CommentListFragment) {
                if (firstFragment.onBackPressed()) {
                    return
                }
            }
        }
        livingView?.setLpLifeCycle(LivingView.LPLifeCycle.BACKPRESSED) {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        livingView?.setLpLifeCycle(LivingView.LPLifeCycle.RESUME)
    }

    override fun onStart() {
        super.onStart()
        livingView?.setLpLifeCycle(LivingView.LPLifeCycle.START)
    }

    override fun onStop() {
        super.onStop()
        livingView?.setLpLifeCycle(LivingView.LPLifeCycle.STOP)
    }

    override fun onPause() {
        super.onPause()
        livingView?.setLpLifeCycle(LivingView.LPLifeCycle.PAUSE)
    }

    override fun onDestroy() {
        livingView?.setLpLifeCycle(LivingView.LPLifeCycle.RELEASE)
        CameraStandManager.release()
        handler.removeCallbacksAndMessages(null)
        huanxinMessageListener?.let {
            HuanxinMessageManager.removeHuanxinMessageListener(it)
        }

        super.onDestroy()
    }

    override fun initData() {
        //PlayerConfig.ignoreMobile = true
        getLiveDetailData()
    }

    /**
     * @param isFromLogin ???????????????????????????????????????????????????????????????
     */
    private fun getLiveDetailData(isFromLogin: Boolean = false) {
        mViewModel?.getLiveDetail(liveId, isFromLogin)
    }

    private fun getCameraStandList() {
        mViewModel?.getCameraStandList(liveId, true)
    }

    private fun pollLiveStatus() {
        isPolling = true
        mViewModel?.getSignalPolling(detailBean?.roomNum.orEmpty())
    }

    private fun sendRequestLiveStatusMessage() {
        handler.removeMessages(POLL_LIVE_STATUS)
        if (liveStatus != LIVE_STATUS_REVIEW) {
            handler.sendEmptyMessageDelayed(POLL_LIVE_STATUS, POLL_TIME)
        }
    }

    override fun startObserve() {
        //??????
        shareObserve()
        //??????????????????
        detailObserve()
        //????????????
        appointObserve()
        //??????????????????
        cameraStandListObserve()
        //????????????????????????
        cameraStandVideoPlayUrlObserve()

        //????????????????????????
        releateVideoPlayObserve()
        //?????????????????????
        streamUrlChangeObserve()
        //???????????????????????????
        liveStatusObserve()
        //???????????????????????????
        liveOnLinePersonNumObserve()
        //??????????????????
        loginObserve()
        //??????????????????
        singlePollingObserve()
        //???????????????
        directorObserve()
    }

    private fun directorObserve() {
        mViewModel?.directorUnitsState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    //??????????????????
                    livingView?.updateDirectorUnits(this)
                }
            }
        })
    }

    private fun singlePollingObserve() {
        var action: ((String) -> Unit) = {//????????????????????????????????????
            sendRequestLiveStatusMessage()
        }
        mViewModel?.signalPollingState?.observe(
            this,
            BaseObserver<MutableList<SignalPolling>>(this, action, action, action = {
                mViewModel?.handleSinglePolling(detailBean, it, liveStatus)
                sendRequestLiveStatusMessage()
            })
        )
    }

    /**
     * ??????????????????
     */
    private fun loginObserve() {
        LiveEventBus
            .get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java)
            .observe(this, Observer {
                getLiveDetailData(true)
            })
    }

    /**
     * ????????????????????????
     */
    private fun liveOnLinePersonNumObserve() {
        mViewModel?.liveOnLinePersonNumState?.observe(this, Observer {
            livingView?.refreshLiveDataInfo(liveStatus, it)
        })
    }

    /**
     * ????????????
     */
    private fun liveStatusObserve() {
        mViewModel?.liveStatusSocketState?.observe(this, Observer {
            handleStatusChange(it)
        })
    }

    private fun handleStatusChange(newLiveStatus: Long) {
        if (liveStatus != newLiveStatus) {
            //??????UI??????
            getLiveDetailData()
        }
        sendRequestLiveStatusMessage()
        liveStatus = newLiveStatus
    }

    /**
     * ?????????????????????
     */
    private fun streamUrlChangeObserve() {
        mViewModel?.cameraIdOfStreamChangeState?.observe(this, Observer {
            livingView?.cameraStreamUrlChange(it)
        })
    }

    /**
     * ?????????????????????????????????????????????
     */
    private fun releateVideoPlayObserve() {
        LiveEventBus.get(
            LIVE_DETAIL_CLICK_VIDEO,
            com.kotlin.android.app.router.liveevent.event.LiveDetailVideoPlayState::class.java
        )
            .observe(this, Observer {
                //????????????????????????
                livingView?.playReleateVideo(it.videoId)
            })
    }

    //??????????????????
    private fun cameraStandVideoPlayUrlObserve() {
        var action: ((String) -> Unit) = {
            livingView?.setVideoPlayUrlError()
            livingView?.setPlayerData(false, liveStatus)
        }
        mViewModel?.cameraPlayUrlState?.observe(
            this,
            BaseObserver<CameraPlayUrl>(this, action, action) {
                livingView?.playLiveVideo(it)
            })
    }

    //????????????
    private fun cameraStandListObserve() {
        var action: ((String) -> Unit) = {
            livingView?.setVideoPlayUrlError()
            livingView?.playCameraVideo(0L, false, false)
        }
        mViewModel?.cameraStandListState?.observe(
            this,
            BaseObserver<LiveVideoExtraBean>(this, action, action) {

                var cameraStandBean = it.bean as CameraStandList
                CameraStandManager.setCameraData(cameraStandBean)
                //???????????????????????????
                //???????????????????????????
                var cameraList = cameraStandBean.cameraList
                if (cameraList?.isNotEmpty() == true) {
                    livingView?.playCameraVideo(cameraList[0].videoId, it.isUpate, false)
                } else {//??????????????????????????????????????????????????????
                    livingView?.playCameraVideo(0L, false, false)
                    livingView?.refreshCameraStandList()
                }
            })
    }

    // ????????????
    private fun appointObserve() {
        var action: ((String) -> Unit) = {
            it.showToast()
        }
        mViewModel?.liveAppointUIState?.observe(
            this,
            BaseObserver<LiveAppointResult>(this, action, action) {
                livingView?.appointSuccess(it.appointCount)
            })
    }

    private fun detailObserve() {
        mViewModel?.detailUIState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    setMultiViewState(MultiStateView.VIEW_STATE_CONTENT)
                    bean.updateDetailUI(isFromLogin)

                }
                netError?.apply {
                    handleDetailError(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.apply {
                    handleDetailError(MultiStateView.VIEW_STATE_ERROR)
                }
                if (isEmpty) {
                    handleDetailError(MultiStateView.VIEW_STATE_EMPTY)
                }
            }
        })
    }

    /**
     * ??????????????????????????????????????????
     */
    private fun handleDetailError(@MultiStateView.ViewState state: Int) {
        if (isPolling.not()) {
            setMultiViewState(state)
        }
        sendRequestLiveStatusMessage()
    }

    private fun loadChatroomUi() {
//        val page = fragmentPagerItemAdapter?.getPage(0)
//        if (page is ChatroomFragment) {
//            page.loadChatroomUi()
//        }
    }

    private fun chatRoomDismissLoading() {
        val page = fragmentPagerItemAdapter?.getPage(0)
        if (page is ChatroomFragment) {
            page.dismissLoadingView()
        }
    }

    private fun onlyLoadLiveDetail() {
        runOnUiThread {
            var creator = FragPagerItems(this@LiveDetailActivity)
            creator.add(title = "??????", clazz = LiveDetailFragment::class.java)
            fragmentPagerItemAdapter = LiveFragmentPageAdapter(supportFragmentManager, creator)

            livingVP?.apply {
                adapter = fragmentPagerItemAdapter
            }
            livingTab?.apply {
                setViewPager(livingVP)
                setSelectedAnim()
            }
        }
    }

    /**
     * ???????????????Fragment
     */
    private fun addChatRoomFragment() {
        var creator = FragPagerItems(this@LiveDetailActivity)
        creator.add(
            title = "??????",
            clazz = ChatroomFragment::class.java,
            args = ChatroomFragment.bundle(detailBean?.easeMobRoomNum)
        )
        creator.add(title = "??????", clazz = LiveDetailFragment::class.java)
        fragmentPagerItemAdapter = LiveFragmentPageAdapter(supportFragmentManager, creator)

        livingVP?.apply {
            adapter = fragmentPagerItemAdapter
        }
        livingTab?.apply {
            setViewPager(livingVP)
            setSelectedAnim()
        }

        //????????????????????????
        setMessageSendListener()
    }

    //????????????????????????
    private fun setMessageSendListener() {
        val page = fragmentPagerItemAdapter?.getPage(0)
        if (page is ChatroomFragment) {
            page.setOnMessageSendListener(object : OnMessageSendListener {
                override fun onSendSuccess(message: EMMessage) {
                    mViewModel?.updateDanmu(listOf(message), true) {
                        livingView?.updateDanmu(it)
                    }
                }
            })
        }
    }

    private fun LiveDetail.updateDetailUI(isFromLogin: Boolean) {
        //PlayerConfig.ignoreMobile = liveStatus == LIVE_STATUS_LIVING
        detailBean = this
        //??????????????????????????????????????????????????????????????????
        livingView?.setLpLifeCycle(LivingView.LPLifeCycle.PAUSE)
        livingView?.liveStatus = liveStatus
        livingView?.updateAppointView(AppointViewBean(
            if (startTime <= 0) "" else getString(
                R.string.live_component_live_start_time_format,
                TimeExt.millis2String(startTime * 1000, "MM???dd??? HH:mm")
            ),
            if (appointed) LIVE_HAD_APPOINT else LIVE_UN_APPOINT, appointCount
        ), backAction = {
            onBackPressed()
        }, appointAction = {//????????????
            afterLogin {//???????????????????????????
                mViewModel?.liveAppoint(liveId)
            }
        }, shareAction = {//??????
            showShareDialog()
        })
        livingView?.showBgImageView(appointedImage)

        if (liveStatus == LIVE_STATUS_LIVING) {
            addChatRoomFragment()
            if (HuanxinConnector.instance.isConnected()) {
                loadChatroomUi()
                connectHuanxinSuccess()
            } else {
                onlyLoadLiveDetail()
            }
        } else {
            var creator = FragPagerItems(this@LiveDetailActivity)
            creator.add(title = "??????", clazz = CommentListFragment::class.java)
            creator.add(title = "??????", clazz = LiveDetailFragment::class.java)
            fragmentPagerItemAdapter = LiveFragmentPageAdapter(supportFragmentManager, creator)
            livingVP?.apply {
                adapter = fragmentPagerItemAdapter
            }
            livingTab?.apply {
                setViewPager(livingVP)
                setSelectedAnim()
            }
        }

        LiveEventBus.get(LIVE_DETAIL_CHANGE_STATUS).post(Any())

        ticketBtn?.apply {
            //????????????,???????????????????????????????????????????????????????????????
            visible(ticketStatus != 0L)
            onClick {
                //????????????????????????
                var ticketProvider: ITicketProvider? =
                    getProvider(ITicketProvider::class.java)
                ticketProvider?.startMovieDetailsActivity(movie?.movieId ?: 0L)

            }
        }

        CameraStandManager.setVideoList(this)
        //??????????????????????????????
        livingView?.refreshLiveDataInfo(liveStatus, onlineCount)
        when (liveStatus) {
            LIVE_STATUS_LIVING -> {//?????????????????????????????????
                //PlayerConfig.ignoreMobile = true
                getCameraStandList()
            }
            LIVE_STATUS_REVIEW -> {
                if (video?.isNotEmpty() == true) {
                    livingView?.setPlayerData(false, liveStatus = LIVE_STATUS_REVIEW)
                    livingView?.playReleateVideo(video?.get(0)?.videoId ?: 0L, true)

                }
            }
        }

        //??????????????????
        this@LiveDetailActivity.liveStatus = liveStatus
        sendRequestLiveStatusMessage()
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun connectHuanxinSuccess() {
        if (huanxinMessageListener == null) {
            huanxinMessageListener =
                object : HuanxinMessageManager.HuanxinMessageListener {
                    override fun onMessageReceived(messages: List<EMMessage>?) {
                        mViewModel?.updateDanmu(messages, false) {
                            livingView?.updateDanmu(it)
                        }
                    }

                    override fun onCmdMessageReceived(messages: List<EMMessage>?) {
                        //????????????
                        mViewModel?.executeCmdMessage(
                            messages,
                            detailBean,
                            liveStatus
                        )
                    }

                    override fun onMessageDelivered(messages: List<EMMessage>?) {}
                }
        }

        huanxinMessageListener?.let {
            HuanxinMessageManager.addHuanxinMessageListener(it)
        }
        chatRoomDismissLoading()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        livingView?.setConfig(livingView, newConfig)
        StatusBarUtils.translucentStatusBar(
            this,
            newConfig.orientation == Configuration.ORIENTATION_PORTRAIT,
            false
        )
        hideSoftInput()
        contentCL?.apply {
            val layoutParams = this.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            this.layoutParams = layoutParams
        }
    }

    /**
     * ??????????????????????????????
     */
    fun getDetailDataBean() = detailBean

    private fun setMultiViewState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
    }

    private fun shareObserve() {
        //??????
        mViewModel?.shareExtendUIState?.observe(this, ShareObserver(this))
    }
}