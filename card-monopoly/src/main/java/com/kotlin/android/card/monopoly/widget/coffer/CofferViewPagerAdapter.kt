package com.kotlin.android.card.monopoly.widget.coffer

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.kotlin.android.card.monopoly.widget.card.adapter.CheckCardAdapter
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.StrongBoxPosition
import com.kotlin.android.ktx.ext.log.e

/**
 * 卡片/保险箱切换视图
 *
 * Created on 2020/8/31.
 *
 * @author o.s
 */
class CofferViewPagerAdapter(private val action: ((event: CheckCardAdapter.ActionEvent) -> Unit)? = null) : RecyclerView.Adapter<CofferViewPagerAdapter.ViewHolder>() {

    private val data = ArrayList<StrongBoxPosition>()

    var selectChange: ((count: ArrayList<Card>) -> Unit)? = null

    fun setData(data: List<StrongBoxPosition>) {
        this.data.clear()
        data.sortedBy { it.position }.forEach {
            if (it.isActive) {
                this.data.add(it)
            }
        }
        notifyDataSetChanged()
    }

    fun reset() {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CheckCardView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            post {
                spec = CheckCardView.Spec.COFFER
            }

            action = {
                it.position = position
                this@CofferViewPagerAdapter.action?.invoke(it)
            }
        })
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val index = position + 1
        val coffer = data.find { it.position == index.toLong() }
        holder.bindData(coffer, position, selectChange)
    }

    class ViewHolder(val view: CheckCardView) : RecyclerView.ViewHolder(view) {

        fun bindData(data: StrongBoxPosition?, position: Int, selectChange: ((count: ArrayList<Card>) -> Unit)? = null) {
            data?.apply {
                "pager index=$position, pos=${this.position}, limit=${unlockStrongBoxCount.toInt()}".e()
                view.position = this.position.toInt()
                view.limit = unlockStrongBoxCount.toInt()
                view.selectChange = selectChange
                view.data = cardList
            }
            if (data == null) {
                view.position = 0
                view.limit = 0
                "pager position=-0, limit=-0".e()
            }
        }
    }

}

class CPAdapter : PagerAdapter() {
    private val data = ArrayList<StrongBoxPosition>()

    fun setData(data: ArrayList<StrongBoxPosition>) {
        data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = data.size

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == (obj as CofferViewPagerAdapter.ViewHolder).itemView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val holder = CofferViewPagerAdapter.ViewHolder(CheckCardView(container.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            post {
                spec = CheckCardView.Spec.COFFER
            }
            container.addView(this)
        })
        val coffer = if (position in 0 until data.size) {
            data[position]
        } else {
            null
        }
        holder.bindData(coffer, position)
        return holder
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView((obj as CofferViewPagerAdapter.ViewHolder).itemView)
    }
}