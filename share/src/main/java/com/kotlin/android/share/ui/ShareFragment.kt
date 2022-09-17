package com.kotlin.android.share.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.share.*
import com.kotlin.android.share.ui.adapter.SharePagerAdapter
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.dismissShareDialog
import kotlinx.android.synthetic.main.share_more_view.*
import kotlinx.android.synthetic.main.share_view.*
import java.io.Serializable

/**
 * 分享对话框
 *
 * Created on 2020/6/29.
 *
 * @author o.s
 */
class ShareFragment : DialogFragment() {
    private val keyLaunchMode = "key_launch_mode"
    private val keyData = "key_data"

    private var data: ShareEntity? = null
    private var launchMode = LaunchMode.STANDARD
    private var event: ((platform: SharePlatform) -> Unit)? = null

    private var shareAdapter: SharePagerAdapter? = null
    private var moreAdapter: SharePagerAdapter? = null

    var shareState: ((state: ShareState) -> Unit)? = null

    /**
     * 分享事件的内部处理
     */
    private val shareClick: (platform: SharePlatform) -> Unit = {
        data?.run {
            // 点击事件处理，接收点击平台类型
            when (it) {
                SharePlatform.WE_CHAT,
                SharePlatform.WE_CHAT_TIMELINE,
                SharePlatform.WEI_BO,
                SharePlatform.QQ -> {
                    if (ShareSupport.isPlatformInstalled(it)) {
                        ShareManager.share(it, this)
                    }
                }
                else -> showToast(getString(R.string.share_not_register, getString(it.title)))
            }
        }
//                ?: showToast(R.string.share_not_data)
        // 分享事件的外部调用
        event?.invoke(it)
    }

