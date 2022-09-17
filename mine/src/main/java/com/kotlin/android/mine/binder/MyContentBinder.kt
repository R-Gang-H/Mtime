package com.kotlin.android.mine.binder

import android.view.View
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.CreatorViewBean
import com.kotlin.android.mine.databinding.MineItemCreatMyContentBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.binder
 * @ClassName:      MyContentBinder
 * @Description:    创作中心-我的内容
 * @Author:         haoruigang
 * @CreateDate:     2022/3/14 17:02
 */
class MyContentBinder(
    val data: CreatorViewBean.MyContentBean,
//    val rootMargin: Rect = Rect(4.dp, 0.dp, 4.dp, 0.dp),
    val itemOnClickListener: ((view: View) -> Unit)? = null,
) :
    MultiTypeBinder<MineItemCreatMyContentBinding>() {

    override fun layoutId(): Int = R.layout.mine_item_creat_my_content

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MyContentBinder && other.data != data
    }

    override fun onBindViewHolder(binding: MineItemCreatMyContentBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.apply {
            bean = this@MyContentBinder
            this.root.onClick { itemOnClickListener?.invoke(it) }
        }
    }
}