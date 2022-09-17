package com.kotlin.android.mine.ui.reward

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.mine.CreatorRewardInfo
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityRewardBinding
import com.kotlin.android.mine.ui.reward.adapter.RewardItemBinder
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager

/**
 * 创作中心 - 权益中心
 */
@Route(path = RouterActivityPath.Mine.PAGE_CREATOR_REWARD_ACTIVITY)
class RewardActivity : BaseVMActivity<RewardViewModel, ActivityRewardBinding>() {
    private lateinit var mAdapter: MultiTypeAdapter
    private var mLevelId = 0L
    override fun initView() {
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(mRecycler)
        }
    }

    private fun getBinder(list: List<CreatorRewardInfo>): List<MultiTypeBinder<*>> {
        val listBinder = mutableListOf<MultiTypeBinder<*>>()
        var isShow = false
        list.forEach {
            if (mLevelId != it.levelId) {
                mLevelId = it.levelId!!
                isShow = true
            } else {
                isShow = false
            }
            listBinder.add(RewardItemBinder(it, isShow))
        }
        return listBinder
    }

    override fun initData() {
        mViewModel?.loadData()//需要传入userid
    }

    override fun startObserve() {
        mViewModel?.rewardState?.observe(this) {
            it.apply {
                success?.apply {
                    mBinding?.apply {
                        mBinding?.bean = levelInfo
                        when (levelInfo.levelName) {
                            "L1" -> {
                                tvLevelName.setTextColorRes(R.color.color_20a0da)
                            }
                            "L2" -> {
                                tvLevelName.setTextColorRes(R.color.color_36c096)
                            }
                            "L3" -> {
                                tvLevelName.setTextColorRes(R.color.color_91d959)
                            }
                            "L4" -> {
                                tvLevelName.setTextColorRes(R.color.color_feb12a)
                            }
                            "L5" -> {
                                tvLevelName.setTextColorRes(R.color.color_ff9530)
                            }
                        }
                    }
                    mAdapter.notifyAdapterAdded(getBinder(this.creatorRewardInfos))

                }
            }
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, themeStyle = ThemeStyle.IMMERSIVE)
            .setTitle(titleRes = R.string.mine_creator_reward_title, isBold = true, textSize = 16f)
            .addItem(
                isReversed = false,
                drawableRes = R.drawable.ic_title_bar_36_back,
                reverseDrawableRes = R.drawable.ic_title_bar_36_back_reversed,
                click = {
                    this.finish()
                }
            )
    }
}