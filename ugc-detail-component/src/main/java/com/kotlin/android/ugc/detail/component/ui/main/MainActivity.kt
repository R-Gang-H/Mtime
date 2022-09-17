package com.kotlin.android.ugc.detail.component.ui.main

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.log.d

import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.comment.ICommentProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.bean.UgcTitleBarBean
import com.kotlin.android.ugc.detail.component.databinding.ActivityMainBinding
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = RouterActivityPath.UgcDetail.PAGE_UGC_MAIN_ACTIVITY)
class MainActivity : BaseVMActivity<MainViewModel, ActivityMainBinding>() {
    private lateinit var mAdapter: MultiTypeAdapter
    private val provider: IUgcProvider? = getProvider(IUgcProvider::class.java)
    private val commentProvider: ICommentProvider? = getProvider(ICommentProvider::class.java)

    override fun initVM(): MainViewModel = viewModels<MainViewModel>().value

    override fun initView() {
        mAdapter = createMultiTypeAdapter(multiTypeRecycler, LinearLayoutManager(this)).apply {
            setOnClickListener { view, any ->
                view.d()
                any.d()
            }
        }

    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleView?.apply {
            setData(UgcTitleBarBean(userName = "离人心上秋",headPic = "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg",isAttention = true) )
//            setListener(back = {finish()},attentionClick = {provider?.startUgcDetail(UGC_DETAIL_PIC_ARTICLE)},moreClick = {
////                provider?.startAlbumDetail()
//                commentProvider?.startComment(123L)
//            })
        }
    }

    override fun initData() {
        mViewModel?.getMovieBinder(this, multiTypeRecycler)?.apply {
            mAdapter.notifyAdapterAdded(this)
        }
//        bannerView?.setData((1..10).map { UgcImageViewBean(imageId = it.toLong(),ugcContent = "离人心上秋${it}",ugcPic = if (it%2==0) "https://img5.mtime.cn/mg/2020/08/04/114801.48087636.jpg" else "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg") }.toMutableList())
    }

    override fun startObserve() {
    }
}