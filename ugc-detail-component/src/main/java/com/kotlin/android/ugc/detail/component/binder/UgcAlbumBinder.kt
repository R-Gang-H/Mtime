package com.kotlin.android.ugc.detail.component.binder

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager

import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcDetailAlbumBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * Created by lushan on 2020/8/6
 * ugc相册
 */
open class UgcAlbumBinder(var bean: UgcAlbumViewBean,var sAlbumId:Long) : MultiTypeBinder<ItemUgcDetailAlbumBinding>() {
    var albumUserId = 0L
    override fun layoutId(): Int = R.layout.item_ugc_detail_album

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcAlbumBinder && other.bean.hashCode() != bean.hashCode()
    }

    fun updateData(mutableList: MutableList<UgcAlbumItemBinder>) {
        bean.albumList.addAll(mutableList)
        notifyAdapterSelfChanged()
    }

    fun reset(){
        bean = UgcAlbumViewBean()
    }

    fun deleteImageById(imageId:Long){
        val list = bean.albumList.filter { it.bean.imageId == imageId }
        list.forEach {
            it.notifyAdapterSelfRemoved()
        }
        bean.albumList.removeIf { it.bean.imageId == imageId }
        notifyAdapterSelfChanged()
    }

    fun updateData(newBean: UgcAlbumViewBean) {
        bean.apply {
            pageView = newBean.pageView
            totalCount = newBean.totalCount
            albumList.addAll(newBean.albumList)
            bean.isLoading = false
        }

        notifyAdapterSelfChanged()
    }


    override fun onClick(view: View) {
        if (view.id == R.id.loadMoreTv) {
            bean.isLoading = true
            notifyAdapterSelfChanged()
        }
        super.onClick(view)
    }

    override fun onBindViewHolder(binding: ItemUgcDetailAlbumBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        createMultiTypeAdapter(binding.recyclerView, GridLayoutManager(binding.root.context, 3)).apply {
            notifyAdapterAdded(bean.albumList)
            setOnClickListener { view, any ->
                val mainProvider = getProvider(IMainProvider::class.java)
                val imageList = arrayListOf<String>()
                imageList.addAll(bean.albumList.map { it.bean.imagePic })
                val ugcAlbumItemBinder = any as UgcAlbumItemBinder
                val indexOf = bean.albumList.indexOf(ugcAlbumItemBinder)
                mainProvider?.startPhotoDetailActivityFromAlbum(imageList, if (indexOf <= 0) 0 else indexOf,sAlbumId,albumUserId,bean.albumList.map { it.bean.imageId }.toLongArray())
            }
        }
    }
}