package com.kotlin.android.card.monopoly.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.app.data.entity.monopoly.Signature
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import kotlinx.android.synthetic.main.item_message_board.view.*

/**
 * 留言板适配器：
 *
 * Created on 2020/11/17.
 *
 * @author o.s
 */

class MsgBoardAdapter(
        private var action: ((userInfo: UserInfo?) -> Unit)? = null
) : RecyclerView.Adapter<MsgBoardAdapter.ViewHolder>() {
    private val data = ArrayList<Signature>()

    fun setData(data: List<Signature>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(parent.context, R.layout.item_message_board, null), action)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bean = if (position in 0 until data.size) {
            data[position]
        } else {
            null
        }
        holder.bindData(bean, position)
    }

    class ViewHolder(
            view: View,
            private var action: ((userInfo: UserInfo?) -> Unit)? = null
    ) : RecyclerView.ViewHolder(view) {

        fun bindData(data: Signature?, position: Int) {
            itemView.avatarView?.apply {
                setUserInfo(data?.userInfo)
                setOnClickListener {
                    action?.invoke(data?.userInfo)
                }
            }
            itemView.nickNameView?.apply {
                data?.userInfo?.apply {
                    text = nickName ?: ""
                }
                setOnClickListener {
                    action?.invoke(data?.userInfo)
                }
            }
            itemView.titleView?.apply {
                text = data?.signature ?: ""
            }
        }

    }
}