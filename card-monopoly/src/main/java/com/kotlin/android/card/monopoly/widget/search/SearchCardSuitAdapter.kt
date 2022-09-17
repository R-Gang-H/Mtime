package com.kotlin.android.card.monopoly.widget.search

import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 下拉可选项适配器：
 *
 * Created on 2020/9/15.
 *
 * @author o.s
 */
class SearchCardSuitAdapter(val action: ((suit: Suit) -> Unit)? = null) : RecyclerView.Adapter<SearchCardSuitAdapter.ViewHolder>() {
    private val mHeight = 35.dp
    private val mLabelWidth = 15.dp
    private val mLabelHeight = 15.dp
    private val mPadding = 15.dp
    private val mPaddingRight = 10.dp
    private val mDrawablePadding = 5.dp
    private val mTextSize = 14F

    private val data by lazy { ArrayList<Suit>() }

    fun setData(data: List<Suit>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight)
            gravity = Gravity.CENTER_VERTICAL
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            setPadding(mPadding, 0, mPaddingRight, 0)
            compoundDrawablePadding = mDrawablePadding
            setBackgroundResource(R.drawable.ic_search_item_bg)
            getDrawableStateList(
                    normalRes = R.drawable.ic_search_item_label,
                    selectedRes = R.drawable.ic_search_item_label_highlight
            ).apply {
                setBounds(0, 0, mLabelWidth, mLabelHeight)
                setCompoundDrawables(null, null, this, null)
            }
            setTextColor(getColorStateList(
                    normalColor = getColor(R.color.color_4e5e73),
                    pressColor = getColor(R.color.color_feb12a),
                    selectedColor = getColor(R.color.color_feb12a)
            ))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
        }) {
            data.forEach { suit ->
                suit.isSelected = suit.suitId == it.suitId
                notifyDataSetChanged()
            }
            action?.invoke(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(val view: TextView, val action: ((suit: Suit) -> Unit)? = null) : RecyclerView.ViewHolder(view) {

        fun bindData(data: Suit, position: Int) {
            view.text = data.suitName
            view.isSelected = data.isSelected

            view.setOnClickListener {
                if (!data.isSelected) {
                    data.isSelected = true
                    view.isSelected = data.isSelected
                }
                action?.invoke(data)
            }
        }
    }
}