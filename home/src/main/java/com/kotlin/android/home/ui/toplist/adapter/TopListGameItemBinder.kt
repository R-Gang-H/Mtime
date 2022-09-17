package com.kotlin.android.home.ui.toplist.adapter

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.kotlin.android.app.data.entity.toplist.GameRankUser
import com.kotlin.android.app.data.entity.toplist.GameTopList
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistGameBinding

import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.router.ext.getProvider

/**
 * @author vivian.wei
 * @date 2020/8/18
 * @desc 首页_榜单游戏
 */
class TopListGameItemBinder (val context: Context, val bean: GameTopList) : MultiTypeBinder<ItemToplistGameBinding>() {

    private val mCommunityPersonProvider =
            getProvider(ICommunityPersonProvider::class.java)

    companion object {
        const val ITEM_MAX_SIZE = 4
    }

    val mHomeProvider = getProvider(IHomeProvider::class.java)

    private lateinit var mAdapter: MultiTypeAdapter

    override fun layoutId(): Int {
        return R.layout.item_toplist_game
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListGameItemBinder && other.bean.rankType == bean.rankType
    }

    override fun onBindViewHolder(binding: ItemToplistGameBinding, position: Int) {
        bean.userList?.let {
            // 第1名
           it.firstOrNull()?.let { gameRankUser ->
               binding.mItemToplistGameTopInc.data = gameRankUser
               gameRankUser.userInfo?.userId?.let { userId ->
                   binding.mItemToplistGameTopInc.mItemToplistGameTopCl.onClick {
                       mCommunityPersonProvider?.startPerson(userId)
                   }
               }
           }
            // 2~4名
            if(it.size > 1) {
                mAdapter = createMultiTypeAdapter(binding.mItemToplistGameUserRv,
                        GridLayoutManager(context, 3))
                // 最多显示3个
                var others = it.subList(1, ITEM_MAX_SIZE.coerceAtMost(it.size))
                mAdapter.notifyAdapterAdded(getBinderList(others))
            }
        }
    }

    /**
     * 会员列表
     */
    private fun getBinderList(list: List<GameRankUser>): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListGameUserItemBinder>()
        list.map {
            var binder = TopListGameUserItemBinder(it)
            binderList.add(binder)
        }
        return binderList
    }
}