package com.kotlin.android.home.ui.toplist.adapter

import com.kotlin.android.app.data.entity.toplist.GameRankUser
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistGameUserBinding
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/10/4
 * @desc 首页_榜单游戏_2~4名会员
 */
class TopListGameUserItemBinder (val bean: GameRankUser) : MultiTypeBinder<ItemToplistGameUserBinding>() {

    private val mCommunityPersonProvider =
            getProvider(ICommunityPersonProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_toplist_game_user
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListGameUserItemBinder && other.bean.userInfo == bean.userInfo
    }

    override fun onBindViewHolder(binding: ItemToplistGameUserBinding, position: Int) {
        // 序号背景色：排名2~4可能没有数据
        bean.orderNumber?.let {
            when(it) {
                2L -> {
                    ShapeExt.setShapeColorAndCorner(
                            binding.mItemToplistGameUserOrderIv,
                            R.color.color_36c096,
                            7)
                }
                3L -> {
                    ShapeExt.setShapeColorAndCorner(
                            binding.mItemToplistGameUserOrderIv,
                            R.color.color_19b3c2,
                            7)
                }
                4L -> {
                    ShapeExt.setShapeColorAndCorner(
                            binding.mItemToplistGameUserOrderIv,
                            R.color.color_20a0da,
                            7)
                }
                else -> {
                }
            }
        }

        bean.userInfo?.userId?.let {userId ->
            binding.root.onClick {
                mCommunityPersonProvider?.startPerson(userId, 0L)
            }
        }

    }
}