    /**
     * 更多的服务事件
     */
    private val moreClick: (platform: SharePlatform) -> Unit = {
        // 点击事件处理，接收点击平台类型
        when (it) {
            SharePlatform.TOP -> ServiceManager.top()
            SharePlatform.FINE -> ServiceManager.fine()
            SharePlatform.DELETE -> ServiceManager.delete()
            SharePlatform.REPORT -> ServiceManager.report()
            SharePlatform.COPY_LINK -> ServiceManager.copyLink()
            SharePlatform.POSTER -> ServiceManager.generatePoster()
            SharePlatform.TOP_CANCEL -> ServiceManager.cancelTop()
            SharePlatform.FINE_CANCEL -> ServiceManager.calcenFine()
            SharePlatform.EDIT->ServiceManager.edit()
            else -> showToast(getString(R.string.service_not_impl, getString(it.title)))
        }
        // 更多的服务事件外部调用
        event?.invoke(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFullScreen)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.run {
            ShareManager.install(this) {
                shareState?.invoke(it)
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
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        savedInstanceState?.run {
//            launchMode = getSerializable(keyLaunchMode) as LaunchMode
//            data = getSerializable(keyData) as? ShareEntity?
//        }

        dialog?.run {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.run {
                decorView.setPadding(0, 0, 0, 0)
                attributes.run {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                    windowAnimations = R.style.BottomDialogAnimation
                }
            }
        }
        val view = inflater.inflate(R.layout.share_view, null)
        view.setBackground(
                colorRes = android.R.color.white,
                cornerRadius = 4.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initShareViewPager()
        initMoreViewPager()
        refreshUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ShareManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        data?.recycle()
    }

    /**
     * 微博分享回调：
     * 在 [Activity.onNewIntent] 中调用
     */
    fun doResultIntent(data: Intent?) {
        ShareManager.doResultIntent(data)
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putSerializable(keyLaunchMode, launchMode)
//        outState.putSerializable(keyData, data)
//    }

    /**
     * 设置启动样式，
     * [LaunchMode.STANDARD]: 常规分享
     * [LaunchMode.ADVANCED]: 跟多操作分享
     */
    fun setLaunchMode(launchMode: LaunchMode, vararg moreActionType: SharePlatform) {
        this.launchMode = launchMode
        notifyMoreData(*moreActionType)
        refreshUI()
    }

    private fun notifyMoreData(vararg moreActionType: SharePlatform) {
        if (moreActionType.isEmpty()) {
            return
        }
        val platforms = ArrayList<ArrayList<SharePlatform>>()
        val page1 = ArrayList<SharePlatform>(4)
        val page2 = ArrayList<SharePlatform>(4)
        val page3 = ArrayList<SharePlatform>(4)
        moreActionType.forEachIndexed { index, sharePlatform ->
            if (index <= 3) {
                page1.add(sharePlatform)
            } else if (index in 4..7) {
                page2.add(sharePlatform)
            }else if(index in 8..11){
                page3.add(sharePlatform)
            }
        }
        if (page1.isNotEmpty()) {
            addPlatform(platforms, page1)
        }
        if (page2.isNotEmpty()) {
            addPlatform(platforms, page2)
        }
        if (page3.isNotEmpty()){
            addPlatform(platforms,page3)
        }
        moreAdapter?.setData(platforms)
        moreIndicatorView?.setViewPager(moreViewPager)

    }

    private fun addPlatform(platforms: ArrayList<ArrayList<SharePlatform>>, page: ArrayList<SharePlatform>) {
        if (page.isNotEmpty()) {
            platforms.add(page)
        }
    }

    /**
     * 设置分享实体
     */
    fun setData(shareEntity: ShareEntity?) {
        this.data = shareEntity
    }

    /**
     * 设置回调监听，如埋点操作等
     */
    fun setEvent(event: ((platform: SharePlatform) -> Unit)?) {
        this.event = event
    }

    private fun initShareViewPager() {
        context?.run {
            shareAdapter = SharePagerAdapter(this, shareClick)
            shareAdapter?.setData(getShareData())
            shareViewPager.adapter = shareAdapter

            shareIndicatorView.run {
                val drawable = getShapeDrawable(
                        colorRes = R.color.color_4E5E73,
                        cornerRadius = 3.dpF
                )
                setNormalColor(drawable)
                setSelectColor(drawable)
                setNormalAlpha(0.2F)
                setViewPager(shareViewPager)
            }
        }
    }

    private fun initMoreViewPager() {
        context?.run {
            moreAdapter = SharePagerAdapter(this, moreClick)
            moreAdapter?.setData(getMoreData())
            moreViewPager.adapter = moreAdapter

            moreIndicatorView.run {
                val drawable = getShapeDrawable(
                        colorRes = R.color.color_4E5E73,
                        cornerRadius = 3.dpF
                )
                setNormalColor(drawable)
                setSelectColor(drawable)
                setNormalAlpha(0.2F)
                setViewPager(moreViewPager)
            }
        }
    }

    /**
     * 常规分享4个（每一页4个，根据业务要求调整，同步布局）
     */
    private fun getShareData(): ArrayList<ArrayList<SharePlatform>> {
        val platforms = ArrayList<ArrayList<SharePlatform>>(1)
        val page1 = ArrayList<SharePlatform>(4)

        page1.add(SharePlatform.WE_CHAT)
        page1.add(SharePlatform.WE_CHAT_TIMELINE)
        page1.add(SharePlatform.WEI_BO)
        page1.add(SharePlatform.QQ)

        platforms.add(page1)
        return platforms
    }

    /**
     * 跟多操作6个（每一页4个，根据业务要求调整，同步布局）
     */
    private fun getMoreData(): ArrayList<ArrayList<SharePlatform>> {
        val platforms = ArrayList<ArrayList<SharePlatform>>(2)
        val page1 = ArrayList<SharePlatform>(4)
        val page2 = ArrayList<SharePlatform>(4)
        // 第一页
        page1.add(SharePlatform.TOP)
        page1.add(SharePlatform.FINE)
        page1.add(SharePlatform.DELETE)
        page1.add(SharePlatform.REPORT)
        // 第二页
        page2.add(SharePlatform.COPY_LINK)
        page2.add(SharePlatform.POSTER)

        platforms.add(page1)
        platforms.add(page2)
        return platforms
    }

    private fun refreshUI() {
//        launchMode.e()
        when (launchMode) {
            LaunchMode.STANDARD -> {
                title.setText(R.string.title_share)
                moreView.visibility = View.GONE
            }
            LaunchMode.ADVANCED -> {
                title.setText(R.string.title_share_more)
                moreView.visibility = View.VISIBLE
            }
        }
    }

    enum class LaunchMode : Serializable {

        /**
         * 标准分享模式
         */
        STANDARD,

        /**
         * 高级分享模式（更多操作）
         */
        ADVANCED
    }
}
