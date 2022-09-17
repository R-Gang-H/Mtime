package com.kotlin.android.card.monopoly.widget.card.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.card.monopoly.widget.card.item.SelectCardItemView
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.log.e

/**
 * 可选择卡片视图适配器：
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */

class SelectCardAdapter(
        private var action: ((card: Card?) -> Unit)? = null
) : RecyclerView.Adapter<SelectCardAdapter.ViewHolder>() {
    private val data = ArrayList<Card>()

    var selectModel: SelectCardView.SelectModel = SelectCardView.SelectModel.SINGLE

    var position: Int = 0
        set(value) {
            field = value
            if (value in 0 until data.size) {
                data[value].isSelected = true
                notifyDataSetChanged()
            }
        }

    /**
     * 选择的卡片列表
     */
    var selectedCards: ArrayList<Card> = ArrayList()

    /**
     * 选择卡片数量发生改变的监听
     */
    var selectChange: ((count: ArrayList<Card>) -> Unit)? = null

    fun setData(data: List<Card>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        selectedCards.clear()
        this.data.forEach {
            if (it.isSelected) {
                selectedCards.add(it)
            }
        }
        selectChange?.invoke(selectedCards)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SelectCardItemView(parent.context).apply {
            setOnClickListener {
                card?.let { card ->
                    (it as? SelectCardItemView)?.apply {
                        selectModel.e()
                        when (selectModel) {
                            SelectCardView.SelectModel.SINGLE -> {
                                when (state) {
                                    CardState.FILL -> {
                                        selectedCards.forEach {
                                            it.isSelected = false
                                            notifyItemChanged(it.position)
                                        }
                                        selectedCards.clear()
                                        state = CardState.SELECTED
                                        card.isSelected = true
                                        selectedCards.add(card)
                                        selectChange?.invoke(selectedCards)
                                    }
                                    CardState.SELECTED -> {
                                        state = CardState.FILL
                                        selectedCards.remove(card)
                                        card.isSelected = false
                                        selectChange?.invoke(selectedCards)
                                    }
                                    else -> {
                                    }
                                }
                            }
                            SelectCardView.SelectModel.MULTIPART -> {
                                when (state) {
                                    CardState.FILL -> {
                                        state = CardState.SELECTED
                                        card.isSelected = true
                                        selectedCards.add(card)
                                        selectChange?.invoke(selectedCards)
                                    }
                                    CardState.SELECTED -> {
                                        state = CardState.FILL
                                        selectedCards.remove(card)
                                        card.isSelected = false
                                        selectChange?.invoke(selectedCards)
                                    }
                                    else -> {
                                    }
                                }
                            }
                            SelectCardView.SelectModel.SINGLE_NOT_CANCEL -> {
                                when (state) {
                                    CardState.FILL -> {
                                        selectedCards.forEach {
                                            it.isSelected = false
                                            notifyItemChanged(it.position)
                                        }
                                        selectedCards.clear()
                                        state = CardState.SELECTED
                                        card.isSelected = true
                                        selectedCards.add(card)
                                        selectChange?.invoke(selectedCards)
                                    }
                                    else -> {
                                    }
                                }
                            }
                        }
                    }
                }
                this@SelectCardAdapter.action?.invoke(card)
            }
        })
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until data.size) {
            holder.bindData(data[position], position)
        }
    }

    class ViewHolder(private val view: SelectCardItemView) : RecyclerView.ViewHolder(view) {

        fun bindData(card: Card, position: Int) {
            view.card = card
            card.position = position
            if (card.isSelected) {
                view.state = CardState.SELECTED
            } else {
                view.state = CardState.FILL
            }

        }

    }
}