package com.kotlin.android.home.ui.findmovie.adapter

import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.toplist.IndexTopListQuery
import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.app.data.entity.toplist.TopListItem
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemTimeListContainItemLayoutBinding
import com.kotlin.android.home.databinding.ItemTimeListContainLayoutBinding
import com.kotlin.android.home.databinding.ItemTimeListLayoutBinding
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * 时光榜单contain
 */
class TimeListBinder(private var data: IndexTopListQuery) :
    MultiTypeBinder<ItemTimeListLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_time_list_layout
    }

    override fun onBindViewHolder(binding: ItemTimeListLayoutBinding, position: Int) {
        binding?.recyclerView.apply {
            var binders = arrayListOf<TimeListContainBinder>()
            var datas = data?.items
            datas?.forEach {
                binders.add(TimeListContainBinder(it))
            }
            /* if (datas?.size.orZero() >= 5) {
                 datas?.subList(0, 5)?.forEach {
                     binders.add(TimeListContainBinder(it))
                 }
             } else {
                 datas?.forEach {
                     binders.add(TimeListContainBinder(it))
                 }
             }*/
            setMovieAdapter(binders, binding)
        }
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TimeListBinder
    }

    private fun setMovieAdapter(
        list: List<TimeListContainBinder>,
        binding: ItemTimeListLayoutBinding
    ) {
        createMultiTypeAdapter(
            binding.recyclerView,
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        ).run {
            notifyAdapterDataSetChanged(list)
        }
    }

    /**
     * 时光榜单contain
     */
    class TimeListContainBinder(val bean: TopListInfo) :
        MultiTypeBinder<ItemTimeListContainLayoutBinding>() {
        override fun layoutId(): Int {
            return R.layout.item_time_list_contain_layout
        }

        override fun onBindViewHolder(binding: ItemTimeListContainLayoutBinding, position: Int) {

            if (position % 2 == 0) {//设置背景色
                binding?.clLayout.setBackground(
                    colorRes = R.color.color_f0fbff,
                    endColorRes = R.color.color_ffffff
                )
            } else binding?.clLayout.setBackground(
                colorRes = R.color.color_fffbf0,
                endColorRes = R.color.color_ffffff
            )

            var binders = arrayListOf<TimeListContainItemBinder>()
            var datas = bean.items
            if (datas?.size.orZero() >= 3) {//最大取三条
                datas?.subList(0, 3)?.forEach {
                    binders.add(TimeListContainItemBinder(it))
                }
            } else {
                datas?.forEach {
                    binders.add(TimeListContainItemBinder(it))
                }
            }
            setMovieAdapter(binders, binding)
            binding?.clLayout.onClick {
                getProvider(IHomeProvider::class.java)?.startToplistDetailActivity(bean.id.orZero())
            }
        }

        override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
            return other is TimeListContainBinder && bean.id != other.bean.id
        }

        private fun setMovieAdapter(
            list: List<TimeListContainItemBinder>,
            binding: ItemTimeListContainLayoutBinding
        ) {
            createMultiTypeAdapter(
                binding.itemRV,
                LinearLayoutManager(binding.root.context)
            ).run {
                mOnClickListener?.let {
                    setOnClickListener(it)
                }
                notifyAdapterDataSetChanged(list)
            }
        }

        /**
         * 时光榜单item
         */
        class TimeListContainItemBinder(val bean: TopListItem) :
            MultiTypeBinder<ItemTimeListContainItemLayoutBinding>() {
            override fun layoutId(): Int {
                return R.layout.item_time_list_contain_item_layout
            }

            override fun onBindViewHolder(
                binding: ItemTimeListContainItemLayoutBinding,
                position: Int
            ) {
                super.onBindViewHolder(binding, position)
                binding.item.onClick {
                    getProvider(IHomeProvider::class.java)?.startToplistDetailActivity(bean.listId.orZero())
                }
                when (bean.rank) {
                    1L -> binding.tv.setBackground(
                        colorRes = R.color.color_ff9848,
                        endColorRes = R.color.color_f54104,
                        cornerRadius = 5.dpF,
                        direction = Direction.RIGHT_BOTTOM,
                        orientation = GradientDrawable.Orientation.BL_TR
                    )
                    2L -> binding.tv.setBackground(
                        colorRes = R.color.color_ffbb48,
                        endColor = R.color.color_f59804,
                        cornerRadius = 5.dpF,
                        direction = Direction.RIGHT_BOTTOM,
                        orientation = GradientDrawable.Orientation.BL_TR
                    )
                    3L -> binding.tv.setBackground(
                        colorRes = R.color.color_c3bebb,
                        endColorRes = R.color.color_cca89c,
                        cornerRadius = 5.dpF,
                        direction = Direction.RIGHT_BOTTOM,
                        orientation = GradientDrawable.Orientation.BL_TR
                    )
                }
            }

            override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
                return other is TimeListContainItemBinder && other.bean.itemId != bean.itemId
            }
        }
    }
}