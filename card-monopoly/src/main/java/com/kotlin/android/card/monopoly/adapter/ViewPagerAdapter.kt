package com.kotlin.android.card.monopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.holder.CardViewHolder
import com.kotlin.android.card.monopoly.widget.holder.CofferViewHolder

/**
 * 卡片/保险箱切换视图
 *
 * Created on 2020/8/31.
 *
 * @author o.s
 */
class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            CofferViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.frag_coffer, parent, false).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            })
        } else {
            CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.frag_card, parent, false).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            })
        }
    }

    override fun getItemCount(): Int = 2

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData()
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindData()
    }
}