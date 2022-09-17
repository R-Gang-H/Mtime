package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.filmlist.MyFavorites
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getDrawableOrNull
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemMineMyCollectionBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/29
 * 描述:个人中心-我收藏的片单
 **/
class MineMyCollectionBinder(val bean: MyFavorites) :
    MultiTypeBinder<ItemMineMyCollectionBinding>() {
    override fun layoutId() = R.layout.item_mine_my_collection

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MineMyCollectionBinder && bean.filmListId == other.bean.filmListId
    }

    override fun onBindViewHolder(binding: ItemMineMyCollectionBinding, position: Int) {
        binding.mMoviePicIv.loadImage(
            data = bean.coverUrl,
            width = 60.dp,
            height = 80.dp,
            roundedRadius = 6.dpF,
            defaultImg = getDrawableOrNull(R.drawable.icon_film_list_bg_v)
        )

        binding.mModifyTv.text = "${formatPublishTime(bean.lastModifyTime)}更新"

        binding.mStatus.let {
            if (bean.status == 100L) {
                it.visible()
            } else {
                it.gone()
            }
            it.setBackground(colorRes = R.color.color_f6f6f6, cornerRadius = 1.dpF)
        }

        binding.mUserCert.let {
            bean.userUserAuthType?.apply {
                when(this){
                    4L ->  it.setBackgroundResource(R.drawable.ic_institutions_cert)
                    2L,3L -> it.setBackgroundResource(R.drawable.ic_filmer_cert)
                    else -> {}
                }
            }
        }
    }
}