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
 * ?????????????????????????????????
 */
@Route(path = RouterActivityPath.Live.PAGE_LIVE_SHARE_POSTER_ACTIVITY)
class LiveSharePosterActivity :
    BaseVMActivity<LiveSharePosterViewModel, ActivityLiveSharePosterBinding>(),
    MultiStateView.MultiStateListener {

    companion object {
        // ??????????????? dp
        val COVER_WIDTH_DP = 316.dp
        val COVER_HEIGHT_DP = 177.dp

        // ???????????????px
        val QR_WIDTH_PX = 160.dp
    }

    private var mLiveId = 0L

    // ????????????Adapter
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
        // ????????????
        mViewModel?.let {
            mShareAdapter.notifyAdapterAdded(it.getShareList())
        }
        loadData()
    }

    override fun startObserve() {
        // ????????????
        liveDetailObserve()
        // ????????????
        shareObserve()
    }

    /**
     * ????????????????????????"??????/??????"???????????????
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
     * ???????????????
     */
    private fun initBackGround() {
        // ????????????????????????
        ShapeExt.setShapeCornerWithColor(mLiveSharePosterContentCL, R.color.color_ffffff,
            4, 4, 0, 0)
        // ?????????X??????????????????
        ShapeExt.setShapeCornerWithColor(mLiveSharePosterLogoCL, R.color.color_f2f3f6,
            0, 0, 4, 4)
        // ????????????????????????
        ShapeExt.setShapeCornerWithColor(mLiveSharePosterPlatformLayout, R.color.color_ffffff,
            4, 4, 0, 0)
    }

    private fun initListener() {
        // ????????????
        mLiveSharePosterCloseIv?.onClick {
            finish()
        }
    }

    /**
     * ??????????????????
     */
    private fun loadData() {
        mViewModel?.getLiveDetail(mLiveId, false)
    }

    /**
     * ??????????????????Observe
     */
    private fun liveDetailObserve() {
        mViewModel?.detailUIState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    mStateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
                    // ??????UI
                    updateUI(this)
                    // ?????????????????????
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
     * ??????????????????Observe
     */
    private fun shareObserve() {
        mViewModel?.shareExtendUIState?.observe(this) {
            it?.apply {
                success?.apply {
                    // ??????h5????????????????????????
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
     * ??????UI
     */
    private fun updateUI(extraBean: LiveDetailExtraBean) {
        val liveDetailViewBean = LiveDetailViewBean.convert(extraBean.bean)
        mBinding?.setVariable(BR.data, liveDetailViewBean)
        // ??????????????????????????????????????????????????????
        mLiveSharePosterCoverIv?.loadImage(
            data = liveDetailViewBean.image,
            width = COVER_WIDTH_DP,
            height = COVER_HEIGHT_DP,
            allowHardware = false
        )
    }

    /**
     * ?????????????????????
     */
    private fun loadQrCode() {
        mViewModel?.getShareInfo(mLiveId, SharePlatform.POSTER)
    }

    /**
     * ????????????????????????
     */
    private fun handleSharePlat(view: View, binder: MultiTypeBinder<*>) {
        if (view.id == R.id.mItemLiveSharePostLayout) {
            val platformId = (binder as LiveSharePosterItemBinder).bean.platformId
            // ??????scrollview?????????
            getBitmapByView(platformId, mLiveSharePosterContentSv) { id, bitmap ->
                showShareAction(id, bitmap)
            }
        }
    }

    /**
     * ??????scrollview?????????
     */
    private fun getBitmapByView(
        platformId: Long,
        contentSv: NestedScrollView,
        listener: (Long, Bitmap?) -> Unit,
    ) {
        doAsync {
            // ??????scrollview?????????
            val bitmapByView = contentSv.getBitmapByView()
            runOnUiThread {
                listener.invoke(platformId, bitmapByView)
            }
        }
    }

    private fun showShareAction(platformId: Long, bitmap: Bitmap?) {
        when (platformId) {
            LiveSharePosterItemViewBean.PLATFORM_LOCAL -> { // ????????????
                val saveResult = bitmap?.savePic(this, FileEnv.downloadImageDir)
                showToast(if (saveResult == true) "????????????" else "????????????")
            }
            LiveSharePosterItemViewBean.PLATFORM_WECHAT -> { // ??????
                shareByPlat(SharePlatform.WE_CHAT, bitmap)
            }
            LiveSharePosterItemViewBean.PLATFORM_FRIEND -> { // ?????????
                shareByPlat(SharePlatform.WE_CHAT_TIMELINE, bitmap)
            }
            LiveSharePosterItemViewBean.PLATFORM_SINA -> { // ????????????
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
     * ???????????????
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
     * ????????????????????????????????????
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