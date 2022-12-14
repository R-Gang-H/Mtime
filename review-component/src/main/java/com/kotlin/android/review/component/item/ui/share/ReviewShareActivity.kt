package com.kotlin.android.review.component.item.ui.share

import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.savePic
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.mtime.ktx.FileEnv
import com.kotlin.android.mtime.ktx.ext.*
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.qrcode.component.utils.createQrCode
import com.kotlin.android.qrcode.component.utils.getHitTypeMap
import com.kotlin.android.review.component.BR
import com.kotlin.android.review.component.R
import com.kotlin.android.review.component.databinding.ActivityReviewShareBinding
import com.kotlin.android.review.component.item.adapter.RatingSubRatingAdapter
import com.kotlin.android.review.component.item.adapter.ReviewImageAdapter
import com.kotlin.android.review.component.item.adapter.ReviewShareBinder
import com.kotlin.android.review.component.item.bean.ReviewShareItemViewBean
import com.kotlin.android.review.component.item.bean.ReviewShareItemViewBean.Companion.REVIEW_SHARE_PLATFORM_SWITCH
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.share.*
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_review_share.*
import org.jetbrains.anko.doAsync

/**
 * ??????????????????
 */
@Route(path = RouterActivityPath.Review.PAGE_REVIEW_SHARE_ACTIVITY)
class ReviewShareActivity : BaseVMActivity<ReviewShareViewModel, ActivityReviewShareBinding>() {

    companion object {
        const val REVIEW_CONTENT_ID = "review_content_id"
        const val REVIEW_IS_PUBLISHED = "review_is_published"
        const val REVIEW_IS_SHOW_EDIT_BTN = "review_is_show_edit_btn"
    }

