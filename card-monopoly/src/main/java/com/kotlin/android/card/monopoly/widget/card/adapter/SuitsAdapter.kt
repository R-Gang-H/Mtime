package com.kotlin.android.card.monopoly.widget.card.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.card.item.SuitsItemView
import com.kotlin.android.app.data.entity.monopoly.Suit

/**
 * 套装适配器
 *
 * Created on 2020/8/28.
 *
 * @author o.s
 */

class SuitsAdapter(
        private var action: ((data: Suit?) -> Unit)? = null
) : RecyclerView.Adapter<SuitsAdapter.ViewHolder>() {
    private val data = ArrayList<Suit>()

    fun setData(data: List<Suit>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SuitsItemView(parent.context).apply {
            action = this@SuitsAdapter.action
        })
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val suit = if (position in 0 until data.size) {
            data[position]
        } else {
            null
        }
        holder.bindData(suit)
    }

    class ViewHolder(
            private val view: SuitsItemView,
    ) : RecyclerView.ViewHolder(view) {

        fun bindData(suit: Suit?) {
            view.data = suit
        }

    }
}