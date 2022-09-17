package com.kotlin.android.card.monopoly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.mtime.widgets.photoview.CustomClickListener
import com.mtime.widgets.photoview.PhotoView

/**
 * @desc 查看大图页面adapter
 * @author zhangjian
 * @date 2021-06-17 10:11:37
 */
class CardImgDetailAdapter(
    val context: Context,
    val list: ArrayList<Card>,
    val close: (() -> Unit)
) : PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemPosition(obj: Any): Int {
        return super.getItemPosition(obj)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cardimg_detail, null)
        val photo = view.findViewById<PhotoView>(R.id.ivImg)
        val photoSuit = view.findViewById<PhotoView>(R.id.ivSuitImg)
        val progress = view.findViewById<ProgressBar>(R.id.loading)
        progress.visibility = View.VISIBLE

        if (list[position].type != 3L) {
            photo.visibility = View.VISIBLE
            photoSuit.visibility = View.GONE

            photo.loadImage(
                data = list[position].cardCover,
                width = (CARD_WIDTH.dp * 2.8F).toInt(),
                height = (CARD_HEIGHT.dp * 2.8F).toInt(),
                errorImgRes = R.drawable.ic_def_card_fail,
            ) {
                progress.visibility = View.GONE
                photo.setImageDrawable(it)
            }
            photo.setCustomClickListener(CustomClickListener { close.invoke() })
        } else {
            photo.visibility = View.GONE
            photoSuit.visibility = View.VISIBLE

            photo.loadImage(
                data = list[position].cardCover,
                width = SUIT_WIDTH.dp,
                height = SUIT_HEIGHT.dp,
                errorImgRes = R.drawable.ic_def_card_fail,
            ) {
                progress.visibility = View.GONE
                photoSuit.setImageDrawable(it)
            }

            photoSuit.setCustomClickListener(CustomClickListener { close.invoke() })
        }

        (container as ViewPager).addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (container as ViewPager).removeView(obj as View)
    }
}