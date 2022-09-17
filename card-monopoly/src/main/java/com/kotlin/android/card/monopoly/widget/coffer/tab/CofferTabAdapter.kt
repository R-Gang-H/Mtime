package com.kotlin.android.card.monopoly.widget.coffer.tab

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.coffer.tab.CofferTabItemView.ActionType
import com.kotlin.android.card.monopoly.widget.coffer.tab.CofferTabItemView.State
import com.kotlin.android.app.data.entity.monopoly.StrongBoxPosition

/**
 * 保险箱Tab适配器
 *
 * Created on 2020/8/28.
 *
 * @author o.s
 */

class CofferTabAdapter : RecyclerView.Adapter<CofferTabAdapter.ViewHolder>() {
    private val data by lazy { ArrayList<Tab>() }

    var action: ((event: CofferTabItemView.ActionEvent) -> Unit)? = null

    fun setData(data: List<StrongBoxPosition>) {
        this.data.clear()
        data.sortedBy { it.position }.forEach {
            this.data.add(Tab(
                    label = it.position,
                    state = if (it.isActive) {
                        State.NORMAL
                    } else {
                        State.LOCK
                    }
            ))
        }
        this.data.find { it.state == State.LOCK }?.state = State.CURRENT_LOCK
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CofferTabItemView(parent.context).apply {
            // 处理事件
            action = {
                when (it.type) {
                    ActionType.OPEN_NOW -> {
                    }
                    ActionType.SELECT -> {
                        selectItem(it.index)
                    }
                }
                this@CofferTabAdapter.action?.invoke(it)
            }
        })
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position], position)
    }

    fun selectItem(position: Int) {
        data.forEachIndexed { index, tab ->
            if (index != position && tab.state == State.SELECT) {
                tab.state = State.NORMAL
                notifyItemChanged(index)
            }
        }
        data[position].state = State.SELECT
        notifyItemChanged(position)
    }

    class ViewHolder(val view: CofferTabItemView) : RecyclerView.ViewHolder(view) {

        fun bindData(tab: Tab, position: Int) {
            view.apply {
                setLabel(tab.label.toString())
                state = tab.state
            }
        }
    }

    data class Tab(
            val label: Long,
            var state: State = State.LOCK
    )
}