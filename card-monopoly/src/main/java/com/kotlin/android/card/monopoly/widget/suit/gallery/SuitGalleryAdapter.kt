package com.kotlin.android.card.monopoly.widget.suit.gallery

import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.log.e

/**
 * 套装轮播图适配器：
 *
 * Created on 2020/9/9.
 *
 * @author o.s
 */
class SuitGalleryAdapter : RecyclerView.Adapter<SuitGalleryAdapter.ViewHolder>() {

    private val data = ArrayList<Suit>()

    private fun addStartBorder() {
        (0..1).forEach { _ ->
            data.add(0, Suit(isBorder = true))
        }
    }

    private fun addEndBorder() {
        (0..1).forEach { _ ->
            data.add(Suit(isBorder = true))
        }
    }

    fun setData(data: List<Suit>) {
        this.data.clear()
        this.data.addAll(data)
        addStartBorder()
        addEndBorder()
        notifyDataSetChanged()
    }

    fun getDataAt(position: Int): Suit? {
        if (position in 0 until data.size) {
            return data[position]
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(parent.context, R.layout.item_suit_gallery, null).apply {
            layoutParams = ViewGroup.LayoutParams(SuitGalleryView.itemWidth, SuitGalleryView.itemHeight)
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(data: Suit) {
            if (data.isBorder) {
                itemView.visibility = View.GONE
            } else {
                itemView.visibility = View.VISIBLE
                (itemView as? GalleryItemLayout)?.apply {
                    url = data.suitCover
                    bitmap = null

                    data.suitCover?.apply {
                        loadImage(this, SuitGalleryView.itemWidth, SuitGalleryView.itemHeight, true) {
                            "loadImage $it".e()
                            if (url == data.suitCover) {
                                if (it is BitmapDrawable) {
                                    itemView.bitmap = it.bitmap
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}