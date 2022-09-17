package com.kotlin.android.card.monopoly.widget.card.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.card.item.PickCardItemView
import com.kotlin.android.card.monopoly.widget.card.view.PickCardView
import com.kotlin.android.app.data.entity.monopoly.Card

/**
 * 可拾取卡片视图适配器：
 *
 * Created on 2020/8/28.
 *
 * @author o.s
 */

class PickCardAdapter : RecyclerView.Adapter<PickCardAdapter.ViewHolder>() {
    private val data = ArrayList<Card>()

    var action: ((card: Card?) -> Unit)? = null

    var spec: PickCardView.Spec = PickCardView.Spec.ROBOT
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setData(data: List<Card>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PickCardItemView(parent.context))
    }

    override fun getItemCount(): Int = spec.spec

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = if (position in 0 until data.size) {
            data[position]
        } else {
            null
        }
        holder.action = action
        holder.bindData(card, position)
    }

    class ViewHolder(private val view: PickCardItemView) : RecyclerView.ViewHolder(view) {

        var action: ((card: Card?) -> Unit)? = null

        fun bindData(card: Card?, position: Int) {
            card?.apply {
                view.action = action
                view.card = card
            } ?: resetItemView(position)
        }

        private fun resetItemView(position: Int) {
            view.card = null
        }
    }
}