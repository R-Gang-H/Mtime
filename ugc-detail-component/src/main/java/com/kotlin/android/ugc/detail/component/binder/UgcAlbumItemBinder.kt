package com.kotlin.android.ugc.detail.component.binder

import android.net.Uri
import android.text.TextUtils
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumItemViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcAlbumItemBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import java.io.File

/**
 * Created by lushan on 2020/8/6
 * 相册中每个小图片
 */
open class UgcAlbumItemBinder(var bean: UgcAlbumItemViewBean) : MultiTypeBinder<ItemUgcAlbumItemBinding>() {
    override fun layoutId(): Int = R.layout.item_ugc_album_item

    override fun onBindViewHolder(binding: ItemUgcAlbumItemBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        if (TextUtils.isEmpty(bean.imagePath).not()) {
            binding.picIv.loadImage(
                data = Uri.fromFile(File(bean.imagePath)),
            )
        } else {
            binding.picIv.loadImage(data = bean.imagePic, width = 110.dp, height = 110.dp)
        }
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcAlbumItemBinder && other.bean != bean
    }

}