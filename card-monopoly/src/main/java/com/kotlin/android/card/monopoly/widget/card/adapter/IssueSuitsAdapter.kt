package com.kotlin.android.card.monopoly.widget.card.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.card.image.SuitImageView
import com.kotlin.android.card.monopoly.widget.card.item.SuitsItemView
import com.kotlin.android.app.data.entity.monopoly.Suit

/**
 * 发行套装适配器
 *
 * Created on 2020/8/28.
 *
 * @author o.s
 */

class IssueSuitsAdapter(
        private var action: ((data: Suit?) -> Unit)? = null
) : RecyclerView.Adapter<IssueSuitsAdapter.ViewHolder>() {
    private val data = ArrayList<Suit>()

    fun setData(data: List<Suit>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SuitImageView(parent.context).apply {
            setOnClickListener {
                action?.invoke(data)
            }
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
            private val view: SuitImageView,
    ) : RecyclerView.ViewHolder(view) {

        fun bindData(suit: Suit?) {
            view.data = suit
        }

    }
}