package com.kotlin.android.card.monopoly.widget.lack

import android.text.method.LinkMovementMethod
import android.util.Range
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.entity.LackCardViewBean
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.invisible
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.*

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import kotlinx.android.synthetic.main.item_lack_card_type_card.view.*
import kotlinx.android.synthetic.main.item_lack_card_type_friend.view.*

/**
 * 缺失卡片适配器：
 *
 * Created on 2020/9/15.
 *
 * @author o.s
 */
class LackCardSuitAdapter(val action: ((data: UserInfo) -> Unit)? = null) : RecyclerView.Adapter<LackCardSuitAdapter.ViewHolder>() {

    private val data by lazy { ArrayList<LackCardViewBean>() }

    var suit: Suit? = null

    fun setData(data: List<LackCardViewBean>?) {
        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == ITEM_TYPE_FRIEND) {
            FriendViewHolder(View.inflate(parent.context, R.layout.item_lack_card_type_friend, null), action)
        } else {
            CardViewHolder(View.inflate(parent.context, R.layout.item_lack_card_type_card, null), action)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position], position, suit)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return if (position in 0 until data.size) {
            data[position].type
        } else {
            0
        }
    }

    abstract class ViewHolder(val view: View, val action: ((data: UserInfo) -> Unit)? = null) : RecyclerView.ViewHolder(view) {
        abstract fun bindData(data: LackCardViewBean, position: Int, suit: Suit? = null)
    }

    class CardViewHolder(view: View, action: ((data: UserInfo) -> Unit)? = null) : ViewHolder(view, action) {

        override fun bindData(data: LackCardViewBean, position: Int, suit: Suit?) {
            if (data.type == ITEM_TYPE_CARD) {
                itemView.setBackground(
                        colorRes = R.color.color_ffffff,
                        strokeColorRes = R.color.color_f2f3f6,
                        strokeWidth = 3.dp,
                        cornerRadius = 6.dpF
                )
                itemView.cardImageView?.apply {
                    card = Card(cardCover = data.cardCover)
                }
                itemView.titleView?.apply {
                    movementMethod = LinkMovementMethod.getInstance()
                    val str = text.toString()
                    val range = Range(18, str.length)
                    text = str.toSpan()
                            .toBold(range)
                            .toUnderLine(range)
                            .toColor(range, color = getColor(R.color.color_1fc4ca))
                            .toLink(range) { _, _ ->
                                suit?.cardList?.forEach {
                                    it.isSelected = it.cardId == data.cardId
                                }
                                getProvider(ICardMonopolyProvider::class.java)?.startAuctionActivity(0, suit)
                            }
                }
            }
        }
    }

    class FriendViewHolder(view: View, action: ((data: UserInfo) -> Unit)? = null) : ViewHolder(view, action) {

        override fun bindData(data: LackCardViewBean, position: Int, suit: Suit?) {
            if (data.type == ITEM_TYPE_FRIEND) {
                data.friend1?.apply {
                    itemView.friendView1?.apply {
                        visible()
                        setBackground(
                                colorRes = R.color.color_f2f3f6,
                                cornerRadius = 6.dpF
                        )
                        setOnClickListener {
                            action?.invoke(data.friend1)
                        }
                    }
                    itemView.avatarView1.setUserInfo(data.friend1)
                    itemView.nickNameView1?.let {
                        it.isOnline = isOnline
                        it.text = nickName
                    }
                } ?: itemView.friendView1?.invisible()

                data.friend2?.apply {
                    itemView.friendView2?.apply {
                        visible()
                        setBackground(
                                colorRes = R.color.color_f2f3f6,
                                cornerRadius = 6.dpF
                        )
                        setOnClickListener {
                            action?.invoke(data.friend2)
                        }
                    }
                    itemView.avatarView2.setUserInfo(data.friend2)
                    itemView.nickNameView2?.let {
                        it.isOnline = isOnline
                        it.text = nickName
                    }
                } ?: itemView.friendView2?.invisible()
            } else {
                itemView.cardImageView?.card = Card(cardCover = data.cardCover)
            }
        }
    }

    companion object {
        private const val ITEM_TYPE_CARD = 1
        private const val ITEM_TYPE_FRIEND = 0 // 拥有卡的好友
    }
}