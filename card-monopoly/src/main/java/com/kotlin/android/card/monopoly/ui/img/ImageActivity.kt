package com.kotlin.android.card.monopoly.ui.img

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.CardImgDetailAdapter
import com.kotlin.android.card.monopoly.constants.Constants.KEY_CARD_DETAIL
import com.kotlin.android.card.monopoly.databinding.ActImgBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle

/**
 * @desc 查看大图页面
 * @author zhangjian
 * @date 2021-06-15 17:11:20
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_IMAGE_DETAIL)
class ImageActivity : BaseVMActivity<CardMonopolyApiViewModel, ActImgBinding>() {

    var mCardData: CardImageDetailBean? = null
    var mAdapter: CardImgDetailAdapter? = null

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
    }

    override fun initView() {
        mCardData = intent.getParcelableExtra(KEY_CARD_DETAIL)
        initTitleView()
        initBackground()
        initViewpager()
    }

    private fun initBackground() {
//        mBinding?.mainLayout?.background = getShapeDrawable(
//            colorRes = R.color.color_a2edff,
//            endColorRes = R.color.color_ffffff
//        )
    }

    private fun close(){
        finish()
    }

    private fun initViewpager() {
        mAdapter = CardImgDetailAdapter(this, mCardData?.card as ArrayList<Card>,::close)
        mBinding?.mViewPager?.adapter = mAdapter
        mBinding?.mViewPager?.currentItem = getSelectItemPosition()
    }

    private fun initTitleView() {
        mBinding?.mTitle?.apply {
            setThemeStyle(ThemeStyle.STANDARD_STATUS_BAR)
            setState(State.REVERSE)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(getString(R.string.back), alwaysShow = true)
        }
    }

    private fun getSelectItemPosition(): Int {
        mCardData?.card?.forEachIndexed { index, card ->
            if (card.position != 0) {
                return index
            }
        }
        return 0
    }

    override fun initData() {

    }

    override fun startObserve() {

    }

}