    private var contentId: Long = 0L//??????id
    private var isPublished: Boolean = false//?????????????????????
    private var isShowEditBtn: Boolean = false//????????????????????????
    private val shareAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(
            shareRv,
            LinearLayoutManager(this).apply { orientation = RecyclerView.HORIZONTAL }).apply {
            setOnClickListener(::handleSharePlat)
        }
    }

    private val photoAdapter: ReviewImageAdapter by lazy {
        ReviewImageAdapter(this)
    }

    override fun initVariable() {
        super.initVariable()
        contentId = intent?.getLongExtra(REVIEW_CONTENT_ID, 0L) ?: 0L
        isPublished = intent?.getBooleanExtra(REVIEW_IS_PUBLISHED, true) ?: true
        isShowEditBtn = intent?.getBooleanExtra(REVIEW_IS_SHOW_EDIT_BTN, false) ?: false
    }

    override fun initVM(): ReviewShareViewModel {
        return viewModels<ReviewShareViewModel>().value
    }

    override fun initView() {
        initBackGround()
        initListener()
        editTv.visible(isShowEditBtn)

        initPhotoRecyclerView()
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
        stateView?.topStatusMargin()
        titleView?.topStatusMargin()
    }

    private fun initPhotoRecyclerView() {
        picRv?.apply {
            val linearLayoutManager = LinearLayoutManager(this@ReviewShareActivity)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = linearLayoutManager
            adapter = photoAdapter
        }
    }

    private fun initListener() {
        selectPicTv?.onClick {//??????????????????
            changePicCL?.visible()
        }
        //????????????
        closeIv?.onClick {
            finish()
        }
        //????????????
        editTv?.onClick {
            mViewModel?.reviewDetailState?.value?.success?.let { detail ->
                detail.movieViewBean?.let {
                    if (it.movieId > 0) {
                        val nameCN = it.nameCn
                        val nameEN = it.nameEn
                        val name = if (nameCN.isEmpty()) {
                            nameEN
                        } else {
                            nameCN
                        }
                        getProvider(IPublishProvider::class.java)
                            ?.startEditorActivity(
                                type = CONTENT_TYPE_FILM_COMMENT,
                                movieId = it.movieId,
                                movieName = name,
                                isLongComment = detail.commonBar?.isShortReview?.not().orFalse()
                            )
                        finish()
                    }
                }
            }
        }
//        ????????????????????????
        confirmBtn?.onClick {
            changePicCL?.gone()
//            ????????????
            showSelectPic(photoAdapter.getSelectImage())
        }

        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    loadData()
                }
            }
        })

    }


    private fun initBackGround() {
//        ??????????????????
        movieInfoCL.setBackground(colorRes = R.color.color_ffffff, cornerRadius = 4.dpF)
//        ??????????????????
        ShapeExt.setShapeCornerWithColor(userInfoCL, R.color.color_f2f3f6, 0, 0, 4, 4)
//????????????
        ShapeExt.setShapeCornerWithColor(changePicCL, R.color.color_ffffff, 4, 4, 0, 0)
//        ??????????????????
        ShapeExt.setShapeCornerWithColor(shareRv, R.color.color_ffffff, 4, 4, 0, 0)
//        ???????????????
        ShapeExt.setShapeColorAndCorner(qrLayout, R.color.color_f2f3f6, 4)
        ShapeExt.setShapeColorAndCorner(selectPicTv, R.color.color_30000000, 10)

    }

    override fun initData() {
        mBinding?.setVariable(BR.viewModel, mViewModel)
//        ????????????
        mViewModel?.getShareList()?.apply {
            shareAdapter.notifyAdapterAdded(this)
        }
        loadData()
    }

    private fun loadData() {
//        mViewModel?.updateJoinDays(UserManager.instance.joinDays, UserManager.instance.markMovieNum)
        mViewModel?.loadReviewDetail(
            contentId,
            CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
            0L
        )
//        mViewModel?.getAccountDetail()
    }

    override fun startObserve() {

//        ????????????
        detailObserve()

//        ??????
        photoObserve()

//        ????????????
        shareObserve()

//        ????????????
        accountObserve()
    }

    private fun accountObserve() {
        //        ??????????????????
        mViewModel?.accountDetailState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    UserManager.instance.update(this)
                    mViewModel?.updateJoinDays(this.joinDays ?: 0L, this.markedMovieCount ?: 0L)
                }
            }
        })
    }

    private fun shareObserve() {
        //        ??????
        mViewModel?.shareUIState?.observe(this, Observer {
            it?.apply {
//                showOrHideProgressDialog(showLoading)
                success?.apply {
//                    shareUrl = url.orEmpty()
//                    ????????????????????????
                    qrIv?.setImageBitmap(
                        url.orEmpty().createQrCode(
                            55.dp,
                            55.dp,
                            getHitTypeMap(),
                            false,
                            getColor(R.color.color_8798af),
                            getColor(R.color.color_f2f3f6)
                        )
                    )

                }
                netError?.apply {

                }
                error?.apply {

                }
            }
        })
    }

    private fun photoObserve() {
        mViewModel?.reviewPhotoState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    if (this.imageInfos?.size ?: 0 > 0) {
                        this.imageInfos?.get(0)?.run {
                            photoAdapter.setSelectImageInfo(this)
                            //                    ?????????????????????
                            showSelectPic(this.image.orEmpty())
                        }
                    }
                    photoAdapter.setData(this.imageInfos ?: mutableListOf())

                }
                error?.apply {

                }
                netError?.apply {

                }
            }
        })
    }

    private fun detailObserve() {
        mViewModel?.reviewDetailState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadQrCode(
                        this.movieViewBean?.movieId
                            ?: 0L, this.commonBar?.isShortReview == true
                    )
                    mViewModel?.loadMoviePhoto(this.movieViewBean?.movieId ?: 0L)//????????????
                    mViewModel?.setContent(this)
                    if (isLogin() && this.commonBar?.createUser?.userId == UserManager.instance.userId) {
                        mViewModel?.getAccountDetail()
                    }
                    showRatingView()
                    //                    showSelectPic(this.movieViewBean?.picUrl.orEmpty())
                }
                netError?.apply {
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.apply {
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }

                if (isEmpty) {
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
            }
        })
    }

    //??????????????????????????????????????????
    private fun showRatingView() {
        mBinding?.run {
            mViewModel?.shareViewState?.value?.let {

                var isSubRatingModel = false
                run breaking@{
                    it.userSubScores.forEach { item ->
                        val rating = item.rating
                        if (rating > 0f) {
                            isSubRatingModel = true
                            return@breaking
                        }
                    }
                }

                if (isSubRatingModel) {
                    userRatingView.gone()
                    subRatingRv.visible()
                    subRatingRv.adapter = RatingSubRatingAdapter().setData(it.userSubScores)
                } else {
                    subRatingRv.gone()

                    if (TextUtils.isEmpty(it.userScore)) {
                        userRatingView.gone()
                    } else {
                        userRatingView.visible()
                        userRatingView.supportTouchEvent = false
                        userRatingView.level = it.userScore.toDouble()
                    }
                }
            }
        }
    }

    /**
     * @param movieId ??????id
     * @param isShortComment ??????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private fun loadQrCode(movieId: Long, isShortComment: Boolean) {
        if (isPublished.not() || isShortComment) {//??????????????????
            mViewModel?.getShareInfo(CommConstant.SHARE_TYPE_FILM, movieId)
        } else {//??????????????????
            mViewModel?.getShareInfo(CommConstant.SHARE_TYPE_LONG_REVIEW, contentId)
        }

    }

    private fun showSelectPic(imageUrl: String) {
//        ??????????????????????????????????????????
        selectPicIv?.loadImage(
            data = imageUrl,
            width = 315.dp,
            height = 177.dp,
            allowHardware = false
        )
    }

    /**
     * ??????????????????
     */
    private fun handleSharePlat(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            R.id.sharePlatTv -> {//??????

                val platformId = (binder as ReviewShareBinder)?.bean?.platformId
                if (platformId == REVIEW_SHARE_PLATFORM_SWITCH) {//????????????
//                    mViewModel?.updateInfo(binder.bean.isOpen)
                    if (binder.bean.isOpen) {
                        userNameTv?.text = "???"
                        userPicIv?.setImageResource(R.drawable.default_user_head)
                    } else {
                        userNameTv?.text = mViewModel?.getNickName()
                        userPicIv?.loadImage(
                            data = mViewModel?.getUserPic(),
                            width = 24.dp,
                            height = 24,
                            allowHardware = false
                        )
                    }

                    return
                }
//                ????????????
                selectPicTv?.gone()
                getBitmapByView(platformId, contentSv) { id, bitmap ->
                    showShareAction(id, bitmap)
                }
            }
        }
    }

    private fun showShareAction(platformId: Long, bitmap: Bitmap?) {
        "platformId:$platformId,bitmap:$bitmap".e()
        when (platformId) {
            ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_LOCAL -> {
                if (bitmap?.savePic(this, FileEnv.downloadImageDir) == true) {
                    showToast("????????????")
                } else {
                    showToast("????????????")
                }
                selectPicTv?.visible()
            }
            ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_WECHAT -> {//??????
                shareByPlat(SharePlatform.WE_CHAT, bitmap)
            }
            ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_FIREND -> {//?????????
                shareByPlat(SharePlatform.WE_CHAT_TIMELINE, bitmap)
            }
            ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_QQ -> {//QQ
                shareByPlat(SharePlatform.QQ, bitmap)
            }
            ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_SINA -> {//????????????
                shareByPlat(SharePlatform.WEI_BO, bitmap)
            }

        }

    }

    private fun shareByPlat(plat: SharePlatform, bitmap: Bitmap?) {
        if (ShareSupport.isPlatformInstalled(plat)) {
            val entity = ShareEntity(shareType = ShareType.SHARE_IMAGE)
            entity.image = bitmap
            ShareManager.share(plat, entity)
        } else {
            showToast(getPlatFormTips(plat))
        }
        selectPicTv?.visible()
    }

    private fun getPlatFormTips(plat: SharePlatform): String {
        return when (plat) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE -> getString(R.string.we_chat_is_not_install)
            SharePlatform.WEI_BO -> getString(R.string.wei_bo_is_not_install)
            SharePlatform.QQ -> getString(R.string.qq_is_not_install)
            else -> ""
        }
    }

    private fun getBitmapByView(
        platformId: Long,
        contentSv: NestedScrollView?,
        listener: (Long, Bitmap?) -> Unit
    ) {
        doAsync {
            "platformId:$platformId,??????bitmap???contentSv:$contentSv".e()
            val bitmapByView = contentSv?.getBitmapByView()
            runOnUiThread {
                listener.invoke(platformId, bitmapByView)
            }
        }
    }


    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ShareManager.onActivityResult(requestCode, resultCode, data)
    }
}