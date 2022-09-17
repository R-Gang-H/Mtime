package com.kotlin.android.card.monopoly.widget.card.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.card.monopoly.widget.card.item.CheckCardItemView
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.w

/**
 * 可勾选的卡片视图适配器：
 *
 * Created on 2020/8/28.
 *
 * @author o.s
 */

class CheckCardAdapter : RecyclerView.Adapter<CheckCardAdapter.ViewHolder>() {
    private val data = ArrayList<Card>()

    var spec: CheckCardView.Spec = CheckCardView.Spec.COFFER
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var limit = 3
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var action: ((event: ActionEvent) -> Unit)? = null

    /**
     * 选择的卡片列表
     */
    var selectedCards: ArrayList<Card> = ArrayList()

    /**
     * 选择卡片数量发生改变的监听
     */
    var selectChange: ((count: ArrayList<Card>) -> Unit)? = null

    var position: Int = -1

    /**
     * 设置卡片数据
     */
    fun setData(data: List<Card>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        selectedCards.clear()
        selectChange?.invoke(selectedCards)
        notifyDataSetChanged()
    }

    fun reset() {
        selectedCards.clear()
        selectChange?.invoke(selectedCards)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CheckCardItemView(parent.context).apply {
            setOnClickListener {
                action?.invoke(
                        ActionEvent(
                                itemPosition = position,
                                state = state,
                                card = card
                        )
                )
                card?.let { card ->
                    (it as? CheckCardItemView).apply {
                        when (state) {
                            CardState.FILL -> {
                                state = CardState.SELECTED
                                selectedCards.add(card)
                                selectChange?.invoke(selectedCards)
                            }
                            CardState.SELECTED -> {
                                state = CardState.FILL
                                selectedCards.remove(card)
                                selectChange?.invoke(selectedCards)
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int = spec.spec

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = if (position in 0 until data.size) {
            data[position]
        } else {
            null
        }
        holder.bindData(card, position, limit, this.position)
    }

    class ViewHolder(private val view: CheckCardItemView) : RecyclerView.ViewHolder(view) {

        fun bindData(card: Card?, position: Int, limit: Int, pos: Int) {
//            "pos=$pos, position=$position, limit=$limit, userCardId=${card?.userCardId}".w()
            view.position = position
            if (position < limit) {
                view.card = card
            } else {
                if (card != null) {
                    view.card = card
                } else {
                    view.state = CardState.LOCK
                }
            }
        }
    }

    data class ActionEvent(
            var itemPosition: Int,
            var position: Int = 1,
            var state: CardState,
            var card: Card? = null
    )
}