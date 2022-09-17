package com.kotlin.android.review.component.item.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.MovieImage
import com.kotlin.android.app.data.entity.image.MoviePhoto
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.review.component.R
import kotlinx.android.synthetic.main.item_review_pic.view.*

/**
 * create by lushan on 2020/11/4
 * description:影评分享剧照列表
 */
class ReviewImageAdapter(var context: Context, var list: MutableList<MovieImage.ImageInfo> = mutableListOf()) : RecyclerView.Adapter<ReviewImageAdapter.ReviewImageHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    private var selectImageId: Long = 0L
    private var selectImageUrl: String? = ""

    inner class ReviewImageHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(bean: MovieImage.ImageInfo, position: Int) {
            with(bean) {
                val marginLayoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
                marginLayoutParams?.apply {
                    leftMargin = if (position == 0) {
                        15.dp
                    } else {
                        5.dp
                    }
                    rightMargin = if (position == list.size - 1) 15.dp else 5.dp
                }
                itemView.picIv?.loadImage(
                    data = image.orEmpty(),
                    width = 108.dp,
                    height = 108.dp
                )
                itemView.selectBtn?.visible(selectImageId == id)
                itemView.onClick {
                    selectImageId = id ?: 0L
                    selectImageUrl = image.orEmpty()
                    notifyDataSetChanged()
                }
            }
        }

    }

    fun setData(list: MutableList<MovieImage.ImageInfo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun setSelectImageInfo(bean: MovieImage.ImageInfo){
        this.selectImageId = bean.id?:0L
        this.selectImageUrl = bean.image.orEmpty()
    }

    fun getSelectImage(): String {
        return selectImageUrl.orEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageHolder {
        return ReviewImageHolder(layoutInflater.inflate(R.layout.item_review_pic, parent, false))
    }

    override fun onBindViewHolder(holder: ReviewImageHolder, position: Int) {
        holder.bindData(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}