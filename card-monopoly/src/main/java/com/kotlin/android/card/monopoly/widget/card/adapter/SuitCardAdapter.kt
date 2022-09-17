package com.kotlin.android.card.monopoly.widget.card.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.card.image.CardImageView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.click.onClick

/**
 * 套装卡片视图适配器：
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */

class SuitCardAdapter(private var action: ((card: Card?) -> Unit)? = null) :
    RecyclerView.Adapter<SuitCardAdapter.ViewHolder>() {
    private val data = ArrayList<Card>()

    fun setData(data: List<Card>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        notifyDataSetChanged()
    }

    private fun clearPositionData(){
        data.forEach {
            it.position = 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until data.size) {
            holder.bindData(data[position])
            holder.itemView.onClick {
                clearPositionData()
                data[position].position = position
                action?.invoke(data[position])
            }
        }
    }

    class ViewHolder(private val view: CardImageView) : RecyclerView.ViewHolder(view) {

        fun bindData(card: Card) {
            view.card = card
        }

    }
}