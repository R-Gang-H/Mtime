package com.kotlin.android.community.family.component.ui.manage.adapter

import com.kotlin.android.app.data.entity.community.group.GroupSection
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemFamilySectionManagerBinding
import com.kotlin.android.community.family.component.ui.manage.widget.EditDialog
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.showDialog

/**
 * @des 家族分区管理item
 * @author zhangjian
 * @date 2022/4/11 19:05
 */
class FamilySectionManagerItemBinder(
    val data: GroupSection,
    var editor: ((sectionId: Long, sectionName: String) -> Unit),
    var del: ((sectionId: Long) -> Unit)
) :
    MultiTypeBinder<ItemFamilySectionManagerBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_family_section_manager
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FamilySectionManagerItemBinder
    }

    override fun onBindViewHolder(binding: ItemFamilySectionManagerBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //设置分区名称
        binding.tvName.text = data.name
        //设置分区编辑按钮
        binding.tvEditor.setBackground(
            colorRes = R.color.color_ffffff,
            strokeWidth = 1.dp,
            strokeColorRes = R.color.color_1cacde,
            cornerRadius = 15.dpF
        )

        //设置分区删除按钮
        binding.tvDel.setBackground(
            colorRes = R.color.color_f5f5f5,
            strokeWidth = 1.dp,
            strokeColorRes = R.color.color_f5f5f5,
            cornerRadius = 15.dpF
        )

        //删除点击
        binding.tvDel.onClick {
            showDialog(
                context = it.context,
                title = getString(R.string.family_delete_dialog_title),
                content = getString(R.string.family_delete_dialog_content),
                positiveAction = {
                    del.invoke(data.sectionId ?: 0L)
                },
                negativeAction = {

                })

        }

        //编辑点击事件
        binding.tvEditor.onClick {
            val dialog = EditDialog(context = it.context,R.style.common_dialog,data.name)
            dialog.apply {
                addClick = {
                    editor.invoke(data.sectionId?:0L,it)
                    this.dismiss()
                }
                cancelClick = {
                    this.dismiss()
                }
                create()
                show()
            }
        }

    }
}