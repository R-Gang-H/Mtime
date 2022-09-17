package com.kotlin.android.live.component.ui.detail

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.savePic
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.live.component.BR
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ActivityLiveSharePosterBinding
import com.kotlin.android.live.component.ui.adapter.LiveSharePosterItemBinder
import com.kotlin.android.live.component.viewbean.LiveDetailExtraBean
import com.kotlin.android.live.component.viewbean.LiveDetailViewBean
import com.kotlin.android.live.component.viewbean.LiveSharePosterItemViewBean
import com.kotlin.android.mtime.ktx.FileEnv
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.getBitmapByView
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.qrcode.component.utils.createQrCode
import com.kotlin.android.qrcode.component.utils.getHitTypeMap
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.share.*
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_live_share_poster.*
import org.jetbrains.anko.doAsync

/**
 * 直播详情生成海报分享页
 */
@Route(path = RouterActivityPath.Live.PAGE_LIVE_SHARE_POSTER_ACTIVITY)
class LiveSharePosterActivity :
    BaseVMActivity<LiveSharePosterViewModel, ActivityLiveSharePosterBinding>(),
    MultiStateView.MultiStateListener {

    companion object {
        // 封面图尺寸 dp
        val COVER_WIDTH_DP = 316.dp
        val COVER_HEIGHT_DP = 177.dp

        // 二维码尺寸px
        val QR_WIDTH_PX = 160.dp
    }

    private var mLiveId = 0L

    // 分享平台Adapter
    private val mShareAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(
            mLiveSharePosterPlatformRv,
            GridLayoutManager(this, 5))
            .apply {
                setOnClickListener(::handleSharePlat)
            }
    }

    override fun initVariable() {
        super.initVariable()
        mLiveId = intent.getLongExtra(LiveDetailActivity.KEY_LIVE_ID, 0L)
    }

    override fun initView() {
        initBackGround()
        initListener()
        ShareManager.install(this) {
            when (it) {
                ShareState.SUCCESS -> {
                    dismissShareDialog()
                }
                ShareState.FAILURE -> {
                }
                ShareState.CANCEL -> {
                    dismissShareDialog()
                }
            }
        }
        mStateView?.topStatusMargin()
        mLiveSharePosterTitleCL?.topStatusMargin()
    }

    override fun initData() {
        // 分享平台
        mViewModel?.let {
            mShareAdapter.notifyAdapterAdded(it.getShareList())
        }
        loadData()
    }

    override fun startObserve() {
        // 直播详情
        liveDetailObserve()
        // 分享信息
        shareObserve()
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET,
            -> {
                loadData()
            }
        }
    }

    /**
     * 初始化背景
     */
    private fun initBackGround() {
        // 直播信息和二维码
        ShapeExt.setShapeCornerWithColor(mLiveSharePosterContentCL, R.color.color_ffffff,
            4, 4, 0, 0)
        // 时光网X时光直播图片
        ShapeExt.setShapeCornerWithColor(mLiveSharePosterLogoCL, R.color.color_f2f3f6,
            0, 0, 4, 4)
        // 分享平台列表弹框
        ShapeExt.setShapeCornerWithColor(mLiveSharePosterPlatformLayout, R.color.color_ffffff,
            4, 4, 0, 0)
    }

    private fun initListener() {
        // 关闭按钮
        mLiveSharePosterCloseIv?.onClick {
            finish()
        }
    }

    /**
     * 加载直播数据
     */
    private fun loadData() {
        mViewModel?.getLiveDetail(mLiveId, false)
    }

    /**
     * 获取直播详情Observe
     */
    private fun liveDetailObserve() {
        mViewModel?.detailUIState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    mStateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
                    // 更新UI
                    updateUI(this)
                    // 加载分享二维码
                    loadQrCode()
                }

                netError?.apply {
                    mStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }

                error?.apply {
                    mStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                if (isEmpty) {
                    mStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

            }
        }
    }

    /**
     * 获取分享信息Observe
     */
    private fun shareObserve() {
        mViewModel?.shareExtendUIState?.observe(this) {
            it?.apply {
                success?.apply {
                    // 设置h5链接生成的二维码
                    this.result.url?.let { url ->
                        if (url.isNotEmpty()) {
                            mLiveSharePosterQrIv?.setImageBitmap(
                                url.createQrCode(
                                    QR_WIDTH_PX,
                                    QR_WIDTH_PX,
                                    getHitTypeMap(),
                                    false,
                                    getColor(R.color.color_000000),
                                    getColor(R.color.color_ffffff)
                                )
                            )
                        }
                    }
                }
                netError?.apply { }
                error?.apply { }
            }
        }
    }

    /**
     * 更新UI
     */
    private fun updateUI(extraBean: LiveDetailExtraBean) {
        val liveDetailViewBean = LiveDetailViewBean.convert(extraBean.bean)
        mBinding?.setVariable(BR.data, liveDetailViewBean)
        // 需要关闭硬件加速才能截取滚动内容图片
        mLiveSharePosterCoverIv?.loadImage(
            data = liveDetailViewBean.image,
            width = COVER_WIDTH_DP,
            height = COVER_HEIGHT_DP,
            allowHardware = false
        )
    }

    /**
     * 加载分享二维码
     */
    private fun loadQrCode() {
        mViewModel?.getShareInfo(mLiveId, SharePlatform.POSTER)
    }

    /**
     * 处理点击分享列表
     */
    private fun handleSharePlat(view: View, binder: MultiTypeBinder<*>) {
        if (view.id == R.id.mItemLiveSharePostLayout) {
            val platformId = (binder as LiveSharePosterItemBinder).bean.platformId
            // 截取scrollview的屏幕
            getBitmapByView(platformId, mLiveSharePosterContentSv) { id, bitmap ->
                showShareAction(id, bitmap)
            }
        }
    }

    /**
     * 截取scrollview的屏幕
     */
    private fun getBitmapByView(
        platformId: Long,
        contentSv: NestedScrollView,
        listener: (Long, Bitmap?) -> Unit,
    ) {
        doAsync {
            // 截取scrollview的屏幕
            val bitmapByView = contentSv.getBitmapByView()
            runOnUiThread {
                listener.invoke(platformId, bitmapByView)
            }
        }
    }

    private fun showShareAction(platformId: Long, bitmap: Bitmap?) {
        when (platformId) {
            LiveSharePosterItemViewBean.PLATFORM_LOCAL -> { // 保存本地
                val saveResult = bitmap?.savePic(this, FileEnv.downloadImageDir)
                showToast(if (saveResult == true) "保存成功" else "保存失败")
            }
            LiveSharePosterItemViewBean.PLATFORM_WECHAT -> { // 微信
                shareByPlat(SharePlatform.WE_CHAT, bitmap)
            }
            LiveSharePosterItemViewBean.PLATFORM_FRIEND -> { // 朋友圈
                shareByPlat(SharePlatform.WE_CHAT_TIMELINE, bitmap)
            }
            LiveSharePosterItemViewBean.PLATFORM_SINA -> { // 新浪微博
                shareByPlat(SharePlatform.WEI_BO, bitmap)
            }
            LiveSharePosterItemViewBean.PLATFORM_QQ -> { // QQ
                shareByPlat(SharePlatform.QQ, bitmap)
            }
            else -> {
            }
        }
    }

    /**
     * 各平台分享
     */
    private fun shareByPlat(plat: SharePlatform, bitmap: Bitmap?) {
        if (ShareSupport.isPlatformInstalled(plat)) {
            val entity = ShareEntity(shareType = ShareType.SHARE_IMAGE)
            entity.image = bitmap
            ShareManager.share(plat, entity)
        } else {
            showToast(getPlatFormTips(plat))
        }
    }

    /**
     * 获取未安装平台客户端提示
     */
    private fun getPlatFormTips(plat: SharePlatform): String {
        return when (plat) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            -> getString(R.string.we_chat_is_not_install)
            SharePlatform.WEI_BO -> getString(R.string.wei_bo_is_not_install)
            SharePlatform.QQ -> getString(R.string.qq_is_not_install)
            else -> ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ShareManager.onActivityResult(requestCode, resultCode, data)
    }

}