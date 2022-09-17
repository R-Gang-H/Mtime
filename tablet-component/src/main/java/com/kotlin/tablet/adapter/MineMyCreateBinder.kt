package com.kotlin.tablet.adapter

import android.view.View
import com.kotlin.android.app.data.entity.filmlist.MyCreate
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getDrawableOrNull
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemMineMyCreateBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/29
 * 描述:个人中心-我创建的片单
 **/
class MineMyCreateBinder(val bean: MyCreate) : MultiTypeBinder<ItemMineMyCreateBinding>() {
    override fun layoutId() = R.layout.item_mine_my_create

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MineMyCreateBinder && bean.filmListId == other.bean.filmListId
    }

    override fun onBindViewHolder(binding: ItemMineMyCreateBinding, position: Int) {
        binding.mMoviePicIv.loadImage(
            data = bean.coverUrl,
            width = 60.dp,
            height = 80.dp,
            roundedRadius = 6.dpF,
            defaultImg = getDrawableOrNull(R.drawable.icon_film_list_bg_v)
        )

        binding.mModifyTv.text = "${formatPublishTime(bean.lastModifyTime)}更新"

    }

   /* override fun onClick(view: View) {
        when (view.id) {
            R.id.mMyCreateHostLay -> {
                getProvider(ITabletProvider::class.java)?.startFilmListDetailsActivity(
                    1,
                    filmListId = bean.filmListId ?: 0L
                )
            }
        }
        super.onClick(view)
    }*/
}