package com.kotlin.android.card.monopoly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.CircleImageView
import com.kotlin.android.card.monopoly.widget.DrawableTextView
import com.kotlin.android.card.monopoly.widget.card.image.CardImageView
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.WishInfo
import com.kotlin.android.image.bindadapter.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt

/**
 * @desc 拍卖行竞价的item
 * @author zhangjian
 * @date 2021-06-08 11:45:23
 */
class WishWallAdapter(
    val context: Context,
    val list: ArrayList<WishInfo>,
    val wishAction: ((friendId: Long) -> Unit),
    val jumpUserInfo: (userId: Long) -> Unit,
    val showImg: ((cardDetails: CardImageDetailBean) -> Unit)
) :
    RecyclerView.Adapter<WishWallAdapter.MViewHolder>() {


    inner class MViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardImageView: CardImageView? = view.findViewById(R.id.cardImage)
        var avatarView: CircleImageView? = view.findViewById(R.id.avatarView)
        var fullWishBtn: DrawableTextView? = view.findViewById(R.id.fullWishBtn)
        var tvName: TextView? = view.findViewById(R.id.tvName)
        var tvDesption: TextView? = view.findViewById(R.id.tvDesption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_wishing_wall, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        list[position].apply {
            //卡片信息
            holder.cardImageView?.card = cardInfo
            holder.cardImageView?.onClick {
                val cardDetailsInfo = CardImageDetailBean()
                cardInfo?.apply {
                    cardDetailsInfo.card = listOf(this)
                }
                showImg.invoke(cardDetailsInfo)
            }
            //用户头像
            holder.avatarView?.apply {
                loadImage(this, userInfo?.avatarUrl, 20.dp, 20.dp)
            }
            holder.avatarView?.onClick {
                jumpUserInfo.invoke(userInfo?.userId ?: 0L)
            }
            //用户名
            holder.tvName?.text = userInfo?.nickName
            holder.tvName?.onClick {
                jumpUserInfo.invoke(userInfo?.userId ?: 0L)
            }
            //许愿内容
            holder.tvDesption?.text = content
            //设置许愿按钮
            ShapeExt.setShapeColorAndCorner(holder.fullWishBtn, R.color.color_feb12a, 45)
            setFullWishBtnStyle(holder.fullWishBtn)
            holder.fullWishBtn?.onClick {
                wishAction.invoke(userInfo?.userId ?: 0L)
            }
        }
    }

    private fun setFullWishBtnStyle(fullWishBtn: DrawableTextView?) {
        fullWishBtn?.apply {
            setDrawable(DrawableTextView.LEFT, getDrawable(R.drawable.ic_wish_magic), 20.dp, 20.dp)
        }

    }

    override fun getItemCount(): Int = list.size

    fun setData(data: ArrayList<WishInfo>) {
        list.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData(){
        list.clear()
        notifyDataSetChanged()
    }
}