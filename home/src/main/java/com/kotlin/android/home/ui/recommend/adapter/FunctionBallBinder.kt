package com.kotlin.android.home.ui.recommend.adapter

import com.kotlin.android.app.data.constant.CommConstant.URL_ZAO_WU_SHE_H5
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.app.router.provider.live.ILiveProvider
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemHomeBallBinding
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 推荐页面-功能球
 */
class FunctionBallBinder : BaseRecommendHeadBinder<ItemHomeBallBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_home_ball
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FunctionBallBinder
    }

    override fun onBindViewHolder(binding: ItemHomeBallBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.apply {
            //找电影
            findFilmTv.onClick {
                getProvider(IHomeProvider::class.java)?.startFindMovie()
            }
            //找家族
            findFamilyTv.onClick {
                getProvider(ICommunityFamilyProvider::class.java)?.startFamilyFind()
            }
            //卡片大富翁
            gameTv.onClick {
                getProvider(ICardMonopolyProvider::class.java) {
                    startCardMainActivity(context = it.context)
                }
            }
            //直播
            liveTv.onClick {
                getProvider(ILiveProvider::class.java) {
                    launchLiveList()
                }
            }
            //造物社
            zaoWuSheTv.onClick {
                getProvider(IJsSDKProvider::class.java) {
                    startH5Activity(BrowserEntity(
                        title = getString(R.string.home_ball_zaowushe),
                        url = URL_ZAO_WU_SHE_H5
                    ))
                }
            }
        }
    }
}
