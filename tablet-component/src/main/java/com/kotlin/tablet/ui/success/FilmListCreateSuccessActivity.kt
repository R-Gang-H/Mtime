package com.kotlin.tablet.ui.success

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.filmlist.FilmListCreateResult
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.bus.ext.observe
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.tablet.KEY_FILM_LIST_INFO
import com.kotlin.tablet.PAGE_SUCCESS_ACTIVITY
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ActivityFilmListCreateSuccessBinding
import com.kotlin.tablet.event.FilmListPageCloseEvent

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/14
 * 描述:片单创建成功
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_CREATE_SUCCESS)
class FilmListCreateSuccessActivity :
    BaseVMActivity<FilmListCreateSuccessViewModel, ActivityFilmListCreateSuccessBinding>() {
    private var mFilmListInfo: FilmListCreateResult.SimpleFilmListInfo? = null

    override fun getIntentData(intent: Intent?) {
        mFilmListInfo = intent?.getParcelableExtra(KEY_FILM_LIST_INFO)
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.vm = mViewModel
        initEvent()
    }

    private fun initEvent() {
        mBinding?.apply {
            mFilmListCoverBgIv.loadImage(
                mFilmListInfo?.coverUrl,
                width = screenWidth - 30.dp,
                height = 176.dp,
                roundedRadius = 10.dpF,
                defaultImg = getShapeDrawable(
                    colorRes = R.color.color_f3f3f4,
                    cornerRadius = 10.dpF
                )
            )
            mAfterAddBtn.onClick {
                //以后添加
                finish()
            }
            mNowAddBtn.onClick {
                //现在添加
                RouterManager.instance.getProvider(ITabletProvider::class.java)
                    ?.startFilmSearchActivity(mFilmListInfo?.filmListId)
            }
        }
    }

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(
                title = getString(R.string.tablet_film_list_create_success),
            )
    }

    override fun initData() {
        mFilmListInfo?.let {
            mViewModel?.setData(it)
        }
    }

    override fun startObserve() {
        observePageClose()
    }

    /**
     * 关闭当前界面
     */
    private fun observePageClose() {
        observe(FilmListPageCloseEvent::class.java) {
            if (it.page == PAGE_SUCCESS_ACTIVITY) {
                finish()
            }
        }
    }
}