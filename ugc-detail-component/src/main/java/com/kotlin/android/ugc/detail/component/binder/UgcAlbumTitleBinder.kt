package com.kotlin.android.ugc.detail.component.binder

import android.view.View
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumTitleViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcAlbumTitleBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/8/12
 * description:相册详情页titleBinder
 */
class UgcAlbumTitleBinder(var bean: UgcAlbumTitleViewBean) : MultiTypeBinder<ItemUgcAlbumTitleBinding>() {
    override fun layoutId(): Int = R.layout.item_ugc_album_title

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean = other is UgcAlbumTitleBinder && other.bean !=bean
    override fun onBindViewHolder(binding: ItemUgcAlbumTitleBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        ShapeExt.setShapeCorner2Color2Stroke(binding.addPicLL,corner = 12,strokeColor = R.color.color_20a0da,strokeWidth = 1)
    }

    fun updateBean(name:String,albumId:Long,userId:Long){
        bean.album = name
        bean.albumId = albumId
        bean.userId = userId
        notifyAdapterSelfChanged()
    }

    fun updateAlbumName(name:String){
        bean.album = name
        notifyAdapterSelfChanged()
    }

    fun getAlbumName():String = bean.album

    override fun onClick(view: View) {
        when(view){
            binding?.albumFl->{//修改相册名称
                if (bean.isMyAlbum()) {//如果是我的点击相册名称修改相册名称
                    super.onClick(view)
                }
            }
            else->super.onClick(view)
        }
    }
}