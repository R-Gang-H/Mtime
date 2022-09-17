package com.kotlin.tablet.ui.details.dialog

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.core.widget.NestedScrollView
import com.kotlin.android.app.data.entity.filmlist.ShareMovy
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.mtime.ktx.ext.getBitmapByView
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.qrcode.component.utils.createQrCode
import com.kotlin.android.qrcode.component.utils.getHitTypeMap
import com.kotlin.android.share.*
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.FilmDetailsShareBinder
import com.kotlin.tablet.databinding.LayoutFilmDetailsShareBinding
import com.kotlin.tablet.ui.details.FilmListDetailsViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * 片单列表页面分享
 */
class FilmDetailsShareDialog(val filmListId: Long) :
    BaseVMDialogFragment<FilmListDetailsViewModel, LayoutFilmDetailsShareBinding>() {

    var shareState: ((state: ShareState) -> Unit)? = null

    private val shareManager by lazy { ShareManager }
    private lateinit var mAdapter: MultiTypeAdapter
    private var shareImageCode =
        "https://mapplink.mtime.cn/?applinkData=%7B%22handleType%22%3A%22jumpPage%22%2C%22pageType%22%3A%22filmListDetails%22%2C%22filmListId%22%3A%22$filmListId%22%7D"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.common_dialog)
    }

    override fun initEnv() {
    }

    override fun activityCreate() {
        super.activityCreate()
        activity?.run {
            shareManager.install(this) {
                shareState?.invoke(it)
                when (it) {
                    ShareState.SUCCESS -> {
                        dismiss()
                    }
                    ShareState.FAILURE -> {
                    }
                    ShareState.CANCEL -> {
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.run {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.run {
                setBackgroundDrawableResource(R.color.transparent)
                decorView.setPadding(0, 0, 0, 0)
                attributes.run {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                    windowAnimations = R.style.BottomDialogAnimation
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(mRecycleView)
            viewTop.setBackground(
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP,
                colorRes = R.color.white,
            )
            ivCoverUrl.setBackground(
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            mConstraint.setPadding(15, 0, 15, 0)
            mConstraintLayout.setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_BOTTOM or Direction.RIGHT_BOTTOM
            )
            mWechat.onClick {//微信点击事件
                share(
                    SharePlatform.WE_CHAT,
                )
            }
            mWechatTime.onClick {//微信朋友圈
                share(
                    SharePlatform.WE_CHAT_TIMELINE,
                )
            }
            mQQ.onClick { //分享QQ
                share(
                    SharePlatform.QQ,
                )
            }
            mWeiBo.onClick { //分享微博
                share(
                    SharePlatform.WEI_BO,
                )
            }
            close.onClick {
                dismiss()
            }
            //二维码部分
            ivShareCode.setImageBitmap(
                shareImageCode.createQrCode(
                    55.dp,
                    55.dp,
                    getHitTypeMap(),
                    false,
                    getColor(R.color.color_8798af),
                    getColor(R.color.color_f2f3f6)
                )
            )
        }
    }

    private fun share(platform: SharePlatform) {
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.QQ,
            SharePlatform.WEI_BO -> {
                if (ShareSupport.isPlatformInstalled(platform)) {
                    val shareEntity = ShareEntity(shareType = ShareType.SHARE_IMAGE)
                    mBinding?.apply {
                        close.gone()
                        getBitmapByView(mNestedScrollView) {
                            close.visible()
                            if (it != null) {
                                shareEntity.image = it
                                shareManager.share(platform, shareEntity)
                            } else {
                                showToast("图片生成失败")
                            }
                        }
                    }
                }
            }
            else -> showToast(
                getString(
                    R.string.share_not_register,
                    getString(platform.title)
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shareManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun initData() {
        //分享内容
        mViewModel?.loadShare(filmListId)
    }

    override fun startObserve() {
        mViewModel?.shareState?.observe(this) {
            it.apply {
                success?.apply {
                    mBinding?.bean = this
                    mBinding?.ivCoverUrl?.loadImage(
                        this.coverUrl,
                        screenWidth,
                        180.dp,
                        defaultImgRes = R.drawable.icon_film_list_bg_h,
                        roundedRadius = 12.dpF,
                        direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
                    )
                    mAdapter.notifyAdapterAdded(getBinder(this.shareMovies))
                }
                error?.apply {
                    showToast("请求失败，请稍后重试")
                }
            }
        }
    }

    private fun getBinder(shareMovies: List<ShareMovy>): List<MultiTypeBinder<*>> {
        val binderList = mutableListOf<MultiTypeBinder<*>>()
        shareMovies.forEach {
            binderList.add(FilmDetailsShareBinder(shareMovies))
        }
        return binderList
    }

    private fun getBitmapByView(
        contentSv: NestedScrollView?,
        listener: (Bitmap?) -> Unit
    ) {
        doAsync {
            val bitmapByView = contentSv?.getBitmapByView()
            uiThread {
                listener.invoke(bitmapByView)
            }
        }
    }
}