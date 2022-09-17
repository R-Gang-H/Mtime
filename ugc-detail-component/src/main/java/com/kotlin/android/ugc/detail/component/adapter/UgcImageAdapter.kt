package com.kotlin.android.ugc.detail.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.click.onClick

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.BR
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcImageViewBean
import com.kotlin.android.widget.adapter.multitype.inflateDataBinding
import kotlinx.android.synthetic.main.item_ugc_detail_title_image.view.*

/**
 * create by lushan on 2020/8/7
 * description: ugc图片详情banner
 */
class UgcImageAdapter(var ctx: Context, var list: MutableList<UgcImageViewBean> = mutableListOf()) : RecyclerView.Adapter<UgcImageAdapter.ViewHolder>() {
    private val mainProvider = getProvider(IMainProvider::class.java)

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(bean: UgcImageViewBean, position: Int) {
            binding.setVariable(BR.data, bean)
            binding.lifecycleOwner = binding.root.context as LifecycleOwner
            binding.root.ugcIv?.onClick {
                val imageList = arrayListOf<String>()
                imageList.addAll(list.map { it.ugcPic })
                mainProvider?.startPhotoDetailActivity(imageList, position)
            }

        }

    }

    fun setData(list: MutableList<UgcImageViewBean>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun getContent(position: Int): String {
        return if (position < 0 || position >= list.size) {
            ""
        } else {
            list[position].ugcContent
        }
    }

    fun getTitle(position: Int): String = if (position < 0 || position >= list.size) "" else list[position].title

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflateDataBinding(R.layout.item_ugc_detail_title_image))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position],position)
    }
}